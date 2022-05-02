package net.grilledham.hamhacks.util;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.EventManager;
import net.grilledham.hamhacks.event.events.EventPacketReceived;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

public class ConnectionUtil {
	
	private static float tickRate;
	
	private static long lastPacketReceivedAt;
	private static long joinedGameAt;
	
	public static void init() {
		EventManager.register(new ConnectionUtil());
	}
	
	@EventListener
	public void packetReceived(EventPacketReceived e) {
		if(e.packet instanceof GameJoinS2CPacket) {
			tickRate = 0;
			lastPacketReceivedAt = joinedGameAt = System.currentTimeMillis();
		}
		if(e.packet instanceof WorldTimeUpdateS2CPacket) {
			long now = System.currentTimeMillis();
			float elapsedTime = (now - lastPacketReceivedAt) / 1000f;
			tickRate = 20 / elapsedTime;
			lastPacketReceivedAt = System.currentTimeMillis();
		}
	}
	
	public static long getTimeSinceLastTick() {
		long now = System.currentTimeMillis();
		if(now - joinedGameAt < 2000) {
			return 0;
		}
		return now - lastPacketReceivedAt;
	}
	
	public static float getTPS() {
		if(MinecraftClient.getInstance().player == null) {
			return 0;
		}
		if(System.currentTimeMillis() - joinedGameAt < 2000) {
			return 20;
		}
		
		return tickRate;
	}
}
