package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.event.EventScroll;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MixinMouse {
	
	@Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
	private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo info) {
		EventScroll e = new EventScroll(vertical, horizontal);
		e.call();
		if(e.canceled) {
			info.cancel();
		}
	}
}
