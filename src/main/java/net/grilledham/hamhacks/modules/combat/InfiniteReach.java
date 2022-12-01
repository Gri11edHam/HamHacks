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
import net.minecraft.util.math.*;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class InfiniteReach extends Module {
	
	private HitResult hitResult = BlockHitResult.createMissed(null, null, null);
	
	private final List<Vec3> teleports = new ArrayList<>();
	
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
		
		List<Vec3> initialPath = new PathFinder().findPath(mc.player.getBlockPos(), new BlockPos(hitResult.getPos()), mc.player.world, 4);
		if(initialPath == null || initialPath.isEmpty()) {
			return;
		}
		List<Vec3> path = new ArrayList<>();
		Vec3 lastVec = null;
		Vec3 lastDir = null;
		for(Vec3 vec : initialPath) {
			if(vec.dist(new Vec3(hitResult.getPos())) <= 5) {
				path.add(vec);
				break;
			}
			if(lastVec == null) {
				lastVec = vec;
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
			if(!dir.equals(lastDir) || vec.dist(lastVec) >= 6) {
				path.add(vec);
				lastVec = vec;
			}
			lastDir = dir;
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
	
	public HitResult getHitResultOrdinal(HitResult result) {
		if(!enabled.get() || hitResult.getType() != HitResult.Type.ENTITY) return result;
		return hitResult;
	}
	
	public void preAttack() {
		if(!enabled.get()) return;
		if(hitResult.getType() == HitResult.Type.ENTITY) {
			teleports.clear();
			EntityHitResult result = (EntityHitResult)hitResult;
			Vec3d playerPos = mc.player.getPos();
			double diffX = result.getEntity().getPos().x - playerPos.x;
			double diffY = result.getEntity().getPos().y - playerPos.y;
			double diffZ = result.getEntity().getPos().z - playerPos.z;
			double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
			float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
			float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));
			float f = MathHelper.cos(-yaw * 0.017453292F - 3.1415927F);
			float g = MathHelper.sin(-yaw * 0.017453292F - 3.1415927F);
			float h = -MathHelper.cos(-pitch * 0.017453292F);
			float i = MathHelper.sin(-pitch * 0.017453292F);
			Vec3 facing = new Vec3(g * h, i, f * h);
			facing.mul(4);
			Vec3 pos = new Vec3(mc.player.getPos());
			Vec3 end = new Vec3(result.getPos());
			while(pos.dist(end) > 8) {
				pos.add(facing);
				final boolean[] collides = {false};
				mc.world.getBlockCollisions(mc.player, mc.player.getBoundingBox(mc.player.getPose())).forEach((a) -> collides[0] = true);
				teleports.add(0, pos.copy());
				mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.getX(), pos.getY(), pos.getZ(), mc.player.isOnGround()));
			}
			facing.div(4);
			while(pos.dist(end) > 3) {
				pos.add(facing);
				teleports.add(0, pos.copy());
				mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.getX(), pos.getY(), pos.getZ(), mc.player.isOnGround()));
			}
//			mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(result.getEntity().getX(), result.getEntity().getY(), result.getEntity().getZ(), mc.player.isOnGround()));
		}
	}
	
	public void postAttack() {
		if(!enabled.get()) return;
		if(hitResult.getType() == HitResult.Type.ENTITY) {
			for(Vec3 pos : teleports) {
				mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.getX(), pos.getY(), pos.getZ(), mc.player.isOnGround()));
			}
			mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.isOnGround()));
		}
	}
}
