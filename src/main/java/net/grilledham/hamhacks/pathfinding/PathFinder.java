package net.grilledham.hamhacks.pathfinding;

import net.grilledham.hamhacks.util.math.Vec3;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class PathFinder {
	
	private final Node[][][] nodes = new Node[512][512][512];
	private Node start;
	private Node startNode;
	private Node endNode;
	private World world;
	private float endDist;
	
	private Consumer<List<Vec3>> whenDone;
	
	private long timeout = -1;
	
	private boolean canceled = false;
	
	private long executionTime = 0;
	
	public PathFinder path(BlockPos start, BlockPos end, World world) {
		return path(start, end, world, 1);
	}
	
	public PathFinder path(BlockPos start, BlockPos end, World world, float endDist) {
		startNode = getStart(start);
		endNode = getNode(end);
		this.world = world;
		this.endDist = endDist;
		return this;
	}
	
	public PathFinder setTimeout(long timeout) {
		this.timeout = timeout;
		return this;
	}
	
	public PathFinder whenDone(Consumer<List<Vec3>> whenDone) {
		this.whenDone = whenDone;
		return this;
	}
	
	public PathFinder begin() {
		Thread thread = new Thread("PathFinder") {
			@Override
			public void run() {
				long startTime = System.currentTimeMillis();
				if(endNode == null) {
					whenDone.accept(new ArrayList<>());
					return;
				}
				
				startNode.g = 0;
				startNode.h = h(startNode, endNode);
				startNode.f = 0;
				
				List<Node> openList = new ArrayList<>();
				
				openList.add(startNode);
				startNode.opened = true;
				
				Node node;
				List<Node> neighbors;
				while(!openList.isEmpty() && ((executionTime = System.currentTimeMillis() - startTime) < timeout || timeout <= -1) && !canceled) {
					node = openList.get(0);
					openList.remove(node);
					node.closed = true;
					
					if(node.pos.isWithinDistance(endNode.pos, endDist)) {
						whenDone.accept(backtrace(node));
						return;
					}
					
					neighbors = getNeighbors(node, world);
					for(Node neighbor : neighbors) {
						if(neighbor.closed) {
							continue;
						}
						
						float ng = node.g + 1;
						
						if(!neighbor.opened || ng < neighbor.g) {
							neighbor.g = ng;
							neighbor.h = h(neighbor, endNode);
							neighbor.f = neighbor.g + neighbor.h;
							neighbor.parent = node;
							
							if(!neighbor.opened) {
								openList.add(neighbor);
								openList.sort((a, b) -> (int)(a.f - b.f));
								neighbor.opened = true;
							} else {
								openList.sort((a, b) -> (int)(a.f - b.f));
							}
						}
					}
				}
				whenDone.accept(new ArrayList<>());
			}
		};
		thread.setPriority(3);
		thread.start();
		return this;
	}
	
	public PathFinder cancel() {
		canceled = true;
		return this;
	}
	
	public long getExecutionTime() {
		return executionTime;
	}
	
	private List<Vec3> backtrace(Node endNode) {
		List<Vec3> path = new ArrayList<>();
		Node node = endNode;
		do {
			path.add(0, Vec3.center(node.pos).sub(0, 0.5, 0));
		} while((node = node.parent) != null);
		return path;
	}
	
	private List<Node> getNeighbors(Node node, World world) {
		List<Node> neighbors = new ArrayList<>();
		BlockPos pos = node.pos;
		for(Direction side : Direction.values()) {
			BlockPos neighbor = pos.offset(side);
			BlockPos aboveNeighbor = neighbor.offset(Direction.UP);
			if(world.getBlockState(neighbor).getCollisionShape(world.getChunk(neighbor), neighbor).isEmpty()) {
				if(world.getBlockState(aboveNeighbor).getCollisionShape(world.getChunk(aboveNeighbor), aboveNeighbor).isEmpty()) {
					Node neighborNode = getNode(neighbor);
					if(neighborNode != null) {
						neighbors.add(neighborNode);
					}
				}
			}
		}
		return neighbors;
	}
	
	private float h(Node from, Node to) {
		return from.pos.getManhattanDistance(to.pos);
	}
	
	private Node getStart(BlockPos pos) {
		return nodes[256][256][256] = start = new Node(pos);
	}
	
	private Node getNode(BlockPos pos) {
		int x = pos.getX() - start.pos.getX() + 256;
		int y = pos.getY() - start.pos.getY() + 256;
		int z = pos.getZ() - start.pos.getZ() + 256;
		if(x < 0 || x > 511 || y < 0 || y > 511 || z < 0 || z > 511) {
			return null;
		}
		if(nodes[x][y][z] == null) {
			nodes[x][y][z] = new Node(pos);
		}
		return nodes[x][y][z];
	}
	
	private static class Node {
		
		public Node parent = null;
		
		public boolean opened = false;
		public boolean closed = false;
		
		public float g = Float.POSITIVE_INFINITY;
		public float h = Float.POSITIVE_INFINITY;
		public float f = Float.POSITIVE_INFINITY;
		
		private final BlockPos pos;
		
		public Node(BlockPos pos) {
			this.pos = pos;
		}
		
		@Override
		public boolean equals(Object o) {
			if(this == o) return true;
			if(o == null || getClass() != o.getClass()) return false;
			Node node = (Node)o;
			return node.pos.getX() == pos.getX() && node.pos.getY() == pos.getY() && node.pos.getZ() == pos.getZ();
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(pos);
		}
	}
}
