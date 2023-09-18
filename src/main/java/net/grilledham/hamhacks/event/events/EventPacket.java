package net.grilledham.hamhacks.event.events;

import net.grilledham.hamhacks.event.EventCancelable;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;

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
		
		public PacketCallbacks callback;
		
		public Type type;
		
		public EventPacketSent(Type type, Packet<?> packet, PacketCallbacks callback) {
			super(packet);
			this.type = type;
			this.callback = callback;
		}
		
		public enum Type {
			PRE,
			POST
		}
	}
}
