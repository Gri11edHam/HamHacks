package net.grilledham.hamhacks.mixin.continuity;

import me.pepperbell.continuity.client.model.CullingCache;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.render.XRay;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CullingCache.class)
public class MixinCullingCache {
	
	@Inject(method = "shouldCull(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;)Z", at = @At("RETURN"), cancellable = true)
	public void shouldCull(BlockRenderView blockView, BlockPos pos, BlockState state, Direction cullFace, CallbackInfoReturnable<Boolean> cir) {
		XRay xRay = ModuleManager.getModule(XRay.class);
		if(xRay.isEnabled()) {
			cir.setReturnValue(xRay.shouldCullSide(state, blockView, pos, cullFace, cir.getReturnValue()));
		}
	}
	
	@Inject(method = "shouldCull(Lnet/fabricmc/fabric/api/renderer/v1/mesh/QuadView;Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z", at = @At("RETURN"), cancellable = true)
	public void shouldCull(QuadView quad, BlockRenderView blockView, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		XRay xRay = ModuleManager.getModule(XRay.class);
		if(xRay.isEnabled()) {
			cir.setReturnValue(xRay.shouldCullSide(state, blockView, pos, quad.cullFace(), cir.getReturnValue()));
		}
	}
}
