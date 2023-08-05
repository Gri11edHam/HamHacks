package net.grilledham.hamhacks.mixin.continuity;

import me.pepperbell.continuity.api.client.ProcessingDataProvider;
import me.pepperbell.continuity.client.processor.BaseProcessingPredicate;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.render.XRay;
import net.minecraft.block.BlockState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BaseProcessingPredicate.class)
public class MixinBaseProcessingPredicate {

	@Inject(method = "shouldProcessQuad", at = @At("RETURN"), cancellable = true)
	public void shouldProcessQuad(QuadView quad, Sprite sprite, BlockRenderView blockView, BlockState state, BlockPos pos, ProcessingDataProvider dataProvider, CallbackInfoReturnable<Boolean> cir) {
		XRay xRay = ModuleManager.getModule(XRay.class);
		if(xRay.isEnabled()) {
			cir.setReturnValue(xRay.shouldDrawSide(state, blockView, pos, quad.cullFace(), cir.getReturnValue()));
		}
	}
}
