package net.grilledham.hamhacks.event.events;

import net.grilledham.hamhacks.event.EventCancelable;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;

public class EventPacketReceived extends EventCancelable {
	
	public Packet<?> packet;
	public PacketListener listener;
	
	public EventPacketReceived(Packet<?> packet, PacketListener listener) {
		this.packet = packet;
		this.listener = listener;
	}
}
