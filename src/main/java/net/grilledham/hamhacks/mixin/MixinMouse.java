package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.event.events.EventLookDirection;
import net.grilledham.hamhacks.event.events.EventScroll;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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
	
	@Redirect(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"))
	private void onCursorPos(ClientPlayerEntity instance, double dx, double dy) {
		EventLookDirection e = new EventLookDirection(dx, dy);
		e.call();
		if(!e.canceled) {
			instance.changeLookDirection(dx, dy);
		}
	}
}
