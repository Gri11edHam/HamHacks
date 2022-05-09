package net.grilledham.hamhacks.modules.movement;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventMotion;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.setting.settings.BoolSetting;
import net.grilledham.hamhacks.util.setting.settings.FloatSetting;
import net.minecraft.block.Material;
import net.minecraft.entity.EntityPose;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class Speed extends Module {
	
	private FloatSetting speed;
	private BoolSetting autoJump;
	private FloatSetting inAirMult;
	private FloatSetting onIceMult;
	private FloatSetting inWaterMult;
	private FloatSetting inTunnelMult;
	private BoolSetting disableWithElytra;
	
	private static Speed INSTANCE;
	
	public Speed() {
		super(new TranslatableText("module.hamhacks.speed"), Category.MOVEMENT, new Keybind(GLFW.GLFW_KEY_K));
		INSTANCE = this;
	}
	
	@Override
	public void addSettings() {
		speed = new FloatSetting(new TranslatableText("setting.speed.speed"), 2.5f, 0f, 10f);
		autoJump = new BoolSetting(new TranslatableText("setting.speed.autojump"), false);
		inAirMult = new FloatSetting(new TranslatableText("setting.speed.inairmultiplier"), 1.7f, 0.25f, 4);
		onIceMult = new FloatSetting(new TranslatableText("setting.speed.onicemultiplier"), 2, 0.25f, 4);
		inTunnelMult = new FloatSetting(new TranslatableText("setting.speed.intunnelmultiplier"), 1.7f, 0.25f, 4);
		inWaterMult = new FloatSetting(new TranslatableText("setting.speed.inwatermultiplier"), 0.6f, 0.25f, 4);
		disableWithElytra = new BoolSetting(new TranslatableText("setting.speed.disablewithelytra"), true);
		addSetting(speed);
		addSetting(autoJump);
		addSetting(inAirMult);
		addSetting(onIceMult);
		addSetting(inTunnelMult);
		addSetting(inWaterMult);
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
			if(distanceForward != 0 && distanceStrafe != 0) {
				distanceForward *= 3 / 4f;
				distanceStrafe *= 3 / 4f;
			}
			float dx = (float) (distanceForward * Math.cos(Math.toRadians(mc.player.getYaw() + 90)));
			float dz = (float) (distanceForward * Math.sin(Math.toRadians(mc.player.getYaw() + 90)));
			dx += (float) (distanceStrafe * Math.cos(Math.toRadians(mc.player.getYaw())));
			dz += (float) (distanceStrafe * Math.sin(Math.toRadians(mc.player.getYaw())));
			dx *= speed.getValue() / 10f;
			dz *= speed.getValue() / 10f;
			if(checkBlockBelow(Material.AIR)) {
				dx *= inAirMult.getValue();
				dz *= inAirMult.getValue();
			}
			if(checkBlockBelow(Material.ICE) || checkBlockBelow(Material.DENSE_ICE)) {
				dx *= onIceMult.getValue();
				dz *= onIceMult.getValue();
			}
			if(!(checkBlockAbove(Material.AIR) || checkBlockAbove(Material.WATER) || checkBlockAbove(Material.BUBBLE_COLUMN))) {
				dx *= inTunnelMult.getValue();
				dz *= inTunnelMult.getValue();
			}
			if(mc.player.isTouchingWater()) {
				dx *= inWaterMult.getValue();
				dz *= inWaterMult.getValue();
			}
			if(mc.player.isSneaking()) {
				dx /= 1.5;
				dz /= 1.5;
			}
			mc.player.setVelocity(new Vec3d(dx, mc.player.getVelocity().y, dz));
			if(autoJump.getValue()) {
				if(mc.player.isOnGround() && (dx != 0 && dz != 0)) {
					mc.player.jump();
				}
			}
		}
	}
	
	private boolean checkBlockAbove(Material material) {
		for(int xAdd = -1; xAdd < 2; xAdd++) {
			for(int zAdd = -1; zAdd < 2; zAdd++) {
				if(mc.world.getBlockState(new BlockPos(mc.player.getPos().add(0.3f * xAdd, 2.01, 0.3f * zAdd))).getMaterial() != material) {
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean checkBlockBelow(Material material) {
		for(int xAdd = -1; xAdd < 2; xAdd++) {
			for(int zAdd = -1; zAdd < 2; zAdd++) {
				if(mc.world.getBlockState(new BlockPos(mc.player.getPos().subtract(0.3f * xAdd, 0.01, 0.3f * zAdd))).getMaterial() != material) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static Speed getInstance() {
		return INSTANCE;
	}
}
