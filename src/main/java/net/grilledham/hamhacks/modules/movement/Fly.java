package net.grilledham.hamhacks.modules.movement;

import com.google.common.collect.Lists;
import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventMotion;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.setting.settings.BoolSetting;
import net.grilledham.hamhacks.util.setting.settings.FloatSetting;
import net.grilledham.hamhacks.util.setting.settings.SelectionSetting;
import net.minecraft.block.Material;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class Fly extends Module {
	
	private float updates = 0;
	
	private SelectionSetting mode;
	private FloatSetting speed;
	private BoolSetting smoothMovement;
	
	private long lastTime;
	
	public Fly() {
		super(new TranslatableText("module.hamhacks.fly"), Category.MOVEMENT, new Keybind(GLFW.GLFW_KEY_F));
	}
	
	@Override
	public void addSettings() {
		mode = new SelectionSetting("Mode", "Default", "Default", "Vanilla");
		speed = new FloatSetting("Speed", 1f, 0f, 10f);
		smoothMovement = new BoolSetting("Smooth Movement", true);
		addSetting(mode);
		addSetting(speed);
		addSetting(smoothMovement);
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		Speed.getInstance().forceDisable();
		if(mc.player == null) {
			return;
		}
		if(mode.getValue().equalsIgnoreCase("Default")) {
			if (!Lists.newArrayList(mc.world.getBlockCollisions(mc.player, mc.player.getBoundingBox().offset(0.0D, -0.0001, 0.0D))).isEmpty()) {
				mc.player.setPosition(mc.player.getPos().add(0, 0.5, 0));
			}
		}
		if(mode.getValue().equalsIgnoreCase("Vanilla")) {
			if (!Lists.newArrayList(mc.world.getBlockCollisions(mc.player, mc.player.getBoundingBox().offset(0.0D, -0.0001, 0.0D))).isEmpty()) {
				mc.player.setPosition(mc.player.getPos().add(0, 0.5, 0));
			}
		}
	}
	
	@EventListener
	public void onMove(EventMotion e) {
		if(e.type == EventMotion.Type.PRE) {
			switch(mode.getValue()) {
				case "Default" -> {
					if(smoothMovement.getValue()) {
						moveSmooth();
					} else {
						move();
					}
					
//					mc.player.getAbilities().flying = true;
//					double x = mc.player.getVelocity().x;
//					double y = mc.player.getVelocity().y;
//					double z = mc.player.getVelocity().z;
//					if(!mc.player.input.jumping && !mc.player.input.sneaking) {
//						y = 0;
//					}
//					if(mc.player.input.movementForward == 0 && mc.player.input.movementSideways == 0) {
//						x = 0;
//						z = 0;
//					}
//					mc.player.setVelocity(new Vec3d(x, y, z));
				}
				case "Vanilla" -> {
					if(smoothMovement.getValue()) {
						moveSmooth();
					} else {
						move();
					}
					
//					mc.player.getAbilities().flying = true;
//					double x = mc.player.getVelocity().x;
//					double y = mc.player.getVelocity().y;
//					double z = mc.player.getVelocity().z;
//					if(!mc.player.input.jumping && !mc.player.input.sneaking) {
//						y = 0;
//					}
//					if(mc.player.input.movementForward == 0 && mc.player.input.movementSideways == 0) {
//						x = 0;
//						z = 0;
//					}
//					mc.player.setVelocity(new Vec3d(x, y, z));
					
					if(updates >= 0.5f) {
						updates = 0;
					}
					
					boolean isAboveBlock = false;
					for(int xAdd = -1; xAdd < 2; xAdd++) {
						for(int zAdd = -1; zAdd < 2; zAdd++) {
							if(mc.world.getBlockState(new BlockPos(mc.player.getPos().subtract(0.3f * xAdd, 1, 0.3f * zAdd))).getMaterial() != Material.AIR) {
								isAboveBlock = true;
								break;
							}
						}
					}
					if(!isAboveBlock) {
						mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getPos().x, mc.player.getPos().y - updates, mc.player.getPos().z, true));
					}
					
					updates += (System.currentTimeMillis() - lastTime) / 1000f;
					lastTime = System.currentTimeMillis();
				}
			}
		}
	}
	
	private void move() {
		mc.player.getAbilities().flying = true;
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
		if(mc.player.input.sneaking) {
			distanceVertical -= 1;
		}
		float dx = (float) (distanceForward * Math.cos(Math.toRadians(mc.player.getYaw() + 90)));
		float dy = distanceVertical;
		float dz = (float) (distanceForward * Math.sin(Math.toRadians(mc.player.getYaw() + 90)));
		dx += (float) (distanceStrafe * Math.cos(Math.toRadians(mc.player.getYaw())));
		dz += (float) (distanceStrafe * Math.sin(Math.toRadians(mc.player.getYaw())));
		dx *= speed.getValue();
		dy *= speed.getValue();
		dz *= speed.getValue();
		mc.player.setVelocity(new Vec3d(dx, dy, dz));
	}
	
	private float lastDx = 0;
	private float lastDy = 0;
	private float lastDz = 0;
	
	private void moveSmooth() {
		mc.player.getAbilities().flying = true;
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
		if(mc.player.input.sneaking) {
			distanceVertical -= 1;
		}
		float dx = (float) (distanceForward * Math.cos(Math.toRadians(mc.player.getYaw() + 90)));
		float dy = distanceVertical;
		float dz = (float) (distanceForward * Math.sin(Math.toRadians(mc.player.getYaw() + 90)));
		dx += (float) (distanceStrafe * Math.cos(Math.toRadians(mc.player.getYaw())));
		dz += (float) (distanceStrafe * Math.sin(Math.toRadians(mc.player.getYaw())));
		dx = lastDx + (dx / 10f);
		dy = lastDy + (dy / 10f);
		dz = lastDz + (dz / 10f);
		dx *= speed.getValue();
		dy *= speed.getValue();
		dz *= speed.getValue();
		if(dx > speed.getValue()) {
			dx = speed.getValue();
		} else if(dx < -speed.getValue()) {
			dx = -speed.getValue();
		}
		if(dy > speed.getValue()) {
			dy = speed.getValue();
		} else if(dy < -speed.getValue()) {
			dy = -speed.getValue();
		}
		if(dz > speed.getValue()) {
			dz = speed.getValue();
		} else if(dz < -speed.getValue()) {
			dz = -speed.getValue();
		}
		if(!mc.player.input.jumping && !mc.player.input.sneaking) {
			dy = 0;
		}
		if(mc.player.input.movementForward == 0 && mc.player.input.movementSideways == 0) {
			dx = 0;
			dz = 0;
		}
		mc.player.setVelocity(new Vec3d(dx, dy, dz));
		lastDx = dx;
		lastDy = dy;
		lastDz = dz;
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		Speed.getInstance().reEnable();
		if(mc.player == null) {
			return;
		}
		if(mode.getValue().equalsIgnoreCase("Default")) {
			mc.player.getAbilities().flying = false;
		} else if (mode.getValue().equalsIgnoreCase("Vanilla")) {
			mc.player.getAbilities().flying = false;
		}
	}
}
