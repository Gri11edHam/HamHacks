package net.grilledham.hamhacks.util;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.EventManager;
import net.grilledham.hamhacks.event.events.EventPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

public class ConnectionUtil {
	
	private static float tickRate;
	private static float calcTickRate;
	
	private static long lastPacketReceivedAt;
	private static long joinedGameAt;
	
	private static ServerInfo serverInfo = null;
	private static long lastUpdate;
	
	public static void init() {
		EventManager.register(new ConnectionUtil());
	}
	
	public static ServerInfo getServerInfo() {
		if(serverInfo == null || System.currentTimeMillis() - lastUpdate > 30000) {
			if(MinecraftClient.getInstance().getCurrentServerEntry() != null) {
				serverInfo = new ServerInfo("current connection", MinecraftClient.getInstance().getCurrentServerEntry().address, MinecraftClient.getInstance().getCurrentServerEntry().isLocal());
			}
			lastUpdate = System.currentTimeMillis();
		}
		return serverInfo;
	}
	
	@EventListener
	public void packetReceived(EventPacket.EventPacketReceived e) {
		if(e.packet instanceof GameJoinS2CPacket) {
			tickRate = 0;
			lastPacketReceivedAt = joinedGameAt = System.currentTimeMillis();
		}
		if(e.packet instanceof WorldTimeUpdateS2CPacket) {
			long now = System.currentTimeMillis();
			float elapsedTime = (now - lastPacketReceivedAt) / 1000f;
			tickRate = Math.min(Math.max(20 / elapsedTime, 0), 20);
			calcTickRate = tickRate;
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
		
		float elapsedTime = (System.currentTimeMillis() - lastPacketReceivedAt) / 1000f;
		if(elapsedTime != 0) {
			calcTickRate = tickRate / elapsedTime;
		}
		
		if(elapsedTime > 2) {
			return calcTickRate;
		}
		
		return tickRate;
	}
}
