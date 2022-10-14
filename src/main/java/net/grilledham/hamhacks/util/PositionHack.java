package net.grilledham.hamhacks.util;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.EventManager;
import net.grilledham.hamhacks.event.events.EventTick;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class PositionHack {
	
	private static final MinecraftClient mc = MinecraftClient.getInstance();
	
	private static boolean useFake;
	private static int useFakeTicks;
	
	private static Vec3d serverPos;
	private static Vec3d realPos;
	
	public static void init() {
		PositionHack hack = new PositionHack();
		EventManager.register(hack);
	}
	
	public static void preSend() {
		if(!useFake) {
			return;
		}
		
		ClientPlayerEntity player = mc.player;
		realPos = player.getPos();
		player.setPosition(player.getPos().add(serverPos));
	}
	
	public static void postSend() {
		if(!useFake) {
			return;
		}
		
		ClientPlayerEntity player = mc.player;
		player.setPosition(realPos);
		if(useFakeTicks <= 0) {
			useFake = false;
		}
	}
	
	@EventListener
	public void tickEvent(EventTick e) {
		useFakeTicks--;
	}
	
	public static void setOffsetPacket(double x, double y, double z) {
		useFake = true;
		useFakeTicks = 10;
		serverPos = new Vec3d(x, y, z);
	}
	
	public static void setOffsetClient(double x, double y, double z) {
		mc.player.setPosition(mc.player.getPos().add(x, y, z));
	}
	
	public static Vec3d getServerPos() {
		return useFake ? mc.player.getPos().add(serverPos) : mc.player.getPos();
	}
}
