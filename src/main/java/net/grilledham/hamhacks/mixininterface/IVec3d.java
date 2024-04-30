package net.grilledham.hamhacks.mixininterface;

import net.minecraft.util.math.Vec3d;

public interface IVec3d {
	
	void hamHacks$setX(double x);
	void hamHacks$setY(double y);
	void hamHacks$setZ(double z);
	
	void hamHacks$set(double x, double y, double z);
	
	void hamHacks$set(Vec3d other);
}
