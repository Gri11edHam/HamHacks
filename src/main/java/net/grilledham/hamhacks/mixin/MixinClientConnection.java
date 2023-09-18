package net.grilledham.hamhacks.mixin;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.grilledham.hamhacks.event.events.EventPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public abstract class MixinClientConnection extends SimpleChannelInboundHandler<Packet<?>> {
	
	@Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
	private static <T extends PacketListener> void handlePacket(Packet<T> packet, PacketListener listener, CallbackInfo ci) {
		EventPacket.EventPacketReceived event = new EventPacket.EventPacketReceived(packet, listener);
		event.call();
		if(event.canceled) {
			ci.cancel();
		}
	}
	
	@Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V", at = @At("HEAD"), cancellable = true)
	public void preSend(Packet<?> packet, PacketCallbacks callback, CallbackInfo ci) {
		EventPacket.EventPacketSent event = new EventPacket.EventPacketSent(EventPacket.EventPacketSent.Type.PRE, packet, callback);
		event.call();
		if(event.canceled) {
			ci.cancel();
		}
	}
	
	@Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V", at = @At("TAIL"), cancellable = true)
	public void postSend(Packet<?> packet, PacketCallbacks callback, CallbackInfo ci) {
		EventPacket.EventPacketSent event = new EventPacket.EventPacketSent(EventPacket.EventPacketSent.Type.POST, packet, callback);
		event.call();
		if(event.canceled) {
			ci.cancel();
		}
	}
	
	@Shadow
	protected abstract void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet) throws Exception;
}
