package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.event.events.EventKey;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class MixinKeyboard {
	
	@Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
	private void keyPressed(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
		EventKey event = new EventKey(window, key, scancode, action, modifiers);
		event.call();
		if(event.canceled) {
			ci.cancel();
		}
	}
}
