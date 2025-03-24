package net.grilledham.hamhacks.modules.combat;

import com.mojang.blaze3d.systems.RenderSystem;
import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventClick;
import net.grilledham.hamhacks.event.events.EventRender3D;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.pathfinding.PathFinder;
import net.grilledham.hamhacks.setting.BoolSetting;
import net.grilledham.hamhacks.setting.ColorSetting;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.grilledham.hamhacks.util.Color;
import net.grilledham.hamhacks.util.PlayerUtil;
import net.grilledham.hamhacks.util.math.Vec3;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class InfiniteReach extends Module {
	
	private HitResult hitResult = BlockHitResult.createMissed(null, null, null);
	
	private PathFinder pathFinder;
	
	private final List<Vec3> pathRemaining = new ArrayList<>();
	private final List<Vec3> returnPathRemaining = new ArrayList<>();
	private int pathState = 0;
	
	public final BoolSetting pathPreview = new BoolSetting("hamhacks.module.infiniteReach.pathPreview", true, () -> true);
	
	public final ColorSetting previewColor = new ColorSetting("hamhacks.module.infiniteReach.previewColor", new Color(0x80FF0000), pathPreview::get);
	
	public final NumberSetting timeout = new NumberSetting("hamhacks.module.infiniteReach.timeout", 5, () -> true, 0, 60, 1, false);
	
	public InfiniteReach() {
		super(Text.translatable("hamhacks.module.infiniteReach"), Category.COMBAT, new Keybind());
		GENERAL_CATEGORY.add(pathPreview);
		GENERAL_CATEGORY.add(previewColor);
		GENERAL_CATEGORY.add(timeout);
	}
	
	@Override
	public String getHUDText() {
		String extra = "";
		if(hitResult.getType() == HitResult.Type.ENTITY && hitResult != null && mc.world != null) {
			extra = "(" + ((EntityHitResult)hitResult).getEntity().getName().getString() + "|" + String.format("%.2f", Math.sqrt(hitResult.squaredDistanceTo(mc.player))) + ")";
		}
		if(pathFinder != null) {
			extra += String.format(" Pathing(%.2f)", pathFinder.getExecutionTime() / 1000D);
		}
		return super.getHUDText() + " \u00a77" + hitResult.getType().name() + extra;
	}
	
	@EventListener
	public void tick(EventTick e) {
		if(!pathRemaining.isEmpty() || !returnPathRemaining.isEmpty()) {
			Vec3 lastPos = null;
			Vec3 startPos = pathState == 0 ? pathRemaining.getFirst() : returnPathRemaining.getFirst();
			Vec3 pos;
			if(pathState == 0) {
				pathState = 1;
				while(!pathRemaining.isEmpty()) {
					pos = pathRemaining.getFirst();
					if(lastPos != null) {
						if(startPos.dist(pos) >= 12) {
							pathState = 0;
							break;
						}
					}
					mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.getX(), pos.getY(), pos.getZ(), mc.player.isOnGround(), mc.player.horizontalCollision));
					pathRemaining.remove(pos);
					lastPos = pos;
				}
				mc.player.setPosition(lastPos.get());
			}
			if(pathState == 1) {
				imc.hamHacks$getInteractionManager().hamHacks$leftClickEntity(((EntityHitResult)hitResult).getEntity());
				mc.player.swingHand(Hand.MAIN_HAND);
//				mc.world.playSound(mc.player, mc.player.getX(), mc.player.getY(), mc.player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, SoundCategory.PLAYERS, 1, 1, Random.newSeed());
				pathState = 2;
			}
			if(pathState == 2) {
				while(!returnPathRemaining.isEmpty()) {
					pos = returnPathRemaining.getFirst();
					if(lastPos != null) {
						if(startPos.dist(pos) >= 12) {
							break;
						}
					}
					mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.getX(), pos.getY(), pos.getZ(), mc.player.isOnGround(), mc.player.horizontalCollision));
					returnPathRemaining.remove(pos);
					lastPos = pos;
				}
				mc.player.setPosition(lastPos.get());
			}
		}
	}
	
	@EventListener
	public void render(EventRender3D e) {
		if(!pathRemaining.isEmpty()) {
			return;
		}
		hitResult = PlayerUtil.hitResult(100, e.tickDelta);
		if(pathFinder != null && pathPreview.get()) {
			PathFinder.Node node = pathFinder.getPath();
			if(node == null || node.parent == null) {
				return;
			}
			
			MatrixStack matrixStack = e.matrices;
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			
			matrixStack.push();
			
			matrixStack.loadIdentity();
			
			Vec3d camPos = mc.getBlockEntityRenderDispatcher().camera.getPos();
			Quaternionf q = new Quaternionf();
			q.rotateXYZ((float)Math.toRadians(mc.getBlockEntityRenderDispatcher().camera.getPitch()), (float)Math.toRadians((mc.getBlockEntityRenderDispatcher().camera.getYaw()) % 360 + 180), 0);
			matrixStack.peek().getPositionMatrix().rotate(q);
			matrixStack.translate(-camPos.x, -camPos.y, -camPos.z);
			
			RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
			RenderSystem.setShaderColor(1, 1, 1, 1);
			
			Matrix4f matrix = matrixStack.peek().getPositionMatrix();
			
			GL11.glDisable(GL11.GL_CULL_FACE);
			
			BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
			
			while(node != null) {
				BlockPos pos = node.pos;
				bufferBuilder.vertex(matrix, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F).color(previewColor.get().getRGB());
				node = node.parent;
			}
			
			BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
			
			matrixStack.pop();
			
			RenderSystem.setShaderColor(1, 1, 1, 1);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_LINE_SMOOTH);
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
		if(pathFinder != null) {
			pathFinder.cancel();
		}
		pathFinder = new PathFinder().path(mc.player.getBlockPos(), BlockPos.ofFloored(hitResult.getPos()), mc.player.clientWorld, 3).setTimeout((long)(timeout.get() * 1000)).whenDone((initialPath) -> {
			if(initialPath == null || initialPath.isEmpty()) {
				pathFinder = null;
				return;
			}
			List<Vec3> path = new ArrayList<>();
			Vec3 lastTP = null;
			Vec3 lastVec = null;
			Vec3 lastDir = null;
			for(Vec3 vec : initialPath) {
				if(vec == initialPath.getLast()) {
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
			pathRemaining.clear();
			pathRemaining.addAll(path);
			returnPathRemaining.clear();
			returnPathRemaining.addAll(path.reversed());
			pathState = 0;
			pathFinder = null;
		}).begin();
	}
}
