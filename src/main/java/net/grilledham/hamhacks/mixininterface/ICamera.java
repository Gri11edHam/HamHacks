package net.grilledham.hamhacks.mixininterface;

import net.minecraft.util.math.Vec3d;

public interface ICamera {
	
	void hamHacks$setCamPos(Vec3d pos);
	
	void hamHacks$setCamRot(float yaw, float pitch);
}
