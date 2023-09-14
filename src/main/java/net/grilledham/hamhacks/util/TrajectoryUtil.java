package net.grilledham.hamhacks.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class TrajectoryUtil {
	
	private static final float GRAVITY = 0.006f;
	
	private static final MinecraftClient mc = MinecraftClient.getInstance();
	
	public static Vec2f getAngle(Vec3d pos, Vec3d target, float velocity) {
		Vec3d dist = target.subtract(pos);
		
		double hDist = Math.sqrt(dist.x * dist.x + dist.z * dist.z);
		double hDistSq = hDist * hDist;
		float velSq = velocity * velocity;
		float pitch = (float)-Math.toDegrees(Math.atan((velSq - Math.sqrt(velSq * velSq - GRAVITY * (GRAVITY * hDistSq + 2 * dist.y * velSq))) / (GRAVITY * hDist)));
		float yaw = (float)Math.toDegrees(Math.atan2(dist.z, dist.x)) - 90F;
		
		return new Vec2f(yaw, pitch);
	}
}
