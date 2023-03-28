package net.grilledham.hamhacks.mixin.sodium;

import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.render.XRay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockRenderer.class, remap = false)
public class MixinBlockRenderer {
	
	@Inject(method = "renderModel", at = @At("HEAD"), cancellable = true)
	private void renderModel(BlockRenderContext ctx, ChunkModelBuilder buffers, CallbackInfoReturnable<Boolean> cir) {
		XRay xRay = ModuleManager.getModule(XRay.class);
		if(xRay.isEnabled() && !xRay.visibleBlocks.get(ctx.state().getBlock())) {
			cir.setReturnValue(false);
		}
	}
}
