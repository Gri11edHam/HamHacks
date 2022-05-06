package net.grilledham.hamhacks.event.events;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.grilledham.hamhacks.event.EventCancelable;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;

public class EventPacket extends EventCancelable {
	
	public Packet<?> packet;
	
	public EventPacket(Packet<?> packet) {
		this.packet = packet;
	}
	
	public static class EventPacketReceived extends EventPacket {
		
		public PacketListener listener;
		
		public EventPacketReceived(Packet<?> packet, PacketListener listener) {
			super(packet);
			this.listener = listener;
		}
	}
	
	public static class EventPacketSent extends EventPacket {
		
		public GenericFutureListener<? extends Future<? super Void>> callback;
		
		public EventPacketSent(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> callback) {
			super(packet);
			this.callback = callback;
		}
	}
}
