package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.render.XRay;
import net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkOcclusionDataBuilder.class)
public class MixinChunkOcclusionDataBuilder {

	@Inject(method = "markClosed", at = @At("HEAD"), cancellable = true)
	private void markClosed(BlockPos pos, CallbackInfo ci) {
		XRay xRay = ModuleManager.getModule(XRay.class);
		if(xRay.isEnabled()) {
			ci.cancel();
		}
	}
}
