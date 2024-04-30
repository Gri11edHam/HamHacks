package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.mixininterface.ICamera;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Camera.class)
public abstract class MixinCamera implements ICamera {
	
	@Shadow protected abstract void setPos(Vec3d pos);
	
	@Shadow protected abstract void setRotation(float yaw, float pitch);
	
	@Override
	public void hamHacks$setCamPos(Vec3d pos) {
		setPos(pos);
	}
	
	@Override
	public void hamHacks$setCamRot(float yaw, float pitch) {
		setRotation(yaw, pitch);
	}
}
