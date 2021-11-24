package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.event.EventKey;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBinding.class)
public class MixinKeyBinding {
	
	@Inject(method = "updatePressedStates", at = @At("HEAD"))
	private static void keyPressed(CallbackInfo ci) {
		new EventKey().call();
	}
}
