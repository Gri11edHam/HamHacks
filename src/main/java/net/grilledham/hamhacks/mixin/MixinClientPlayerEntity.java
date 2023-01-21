package net.grilledham.hamhacks.mixin;

import com.mojang.authlib.GameProfile;
import net.grilledham.hamhacks.event.events.EventMotion;
import net.grilledham.hamhacks.mixininterface.IClientEntityPlayer;
import net.grilledham.hamhacks.util.PositionHack;
import net.grilledham.hamhacks.util.RotationHack;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity implements IClientEntityPlayer {
	
	@Shadow protected abstract boolean isWalking();
	
	public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
		super(world, profile);
	}
	
	@Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
	public void preMoveEvent(CallbackInfo ci) {
		EventMotion event = new EventMotion(EventMotion.Type.PRE);
		event.call();
		if(event.canceled) {
			ci.cancel();
		}
	}
	
	@Inject(method = "tickMovement", at = @At("TAIL"), cancellable = true)
	public void postMoveEvent(CallbackInfo ci) {
		EventMotion event = new EventMotion(EventMotion.Type.POST);
		event.call();
		if(event.canceled) {
			ci.cancel();
		}
	}
	
	@Inject(method = "sendMovementPackets", at = @At("HEAD"))
	private void preSendMovePackets(CallbackInfo ci) {
		RotationHack.preSend();
		PositionHack.preSend();
	}
	
	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 0))
	private void preSendMovePacketsVehicle(CallbackInfo ci) {
		RotationHack.preSend();
		PositionHack.preSend();
	}
	
	@Inject(method = "sendMovementPackets", at = @At("TAIL"))
	private void postSendMovePackets(CallbackInfo ci) {
		RotationHack.postSend();
		PositionHack.postSend();
	}
	
	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 1, shift = At.Shift.AFTER))
	private void postSendMovePacketsVehicle(CallbackInfo ci) {
		RotationHack.postSend();
		PositionHack.postSend();
	}
	
	@Override
	public boolean walking() {
		return isWalking();
	}
}
