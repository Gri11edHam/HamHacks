package net.grilledham.hamhacks.util.math;

public class Vec4 {
	
	private double x;
	private double y;
	private double z;
	private double w;
	
	public Vec4(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public Vec4() {
		this(0, 0, 0, 0);
	}
	
	public void set(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public double getW() {
		return w;
	}
	
	public void toScreen() {
		double W = 1 / w * 0.5;
		x = x * W + 0.5;
		y = y * W + 0.5;
		z = z * W + 0.5;
		w = W;
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ", " + w + ")";
	}
}
