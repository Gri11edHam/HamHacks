package net.grilledham.hamhacks.mixin;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.grilledham.hamhacks.event.events.EventPacketReceived;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public abstract class MixinClientConnection extends SimpleChannelInboundHandler<Packet<?>> {
	
	@Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
	private static <T extends PacketListener> void handlePacket(Packet<T> packet, PacketListener listener, CallbackInfo ci) {
		EventPacketReceived event = new EventPacketReceived(packet, listener);
		event.call();
		if(event.canceled) {
			ci.cancel();
		}
	}
	
	@Shadow
	protected abstract void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet) throws Exception;
}
