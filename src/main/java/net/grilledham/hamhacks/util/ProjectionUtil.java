package net.grilledham.hamhacks.util;

import net.grilledham.hamhacks.mixininterface.IMatrix4f;
import net.grilledham.hamhacks.util.math.Vec3;
import net.grilledham.hamhacks.util.math.Vec4;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

public class ProjectionUtil {
	
	private static final Vec4 vec4 = new Vec4();
	private static final Vec4 modelVec = new Vec4();
	private static final Vec4 projectionModelVec = new Vec4();
	private static final Vec3 camPos = new Vec3();
	private static Matrix4f model;
	private static Matrix4f projection;
	private static double windowScale;
	
	public static double scale;
	
	private static final MinecraftClient mc = MinecraftClient.getInstance();
	
	public static void updateMatrices(MatrixStack stack, Matrix4f newProjection) {
		model = stack.peek().getPositionMatrix().copy();
		projection = newProjection;
		
		camPos.set(mc.gameRenderer.getCamera().getPos());
		
		windowScale = mc.getWindow().calculateScaleFactor(1, false);
	}
	
	public static boolean to2D(Vec3 pos, float scale) {
		ProjectionUtil.scale = getScale(pos) * scale;
		
		vec4.set(pos.getX() - camPos.getX(), pos.getY() - camPos.getY(), pos.getZ() - camPos.getZ(), 1);
		
		((IMatrix4f)(Object)model).multiply(vec4, modelVec);
		((IMatrix4f)(Object)projection).multiply(modelVec, projectionModelVec);
		
		if(projectionModelVec.getW() <= 0) {
			return false;
		}
		
		projectionModelVec.toScreen();
		
		double x = projectionModelVec.getX() * mc.getWindow().getFramebufferWidth();
		double y = projectionModelVec.getY() * mc.getWindow().getFramebufferHeight();
		if(Double.isInfinite(x) || Double.isInfinite(y)) {
			return false;
		}
		pos.set(x / windowScale, mc.getWindow().getFramebufferHeight() - y / windowScale, projectionModelVec.getZ());
		return true;
	}
	
	private static double getScale(Vec3 pos) {
		double dist = camPos.dist(pos);
		return MathHelper.clamp(1 - dist * 0.01, 0.5, Integer.MAX_VALUE);
	}
}
