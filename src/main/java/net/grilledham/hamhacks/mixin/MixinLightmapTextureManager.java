package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.modules.render.Fullbright;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapTextureManager.class)
public class MixinLightmapTextureManager {
	
	private float delta = 0;
	
	@Inject(method = "update", at = @At("HEAD"))
	private void getTickDelta(float delta, CallbackInfo ci) {
		this.delta = delta;
	}
	
	@ModifyVariable(method = "update", at = @At(value = "STORE", ordinal = 1), index = 21)
	private float overwriteGamma(float original) {
		return Fullbright.getInstance().getBrightness(original, delta);
	}
}
