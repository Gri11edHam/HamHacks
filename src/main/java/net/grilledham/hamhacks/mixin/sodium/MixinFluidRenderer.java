package net.grilledham.hamhacks.mixin.sodium;

import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.FluidRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.data.ChunkRenderBounds;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.render.XRay;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FluidRenderer.class, remap = false)
public class MixinFluidRenderer {

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void render(BlockRenderView world, FluidState fluidState, BlockPos pos, BlockPos offset, ChunkBuildBuffers buffers, ChunkRenderBounds.Builder bounds, CallbackInfo ci) {
		XRay xRay = ModuleManager.getModule(XRay.class);
		if(xRay.isEnabled() && !xRay.visibleBlocks.get(fluidState.getBlockState().getBlock())) {
			ci.cancel();
		}
	}
}
