package net.grilledham.hamhacks.modules.movement;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventMotion;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.BoolSetting;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.minecraft.block.Material;
import net.minecraft.entity.EntityPose;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class Speed extends Module {
	
	@NumberSetting(
			name = "hamhacks.module.speed.speed",
			defaultValue = 2.5f,
			min = 0,
			max = 10
	)
	public float speed = 2.5f;
	
	@BoolSetting(name = "hamhacks.module.speed.autoJump")
	public boolean autoJump = false;
	
	@NumberSetting(
			name = "hamhacks.module.speed.inAirMultiplier",
			defaultValue = 1.7f,
			min = 0.25f,
			max = 4
	)
	public float inAirMult = 1.7f;
	
	@NumberSetting(
			name = "hamhacks.module.speed.onIceMultiplier",
			defaultValue = 2,
			min = 0.25f,
			max = 4
	)
	public float onIceMult = 2;
	
	@NumberSetting(
			name = "hamhacks.module.speed.inTunnelMultiplier",
			defaultValue = 1.7f,
			min = 0.25f,
			max = 4
	)
	public float inTunnelMult = 1.7f;
	
	@NumberSetting(
			name = "hamhacks.module.speed.inWaterMultiplier",
			defaultValue = 0.6f,
			min = 0.25f,
			max = 4
	)
	public float inWaterMult = 0.6f;
	
	@BoolSetting(name = "hamhacks.module.speed.disableWithElytra", defaultValue = true)
	public boolean disableWithElytra = true;
	
	public Speed() {
		super(Text.translatable("hamhacks.module.speed"), Category.MOVEMENT, new Keybind(GLFW.GLFW_KEY_K));
	}
	
	@Override
	public String getHUDText() {
		return super.getHUDText() + " \u00a77" + String.format("%.2f", speed);
	}
	
	@EventListener
	public void onMove(EventMotion e) {
		if(e.type == EventMotion.Type.PRE) {
			if(mc.player.getPose() == EntityPose.FALL_FLYING && disableWithElytra) {
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
			dx *= speed / 10f;
			dz *= speed / 10f;
			if(checkBlockBelow(Material.AIR)) {
				dx *= inAirMult;
				dz *= inAirMult;
			}
			if(checkBlockBelow(Material.ICE) || checkBlockBelow(Material.DENSE_ICE)) {
				dx *= onIceMult;
				dz *= onIceMult;
			}
			if(!(checkBlockAbove(Material.AIR) || checkBlockAbove(Material.WATER) || checkBlockAbove(Material.BUBBLE_COLUMN))) {
				dx *= inTunnelMult;
				dz *= inTunnelMult;
			}
			if(mc.player.isTouchingWater()) {
				dx *= inWaterMult;
				dz *= inWaterMult;
			}
			if(mc.player.isSneaking()) {
				dx /= 1.5;
				dz /= 1.5;
			}
			mc.player.setVelocity(new Vec3d(dx, mc.player.getVelocity().y, dz));
			if(autoJump) {
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
}
