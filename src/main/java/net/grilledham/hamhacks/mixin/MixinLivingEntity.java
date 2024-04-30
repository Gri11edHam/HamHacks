package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.player.Step;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {
	
	@Inject(method = "getStepHeight", at = @At("HEAD"), cancellable = true)
	private void overrideStepHeight(CallbackInfoReturnable<Float> cir) {
		Step step = ModuleManager.getModule(Step.class);
		if(step.isEnabled()) {
			cir.setReturnValue(step.height.get().floatValue());
		}
	}
}
