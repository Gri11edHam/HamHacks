package net.grilledham.hamhacks.modules.movement;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventMotion;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.BoolSetting;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.grilledham.hamhacks.setting.SettingCategory;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityPose;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class Speed extends Module {
	
	private final SettingCategory OPTIONS_CATEGORY = new SettingCategory("hamhacks.module.speed.category.options");
	
	private final NumberSetting speed = new NumberSetting("hamhacks.module.speed.speed", 2.5, () -> true, 0, 10);
	
	private final BoolSetting autoJump = new BoolSetting("hamhacks.module.speed.autoJump", false, () -> true);
	
	private final BoolSetting disableWithElytra = new BoolSetting("hamhacks.module.speed.disableWithElytra", true, () -> true);
	
	private final SettingCategory MULTIPLIERS_CATEGORY = new SettingCategory("hamhacks.module.speed.category.multipliers");
	
	private final NumberSetting inAirMult = new NumberSetting("hamhacks.module.speed.inAirMultiplier", 1.7, () -> true, 0.25, 4);
	
	private final NumberSetting onIceMult = new NumberSetting("hamhacks.module.speed.onIceMultiplier", 2, () -> true, 0.25, 4);
	
	private final NumberSetting inTunnelMult = new NumberSetting("hamhacks.module.speed.inTunnelMultiplier", 1.7, () -> true, 0.25, 4);
	
	private final NumberSetting inWaterMult = new NumberSetting("hamhacks.module.speed.inWaterMultiplier", 0.6, () -> true, 0.25, 4);
	
	public Speed() {
		super(Text.translatable("hamhacks.module.speed"), Category.MOVEMENT, new Keybind(GLFW.GLFW_KEY_K));
		settingCategories.add(0, OPTIONS_CATEGORY);
		OPTIONS_CATEGORY.add(speed);
		OPTIONS_CATEGORY.add(autoJump);
		OPTIONS_CATEGORY.add(disableWithElytra);
		settingCategories.add(1, MULTIPLIERS_CATEGORY);
		MULTIPLIERS_CATEGORY.add(inAirMult);
		MULTIPLIERS_CATEGORY.add(onIceMult);
		MULTIPLIERS_CATEGORY.add(inTunnelMult);
		MULTIPLIERS_CATEGORY.add(inWaterMult);
	}
	
	@Override
	public String getHUDText() {
		return super.getHUDText() + " \u00a77" + String.format("%.2f", speed.get());
	}
	
	@EventListener
	public void onMove(EventMotion e) {
		if(e.type == EventMotion.Type.PRE) {
			if(mc.player.getPose() == EntityPose.FALL_FLYING && disableWithElytra.get()) {
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
			double dx = (float) (distanceForward * Math.cos(Math.toRadians(mc.player.getYaw() + 90)));
			double dz = (float) (distanceForward * Math.sin(Math.toRadians(mc.player.getYaw() + 90)));
			dx += (float) (distanceStrafe * Math.cos(Math.toRadians(mc.player.getYaw())));
			dz += (float) (distanceStrafe * Math.sin(Math.toRadians(mc.player.getYaw())));
			dx *= speed.get() / 10f;
			dz *= speed.get() / 10f;
			if(checkBlockBelow(Blocks.AIR)) {
				dx *= inAirMult.get();
				dz *= inAirMult.get();
			}
			if(checkBlockBelow(Blocks.ICE) || checkBlockBelow(Blocks.PACKED_ICE)) {
				dx *= onIceMult.get();
				dz *= onIceMult.get();
			}
			if(!(checkBlockAbove(Blocks.AIR) || checkBlockAbove(Blocks.WATER) || checkBlockAbove(Blocks.BUBBLE_COLUMN))) {
				dx *= inTunnelMult.get();
				dz *= inTunnelMult.get();
			}
			if(mc.player.isTouchingWater()) {
				dx *= inWaterMult.get();
				dz *= inWaterMult.get();
			}
			if(mc.player.isSneaking()) {
				dx /= 1.5;
				dz /= 1.5;
			}
			mc.player.setVelocity(new Vec3d(dx, mc.player.getVelocity().y, dz));
			if(autoJump.get()) {
				if(mc.player.isOnGround() && (dx != 0 && dz != 0)) {
					mc.player.jump();
				}
			}
		}
	}
	
	private boolean checkBlockAbove(Block block) {
		for(int xAdd = -1; xAdd < 2; xAdd++) {
			for(int zAdd = -1; zAdd < 2; zAdd++) {
				if(mc.world.getBlockState(BlockPos.ofFloored(mc.player.getPos().add(0.3f * xAdd, 2.01, 0.3f * zAdd))).getBlock() != block) {
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean checkBlockBelow(Block block) {
		for(int xAdd = -1; xAdd < 2; xAdd++) {
			for(int zAdd = -1; zAdd < 2; zAdd++) {
				if(mc.world.getBlockState(BlockPos.ofFloored(mc.player.getPos().subtract(0.3f * xAdd, 0.01, 0.3f * zAdd))).getBlock() != block) {
					return false;
				}
			}
		}
		return true;
	}
}
