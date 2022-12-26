package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.render.XRay;
import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlockState.class)
public abstract class MixinAbstractBlockState {
	
	@Shadow public abstract Block getBlock();
	
	@Inject(method = "getAmbientOcclusionLightLevel", at = @At("HEAD"), cancellable = true)
	public void getAmbientOcclusionLightLevel(BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
		XRay xRay = ModuleManager.getModule(XRay.class);
		if(xRay.isEnabled()) {
			cir.setReturnValue(1F);
		}
	}
	
	@Inject(method = "getLuminance", at = @At("HEAD"), cancellable = true)
	public void getLuminance(CallbackInfoReturnable<Integer> cir) {
		XRay xRay = ModuleManager.getModule(XRay.class);
		if(xRay.isEnabled()) {
				cir.setReturnValue(12);
		}
	}
}
