package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.mixininterface.IRenderTickCounter;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RenderTickCounter.class)
public class MixinRenderTickCounter implements IRenderTickCounter {
	
	@Mutable
	@Shadow @Final private float tickTime;
	
	@Override
	public void hamHacks$setTPS(float tps) {
		tickTime = 1000 / tps;
	}
	
	@Override
	public float hamHacks$getTPS() {
		return 1000 / tickTime;
	}
}
