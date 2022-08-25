package net.grilledham.hamhacks.mixininterface;

import net.minecraft.client.render.Camera;

public interface IGameRenderer {
	
	double getFOV(Camera camera, float tickDelta, boolean changingFov);
}
