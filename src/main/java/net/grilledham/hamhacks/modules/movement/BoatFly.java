package net.grilledham.hamhacks.modules.movement;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.BoolSetting;
import net.grilledham.hamhacks.util.math.Vec3;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class BoatFly extends Module {
	
	private long lastTime;
	
	private float updates = 0;
	
	private final BoolSetting autoBreak = new BoolSetting("hamhacks.module.boatFly.autoBreak", false, () -> true);
	
	private boolean shouldDismount = false;
	
	private BoatEntity lastBoat = null;
	
	public BoatFly() {
		super(Text.translatable("hamhacks.module.boatFly"), Category.MOVEMENT, new Keybind(GLFW.GLFW_KEY_B));
		GENERAL_CATEGORY.add(autoBreak);
	}
	
	@EventListener
	public void onTick(EventTick e) {
		if(mc.player == null) {
			return;
		}
		if(mc.player.hasVehicle()) {
			Entity vehicle = mc.player.getVehicle();
			float distanceForward = 0;
			float distanceStrafe = 0;
			float distanceVertical = 0;
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
			if(mc.player.input.jumping) {
				distanceVertical += 1;
			}
			if(mc.options.sprintKey.isPressed()) {
				distanceVertical -= 1;
			}
			if(mc.options.sneakKey.isPressed()) {
				shouldDismount = true;
				if(autoBreak.get() && vehicle.getType() == EntityType.BOAT && vehicle.getControllingPassenger() == mc.player) {
					lastBoat = (BoatEntity)vehicle;
				}
			}
			float dx = (float)(distanceForward * Math.cos(Math.toRadians(mc.player.getYaw() + 90)));
			float dy = distanceVertical;
			float dz = (float)(distanceForward * Math.sin(Math.toRadians(mc.player.getYaw() + 90)));
			dx += (float)(distanceStrafe * Math.cos(Math.toRadians(mc.player.getYaw())));
			dz += (float)(distanceStrafe * Math.sin(Math.toRadians(mc.player.getYaw())));
			if(vehicle.getType() == EntityType.BOAT) {
				dy += 0.04;
			}
			vehicle.setVelocity(new Vec3d(dx, dy, dz));
			vehicle.setYaw(mc.player.getYaw());
			
			if(updates >= 0.5) {
				updates = 0;
				boolean isAboveBlock = false;
				for(int xAdd = -1; xAdd < 2; xAdd++) {
					for(int zAdd = -1; zAdd < 2; zAdd++) {
						if(mc.world.getBlockState(BlockPos.ofFloored(mc.player.getPos().subtract(0.3f * xAdd, 0.05f, 0.3f * zAdd))).getBlock() != Blocks.AIR) {
							isAboveBlock = true;
							break;
						}
					}
				}
				if(!isAboveBlock) {
					Vec3 prevPos = new Vec3(vehicle.getPos());
					if(mc.player.getVelocity().getY() > 0) {
						vehicle.setPosition(vehicle.getX(), vehicle.getY() - vehicle.getVelocity().getY() - 0.2, vehicle.getZ());
					} else {
						vehicle.setPosition(vehicle.getX(), vehicle.getY() - 0.05, vehicle.getZ());
					}
					mc.player.networkHandler.sendPacket(new VehicleMoveC2SPacket(vehicle));
					vehicle.setPosition(prevPos.get());
				}
			}
			
			updates += (System.currentTimeMillis() - lastTime) / 1000f;
			lastTime = System.currentTimeMillis();
		} else {
			shouldDismount = false;
		}
		if(lastBoat != null && !mc.player.hasVehicle()) {
			if(lastBoat.isAlive()) {
				mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(lastBoat, mc.player.isSneaking()));
			} else {
				lastBoat = null;
			}
		}
	}
	
	public boolean shouldDismount() {
		return shouldDismount;
	}
}
