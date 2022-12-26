package net.grilledham.hamhacks.mixin.sodium;

import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import me.jellysquid.mods.sodium.client.render.pipeline.BlockRenderer;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.render.XRay;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockRenderer.class, remap = false)
public class MixinBlockRenderer {
	
	@Inject(method = "renderModel", at = @At("HEAD"), cancellable = true)
	private void renderModel(BlockRenderView world, BlockState state, BlockPos pos, BlockPos origin, BakedModel model, ChunkModelBuilder buffers, boolean cull, long seed, CallbackInfoReturnable<Boolean> cir) {
		XRay xRay = ModuleManager.getModule(XRay.class);
		if(xRay.isEnabled() && !xRay.visibleBlocks.get(state.getBlock())) {
			cir.setReturnValue(false);
		}
	}
}
