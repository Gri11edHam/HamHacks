package net.grilledham.hamhacks.mixininterface;

import net.minecraft.util.math.Vec3d;

public interface ICamera {
	
	void setCamPos(Vec3d pos);
	
	void setCamRot(float yaw, float pitch);
}
