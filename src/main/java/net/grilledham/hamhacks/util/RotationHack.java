package net.grilledham.hamhacks.util;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.EventManager;
import net.grilledham.hamhacks.event.events.EventMotion;
import net.grilledham.hamhacks.event.events.EventTick;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class RotationHack {
	
	private static final MinecraftClient mc = MinecraftClient.getInstance();
	
	private static boolean useFake;
	private static int useFakeTicks;
	
	private static float serverYaw;
	private static float serverPitch;
	
	private static float realYaw;
	private static float realPitch;
	
	public static void init() {
		RotationHack hack = new RotationHack();
		EventManager.register(hack);
	}
	
	@EventListener
	public void motionEvent(EventMotion e) {
		if(e.type == EventMotion.Type.PRE) {
			if(!useFake) {
				return;
			}
			
			ClientPlayerEntity player = mc.player;
			realYaw = player.getYaw();
			realPitch = player.getPitch();
			player.setYaw(serverYaw);
			player.setPitch(serverPitch);
		} else if(e.type == EventMotion.Type.POST) {
			if(!useFake) {
				return;
			}
			
			ClientPlayerEntity player = mc.player;
			player.setYaw(realYaw);
			player.setPitch(realPitch);
			if(useFakeTicks <= 0) {
				useFake = false;
			}
		}
	}
	
	@EventListener
	public void tickEvent(EventTick e) {
		useFakeTicks--;
	}
	
	public static void faceVectorPacket(Vec3d vec, int ticks) {
		Vec3d eyesPos = mc.player.getEyePos();
		
		double diffX = vec.x - eyesPos.x;
		double diffY = vec.y - eyesPos.y;
		double diffZ = vec.z - eyesPos.z;
		
		double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
		
		float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
		float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));
		
		useFake = true;
		useFakeTicks = ticks;
		serverYaw = yaw;
		serverPitch = pitch;
	}
	
	public static void faceVectorClient(Vec3d vec) {
		Vec3d eyesPos = mc.player.getEyePos();
		
		double diffX = vec.x - eyesPos.x;
		double diffY = vec.y - eyesPos.y;
		double diffZ = vec.z - eyesPos.z;
		
		double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
		
		float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
		float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));
		
		mc.player.setYaw(yaw);
		mc.player.setPitch(pitch);
	}
	
	public static void faceVectorClientIgnorePitch(Vec3d vec) {
		Vec3d eyesPos = mc.player.getEyePos();
		
		double diffX = vec.x - eyesPos.x;
		double diffZ = vec.z - eyesPos.z;
		
		float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
		
		mc.player.setYaw(yaw);
		mc.player.setPitch(0);
	}
	
	public static float getServerYaw() {
		return useFake ? serverYaw : mc.player.getYaw();
	}
	
	public static float getServerPitch() {
		return useFake ? serverPitch : mc.player.getPitch();
	}
}
