package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.event.events.EventMotion;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {
	
	@Inject(method = "sendMovementPackets", at = @At("HEAD"), cancellable = true)
	public void preMoveEvent(CallbackInfo ci) {
		EventMotion event = new EventMotion(EventMotion.Type.PRE);
		event.call();
		if(event.canceled) {
			ci.cancel();
		}
	}
	
	@Inject(method = "sendMovementPackets", at = @At("TAIL"), cancellable = true)
	public void postMoveEvent(CallbackInfo ci) {
		EventMotion event = new EventMotion(EventMotion.Type.POST);
		event.call();
		if(event.canceled) {
			ci.cancel();
		}
	}
}
