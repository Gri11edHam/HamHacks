package net.grilledham.hamhacks.util;

import net.grilledham.hamhacks.mixininterface.IVec3d;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.render.Freecam;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class PlayerUtil {
	
	private static final MinecraftClient mc = MinecraftClient.getInstance();
	
	public static HitResult hitResult(double distance, float tickDelta) {
		HitResult result;
		Freecam freecam = ModuleManager.getModule(Freecam.class);
		if(freecam.isEnabled() && freecam.targetMode.get() == 1) {
			Entity entity = mc.getCameraEntity();
			
			Vec3d pos = entity.getPos().multiply(1);
			double prevX = entity.prevX;
			double prevY = entity.prevY;
			double prevZ = entity.prevZ;
			float yaw = entity.getYaw();
			float pitch = entity.getPitch();
			float prevYaw = entity.prevYaw;
			float prevPitch = entity.prevPitch;
			
			((IVec3d)entity.getPos()).hamHacks$set(freecam.pos);
			entity.prevX = freecam.prevPos.x;
			entity.prevY = freecam.prevPos.y;
			entity.prevZ = freecam.prevPos.z;
			entity.setYaw(freecam.yaw);
			entity.setPitch(freecam.pitch);
			entity.prevYaw = freecam.prevYaw;
			entity.prevPitch = freecam.prevPitch;
			
			result = raycast(distance, tickDelta);
			
			((IVec3d)entity.getPos()).hamHacks$set(pos);
			entity.prevX = prevX;
			entity.prevY = prevY;
			entity.prevZ = prevZ;
			entity.setYaw(yaw);
			entity.setPitch(pitch);
			entity.prevYaw = prevYaw;
			entity.prevPitch = prevPitch;
		} else {
			result = raycast(distance, tickDelta);
		}
		return result;
	}
	
	public static HitResult raycast(double distance, float tickDelta) {
		HitResult hitResult;
		hitResult = mc.player.raycast(distance, tickDelta, false);
		Vec3d vec3d = mc.player.getCameraPosVec(tickDelta);
		double distanceSquared = distance;
		distanceSquared *= distanceSquared;
		
		Vec3d vec3d2 = mc.player.getRotationVec(1.0F);
		Vec3d vec3d3 = vec3d.add(vec3d2.x * distance, vec3d2.y * distance, vec3d2.z * distance);
		Box box = mc.player.getBoundingBox().stretch(vec3d2.multiply(distance)).expand(1.0, 1.0, 1.0);
		EntityHitResult entityHitResult = ProjectileUtil.raycast(mc.player, vec3d, vec3d3, box, (entityx) -> !entityx.isSpectator() && entityx.canHit(), distanceSquared);
		if (entityHitResult != null) {
			Vec3d vec3d4 = entityHitResult.getPos();
			double g = vec3d.squaredDistanceTo(vec3d4);
			if (g > distanceSquared) {
				hitResult = BlockHitResult.createMissed(vec3d4, Direction.getFacing(vec3d2.x, vec3d2.y, vec3d2.z), BlockPos.ofFloored(vec3d4));
			} else if (g < distanceSquared || hitResult == null) {
				hitResult = entityHitResult;
			}
		}
		return hitResult;
	}
}
