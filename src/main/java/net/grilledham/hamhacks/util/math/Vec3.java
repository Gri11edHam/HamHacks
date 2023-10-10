package net.grilledham.hamhacks.util.math;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class Vec3 {
	
	private double x;
	private double y;
	private double z;
	
	public Vec3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vec3(Vec3 pos) {
		this(pos.x, pos.y, pos.z);
	}
	
	public Vec3(Vec3d pos) {
		this(pos.x, pos.y, pos.z);
	}
	
	public Vec3() {
		this(0, 0, 0);
	}
	
	public void set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void set(Vec3 pos) {
		set(pos.x, pos.y, pos.z);
	}
	
	public void set(Vec3d pos) {
		set(pos.x, pos.y, pos.z);
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public void setZ(double z) {
		this.z = z;
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
	
	public Vec3 add(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}
	
	public Vec3 add(Vec3 pos) {
		return add(pos.x, pos.y, pos.z);
	}
	
	public Vec3 sub(double x, double y, double z) {
		return add(-x, -y, -z);
	}
	
	public Vec3 sub(Vec3 pos) {
		return sub(pos.x, pos.y, pos.z);
	}
	
	public Vec3 mul(double x, double y, double z) {
		this.x *= x;
		this.y *= y;
		this.z *= z;
		return this;
	}
	
	public Vec3 mul(Vec3 pos) {
		return mul(pos.x, pos.y, pos.z);
	}
	
	public Vec3 mul(double d) {
		return mul(d, d, d);
	}
	
	public Vec3 div(double x, double y, double z) {
		return mul(1 / x, 1 / y, 1 / z);
	}
	
	public Vec3 div(Vec3 pos) {
		return div(pos.x, pos.y, pos.z);
	}
	
	public Vec3 div(double d) {
		return div(d, d, d);
	}
	
	public double dist(double x, double y, double z) {
		double dx = x - this.x;
		double dy = y - this.y;
		double dz = z - this.z;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
	
	public double dist(Vec3 pos) {
		return dist(pos.x, pos.y, pos.z);
	}
	
	public Vec3d get() {
		return new Vec3d(x, y, z);
	}
	
	public Vec3 copy() {
		return new Vec3(this);
	}
	
	public static Vec3 center(BlockPos pos) {
		return new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Vec3 vec3 = (Vec3)o;
		return Double.compare(vec3.x, x) == 0 && Double.compare(vec3.y, y) == 0 && Double.compare(vec3.z, z) == 0;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x, y, z);
	}
}
