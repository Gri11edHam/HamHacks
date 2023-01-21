package net.grilledham.hamhacks.util;

import net.grilledham.hamhacks.util.math.Vec3;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class ProjectionUtil {
	
	private static final Vector4f vec4 = new Vector4f();
	private static final Vector4f modelVec = new Vector4f();
	private static final Vector4f projectionModelVec = new Vector4f();
	private static final Vec3 camPos = new Vec3();
	private static Matrix4f model;
	private static Matrix4f projection;
	private static double windowScale;
	
	public static double scale;
	
	private static final MinecraftClient mc = MinecraftClient.getInstance();
	
	public static void updateMatrices(MatrixStack stack, Matrix4f newProjection) {
		model = new Matrix4f(stack.peek().getPositionMatrix());
		projection = newProjection;
		
		camPos.set(mc.gameRenderer.getCamera().getPos());
		
		windowScale = mc.getWindow().calculateScaleFactor(1, false);
	}
	
	public static boolean to2D(Vec3 pos, double scale, boolean distanceScaling) {
		ProjectionUtil.scale = distanceScaling ? getScale(pos) * scale : scale;
		
		vec4.set(pos.getX() - camPos.getX(), pos.getY() - camPos.getY(), pos.getZ() - camPos.getZ(), 1);
		
		vec4.mul(model, modelVec);
		modelVec.mul(projection, projectionModelVec);
		
		if(projectionModelVec.w() <= 0) {
			return false;
		}
		
		double W = 1 / projectionModelVec.w() * 0.5;
		projectionModelVec.set(
				projectionModelVec.x() * W + 0.5,
				projectionModelVec.y() * W + 0.5,
				projectionModelVec.z() * W + 0.5,
				W
		);
		
		double x = projectionModelVec.x() * mc.getWindow().getFramebufferWidth();
		double y = projectionModelVec.y() * mc.getWindow().getFramebufferHeight();
		if(Double.isInfinite(x) || Double.isInfinite(y)) {
			return false;
		}
		pos.set(x / windowScale, mc.getWindow().getFramebufferHeight() - y / windowScale, projectionModelVec.z());
		return true;
	}
	
	private static double getScale(Vec3 pos) {
		double dist = camPos.dist(pos);
		return MathHelper.clamp(1 - dist * 0.01, 0.5, Integer.MAX_VALUE);
	}
}
