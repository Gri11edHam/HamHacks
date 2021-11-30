package net.grilledham.hamhacks.modules.movement;

import net.grilledham.hamhacks.event.Event;
import net.grilledham.hamhacks.event.EventMotion;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.Setting;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class Speed extends Module {
	
	private Setting speed;
	private Setting autoJump;
	private Setting disableWithElytra;
	
	public Speed() {
		super("Speed", Category.MOVEMENT, new Keybind(GLFW.GLFW_KEY_K));
	}
	
	@Override
	public void addSettings() {
		speed = new Setting("Speed", 2.5f, 0f, 10f);
		autoJump = new Setting("Auto Jump", false);
		disableWithElytra = new Setting("Disable With Elytra", true);
		settings.add(speed);
		settings.add(autoJump);
		settings.add(disableWithElytra);
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
	}
	
	@Override
	public boolean onEvent(Event e) {
		boolean superReturn = super.onEvent(e);
		if(superReturn) {
			if(e instanceof EventMotion && ((EventMotion)e).type == EventMotion.Type.PRE) {
				if(mc.player.getPose() == EntityPose.FALL_FLYING && disableWithElytra.getBool()) {
					return true;
				}
				float distanceForward = 0;
				float distanceStrafe = 0;
				if(mc.player.input.pressingForward) {
					distanceForward += 1;
				}
				if(mc.player.input.pressingBack) {
					distanceForward -= 1;
				}
				if(mc.player.input.pressingRight) {
					distanceStrafe -= 1;
				}
				if(mc.player.input.pressingLeft) {
					distanceStrafe += 1;
				}
				float dx = (float) (distanceForward * Math.cos(Math.toRadians(mc.player.getYaw() + 90)));
				float dz = (float) (distanceForward * Math.sin(Math.toRadians(mc.player.getYaw() + 90)));
				dx += (float) (distanceStrafe * Math.cos(Math.toRadians(mc.player.getYaw())));
				dz += (float) (distanceStrafe * Math.sin(Math.toRadians(mc.player.getYaw())));
				dx *= speed.getFloat() / 10f;
				dz *= speed.getFloat() / 10f;
				mc.player.setVelocity(new Vec3d(dx, mc.player.getVelocity().y, dz));
				if(autoJump.getBool()) {
					if(mc.player.isOnGround() && (dx != 0 && dz != 0)) {
						mc.player.jump();
					}
				}
			}
		}
		return superReturn;
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
	}
}
