package net.grilledham.hamhacks.modules.movement;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventRender3D;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.pathfinding.PathFinder;
import net.grilledham.hamhacks.setting.KeySetting;
import net.grilledham.hamhacks.util.math.Vec3;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;
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

public class ClickTP extends Module {
	
	private HitResult hitResult = BlockHitResult.createMissed(null, null, null);
	
	private PathFinder pathFinder;
	
	public final KeySetting activate = new KeySetting("hamhacks.module.clickTP.activate", new Keybind(GLFW.GLFW_MOUSE_BUTTON_RIGHT - Keybind.MOUSE_SHIFT), () -> true);
	
	public ClickTP() {
		super(Text.translatable("hamhacks.module.clickTP"), Category.MOVEMENT, new Keybind());
		GENERAL_CATEGORY.add(activate);
	}
	
	@Override
	public String getHUDText() {
		String extra = "";
		if(hitResult.getType() == HitResult.Type.ENTITY && hitResult != null && mc.world != null) {
			extra = "(" + ((EntityHitResult)hitResult).getEntity().getName().getString() + "|" + String.format("%.2f", Math.sqrt(hitResult.squaredDistanceTo(mc.player))) + ") ";
		} else if(hitResult.getType() == HitResult.Type.BLOCK && hitResult != null && mc.world != null) {
			extra = "(" + mc.world.getBlockState(((BlockHitResult)hitResult).getBlockPos()).getBlock().getName().getString() + "|" + String.format("%.2f", Math.sqrt(hitResult.squaredDistanceTo(mc.player))) + ") ";
		}
		if(pathFinder != null) {
			extra += String.format("Pathing(%.2f)", pathFinder.getExecutionTime() / 1000D);
		}
		return super.getHUDText() + " \u00a77" + hitResult.getType().name() + extra.trim();
	}
	
	@EventListener
	public void tick(EventTick e) {
		if(mc.currentScreen != null || hitResult == null || hitResult.getType() == HitResult.Type.MISS) {
			return;
		}
		boolean pressed = false;
		while(activate.get().wasPressed()) {
			pressed = true;
		}
		if(pressed) doTeleport(hitResult);
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
				hitResult = BlockHitResult.createMissed(vec3d4, Direction.getFacing(vec3d2.x, vec3d2.y, vec3d2.z), BlockPos.ofFloored(vec3d4));
			} else if (g < d1 || hitResult == null) {
				hitResult = entityHitResult;
			}
		}
	}
	
	private void doTeleport(HitResult hitResult) {
		BlockPos endPos = BlockPos.ofFloored(hitResult.getPos());
		if(hitResult.getType() == HitResult.Type.BLOCK) {
			BlockPos above = ((BlockHitResult)hitResult).withSide(((BlockHitResult)hitResult).getSide().getOpposite()).getBlockPos().up();
			if(mc.world.getBlockState(above).getCollisionShape(mc.world, above).isEmpty() && mc.world.getBlockState(above.up()).getCollisionShape(mc.world, above.up()).isEmpty()) {
				endPos = above;
			} else if(!mc.world.getBlockState(endPos.up()).getCollisionShape(mc.world, endPos.up()).isEmpty() || !mc.world.getBlockState(endPos).getCollisionShape(mc.world, endPos).isEmpty()) {
				endPos = endPos.down();
				if(!mc.world.getBlockState(endPos.up()).getCollisionShape(mc.world, endPos.up()).isEmpty() || !mc.world.getBlockState(endPos).getCollisionShape(mc.world, endPos).isEmpty()) {
					endPos = endPos.down();
					if(!mc.world.getBlockState(endPos.up()).getCollisionShape(mc.world, endPos.up()).isEmpty() || !mc.world.getBlockState(endPos).getCollisionShape(mc.world, endPos).isEmpty()) {
						return;
					}
				}
			}
		}
		if(pathFinder != null) {
			pathFinder.cancel();
		}
		pathFinder = new PathFinder().path(mc.player.getBlockPos(), endPos, mc.player.clientWorld).setTimeout(5000L).whenDone((initialPath) -> {
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
			for(Vec3 pos : path) {
				mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.getX(), pos.getY(), pos.getZ(), mc.player.isOnGround()));
			}
			mc.player.setPosition(path.getLast().get());
			pathFinder = null;
		}).begin();
	}
}
