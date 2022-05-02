package net.grilledham.hamhacks.modules.movement;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventMotion;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.setting.settings.BoolSetting;
import net.grilledham.hamhacks.util.setting.settings.FloatSetting;
import net.minecraft.entity.EntityPose;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class Speed extends Module {
	
	private FloatSetting speed;
	private BoolSetting autoJump;
	private BoolSetting disableWithElytra;
	
	private static Speed INSTANCE;
	
	public Speed() {
		super(new TranslatableText("module.hamhacks.speed"), Category.MOVEMENT, new Keybind(GLFW.GLFW_KEY_K));
		INSTANCE = this;
	}
	
	@Override
	public void addSettings() {
		speed = new FloatSetting("Speed", 2.5f, 0f, 10f);
		autoJump = new BoolSetting("Auto Jump", false);
		disableWithElytra = new BoolSetting("Disable With Elytra", true);
		addSetting(speed);
		addSetting(autoJump);
		addSetting(disableWithElytra);
	}
	
	@EventListener
	public void onMove(EventMotion e) {
		if(e.type == EventMotion.Type.PRE) {
			if(mc.player.getPose() == EntityPose.FALL_FLYING && disableWithElytra.getValue()) {
				return;
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
			dx *= speed.getValue() / 10f;
			dz *= speed.getValue() / 10f;
			mc.player.setVelocity(new Vec3d(dx, mc.player.getVelocity().y, dz));
			if(autoJump.getValue()) {
				if(mc.player.isOnGround() && (dx != 0 && dz != 0)) {
					mc.player.jump();
				}
			}
		}
	}
	
	public static Speed getInstance() {
		return INSTANCE;
	}
}
