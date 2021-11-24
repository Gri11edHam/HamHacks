package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.event.EventMotion;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {
	
	@Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
	public void moveEvent(CallbackInfo ci) {
		EventMotion event = new EventMotion();
		event.call();
		if(event.canceled) {
			ci.cancel();
		}
	}
}
