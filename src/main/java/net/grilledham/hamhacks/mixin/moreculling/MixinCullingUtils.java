package net.grilledham.hamhacks.mixin.moreculling;

import ca.fxco.moreculling.utils.CullingUtils;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.render.XRay;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CullingUtils.class)
public class MixinCullingUtils {

	@Inject(method = "shouldDrawSideCulling", at = @At("RETURN"), cancellable = true, remap = false)
	private static void shouldDrawSideCulling(BlockState thisState, BlockState sideState, BlockView world, BlockPos thisPos, Direction side, BlockPos sidePos, CallbackInfoReturnable<Boolean> cir) {
		XRay xRay = ModuleManager.getModule(XRay.class);
		if(xRay.isEnabled()) {
			cir.setReturnValue(xRay.shouldDrawSide(thisState, sideState, side, cir.getReturnValue()));
		}
	}
}
