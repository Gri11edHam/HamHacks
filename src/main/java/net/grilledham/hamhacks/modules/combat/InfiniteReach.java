package net.grilledham.hamhacks.modules.combat;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventClick;
import net.grilledham.hamhacks.event.events.EventRender3D;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.pathfinding.PathFinder;
import net.grilledham.hamhacks.util.math.Vec3;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class InfiniteReach extends Module {
	
	private HitResult hitResult = BlockHitResult.createMissed(null, null, null);
	
	public InfiniteReach() {
		super(Text.translatable("hamhacks.module.infiniteReach"), Category.COMBAT, new Keybind());
	}
	
	@Override
	public String getHUDText() {
		String extra = "";
		if(hitResult.getType() == HitResult.Type.ENTITY) {
			extra = "(" + ((EntityHitResult)hitResult).getEntity().getName().getString() + "|" + String.format("%.2f", Math.sqrt(hitResult.squaredDistanceTo(mc.player))) + ")";
		}
		return super.getHUDText() + " \u00a77" + hitResult.getType().name() + extra;
	}
	
	@EventListener
	public void render(EventRender3D e) {
		hitResult = null;
		double d = 100;
		hitResult = mc.player.raycast(d, e.tickDelta, false);
		Vec3d vec3d = mc.player.getCameraPosVec(e.tickDelta);
		double d1 = d;
		d1 *= d1;
		
		Vec3d vec3d2 = mc.player.getRotationVec(1.0F);
		Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
		Box box = mc.player.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0, 1.0, 1.0);
		EntityHitResult entityHitResult = ProjectileUtil.raycast(mc.player, vec3d, vec3d3, box, (entityx) -> !entityx.isSpectator() && entityx.canHit(), d1);
		if (entityHitResult != null) {
			Vec3d vec3d4 = entityHitResult.getPos();
			double g = vec3d.squaredDistanceTo(vec3d4);
			if (g > 100 * 100) {
				hitResult = BlockHitResult.createMissed(vec3d4, Direction.getFacing(vec3d2.x, vec3d2.y, vec3d2.z), new BlockPos(vec3d4));
			} else if (g < d1 || hitResult == null) {
				hitResult = entityHitResult;
			}
		}
	}
	
	@EventListener
	public void onClick(EventClick e) {
		if(mc.currentScreen != null || e.button != 0 || hitResult == null || hitResult.getType() != HitResult.Type.ENTITY) {
			return;
		}
		
		if(GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), e.button) != GLFW.GLFW_PRESS) {
			return;
		}
		e.canceled = true;
		
		doAttack(hitResult);
	}
	
	public void doAttack(HitResult hitResult) {
		List<Vec3> initialPath = new PathFinder().findPath(mc.player.getBlockPos(), new BlockPos(hitResult.getPos()), mc.player.world, 4);
		if(initialPath == null || initialPath.isEmpty()) {
			return;
		}
		List<Vec3> path = new ArrayList<>();
		Vec3 lastTP = null;
		Vec3 lastVec = null;
		Vec3 lastDir = null;
		for(Vec3 vec : initialPath) {
			if(vec.dist(new Vec3(hitResult.getPos())) <= 5) {
				path.add(vec);
				break;
			}
			if(lastVec == null) {
				lastVec = vec;
				lastTP = vec;
				lastDir = new Vec3();
				continue;
			}
			Vec3 dir = lastVec.copy().sub(vec);
			if(dir.getX() > 0) {
				dir.setX(1);
			} else if(dir.getX() < 0) {
				dir.setX(-1);
			}
			if(dir.getY() > 0) {
				dir.setY(1);
			} else if(dir.getY() < 0) {
				dir.setY(-1);
			}
			if(dir.getZ() > 0) {
				dir.setZ(1);
			} else if(dir.getZ() < 0) {
				dir.setZ(-1);
			}
			if(!dir.equals(lastDir)) {
				path.add(lastVec);
				lastTP = lastVec;
			} else if(vec.dist(lastTP) >= 6) {
				path.add(vec);
				lastTP = vec;
			}
			lastDir = dir;
			lastVec = vec;
		}
		for(Vec3 pos : path) {
			mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.getX(), pos.getY(), pos.getZ(), mc.player.isOnGround()));
		}
		imc.getInteractionManager().leftClickEntity(((EntityHitResult)hitResult).getEntity());
		mc.player.swingHand(Hand.MAIN_HAND);
		for(int i = path.size() - 1; i >= 0; i--) {
			Vec3 pos = path.get(i);
			mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.getX(), pos.getY(), pos.getZ(), mc.player.isOnGround()));
		}
	}
}
