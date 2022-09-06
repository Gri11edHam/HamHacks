package net.grilledham.hamhacks.mixininterface;

import net.minecraft.util.math.Vec3d;

public interface IVec3d {
	
	void set(double x, double y, double z);
	
	void set(Vec3d other);
}
