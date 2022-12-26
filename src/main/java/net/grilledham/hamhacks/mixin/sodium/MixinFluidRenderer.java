package net.grilledham.hamhacks.mixin.sodium;

import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import me.jellysquid.mods.sodium.client.render.pipeline.FluidRenderer;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.render.XRay;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FluidRenderer.class, remap = false)
public class MixinFluidRenderer {

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void render(BlockRenderView world, FluidState fluidState, BlockPos pos, BlockPos offset, ChunkModelBuilder buffers, CallbackInfoReturnable<Boolean> cir) {
		XRay xRay = ModuleManager.getModule(XRay.class);
		if(xRay.isEnabled() && !xRay.visibleBlocks.get(fluidState.getBlockState().getBlock())) {
			cir.setReturnValue(false);
		}
	}
}
