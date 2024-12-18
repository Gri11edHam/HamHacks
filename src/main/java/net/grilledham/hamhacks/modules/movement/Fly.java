package net.grilledham.hamhacks.modules.movement;

import com.google.common.collect.Lists;
import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventMotion;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.notification.Notifications;
import net.grilledham.hamhacks.setting.BoolSetting;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.grilledham.hamhacks.setting.SelectionSetting;
import net.grilledham.hamhacks.util.PositionHack;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class Fly extends Module {
	
	private float updates = 0;
	
	private final SelectionSetting mode = new SelectionSetting("hamhacks.module.fly.mode", 0, () -> true, "hamhacks.module.fly.mode.default", "hamhacks.module.fly.mode.vanilla", "hamhacks.module.fly.mode.jetpack");
	
	private final NumberSetting speed = new NumberSetting("hamhacks.module.fly.speed", 1, () -> mode.get() != 2, 0, 10);
	
	private final BoolSetting smoothMovement = new BoolSetting("hamhacks.module.fly.smoothMovement", false, () -> mode.get() != 2);
	
	private final NumberSetting jetpackSpeed = new NumberSetting("hamhacks.module.fly.jetpackSpeed", 0.2, () -> mode.get() == 2, 0.1, 1);
	
	private final BoolSetting autoLand = new BoolSetting("hamhacks.module.fly.autoLand", false, () -> mode.get() == 2);
	
	private boolean landing = false;
	
	private long lastTime;
	
	public Fly() {
		super(Text.translatable("hamhacks.module.fly"), Category.MOVEMENT, new Keybind(GLFW.GLFW_KEY_F));
		GENERAL_CATEGORY.add(mode);
		GENERAL_CATEGORY.add(speed);
		GENERAL_CATEGORY.add(smoothMovement);
		GENERAL_CATEGORY.add(jetpackSpeed);
		GENERAL_CATEGORY.add(autoLand);
	}
	
	@Override
	public String getHUDText() {
		return super.getHUDText() + " \u00a77" + mode.options()[mode.get()];
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		if(ModuleManager.getModule(Speed.class).isEnabled()) {
			Notifications.notify(getName(), "Disabled " + ModuleManager.getModule(Speed.class).getName());
		}
		ModuleManager.getModule(Speed.class).forceDisable();
		if(mc.player == null) {
			return;
		}
		if(mode.get() == 0) {
			if(!Lists.newArrayList(mc.world.getBlockCollisions(mc.player, mc.player.getBoundingBox().offset(0.0D, -0.0001, 0.0D))).isEmpty()) {
				mc.player.setPosition(mc.player.getPos().add(0, 0.5, 0));
			}
		}
		if(mode.get() == 1) {
			if(!Lists.newArrayList(mc.world.getBlockCollisions(mc.player, mc.player.getBoundingBox().offset(0.0D, -0.0001, 0.0D))).isEmpty()) {
				mc.player.setPosition(mc.player.getPos().add(0, 0.5, 0));
			}
		}
	}
	
	@EventListener
	public void onMove(EventMotion e) {
		if(e.type == EventMotion.Type.PRE) {
			switch(mode.get()) {
				case 0 -> {
					if(smoothMovement.get()) {
						moveSmooth();
					} else {
						move();
					}
				}
				case 1 -> {
					if(smoothMovement.get()) {
						moveSmooth();
					} else {
						move();
					}
					if(updates >= 0.5) {
						updates = 0;
					}
					
					if(updates >= 0.25) {
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
							if(mc.player.getVelocity().getY() > 0) {
								PositionHack.setOffsetPacket(0, -(mc.player.getVelocity().getY()) - 0.2, 0);
							} else {
								PositionHack.setOffsetPacket(0, -0.05, 0);
							}
						} else {
							PositionHack.setOffsetPacket(0, 0, 0);
						}
					} else {
						PositionHack.setOffsetPacket(0, 0, 0);
					}
					
					updates += (System.currentTimeMillis() - lastTime) / 1000f;
					lastTime = System.currentTimeMillis();
				}
				case 2 -> {
					mc.player.addVelocity(0, mc.player.input.playerInput.jump() ? jetpackSpeed.get() : 0, 0);
					if(autoLand.get()) {
						BlockPos pos = mc.player.getBlockPos().down();
						for(; !mc.world.isOutOfHeightLimit(pos); pos = pos.down()) {
							BlockState state = mc.world.getBlockState(pos);
							if(!state.getCollisionShape(mc.world, pos).isEmpty()) {
								break;
							}
						}
						if(!mc.world.isOutOfHeightLimit(pos)) {
							double d0 = mc.player.getPos().add(0, mc.player.getVelocity().getY() * 4, 0).getY() - pos.getY();
							double d1 = mc.player.getPos().getY() - pos.getY();
							if(d0 < 0 && d1 > 1.5) {
								mc.player.addVelocity(0, -mc.player.getVelocity().getY() + 0.2, 0);
							}
						}
					}
				}
			}
		}
	}
	
	private void move() {
		mc.player.getAbilities().flying = true;
		float distanceForward = 0;
		float distanceStrafe = 0;
		float distanceVertical = 0;
		if(mc.player.input.playerInput.forward()) {
			distanceForward += 1;
		}
		if(mc.player.input.playerInput.backward()) {
			distanceForward -= 1;
		}
		if(mc.player.input.playerInput.right()) {
			distanceStrafe -= 1;
		}
		if(mc.player.input.playerInput.left()) {
			distanceStrafe += 1;
		}
		if(mc.player.input.playerInput.jump()) {
			distanceVertical += 1;
		}
		if(mc.player.input.playerInput.sneak()) {
			distanceVertical -= 1;
		}
		float dx = (float)(distanceForward * Math.cos(Math.toRadians(mc.player.getYaw() + 90)));
		float dy = distanceVertical;
		float dz = (float)(distanceForward * Math.sin(Math.toRadians(mc.player.getYaw() + 90)));
		dx += (float)(distanceStrafe * Math.cos(Math.toRadians(mc.player.getYaw())));
		dz += (float)(distanceStrafe * Math.sin(Math.toRadians(mc.player.getYaw())));
		dx *= speed.get();
		dy *= speed.get();
		dz *= speed.get();
		mc.player.setVelocity(new Vec3d(dx, dy, dz));
	}
	
	private double lastDx = 0;
	private double lastDy = 0;
	private double lastDz = 0;
	
	private void moveSmooth() {
		mc.player.getAbilities().flying = true;
		float distanceForward = 0;
		float distanceStrafe = 0;
		float distanceVertical = 0;
		if(mc.player.input.playerInput.forward()) {
			distanceForward += 1;
		}
		if(mc.player.input.playerInput.backward()) {
			distanceForward -= 1;
		}
		if(mc.player.input.playerInput.right()) {
			distanceStrafe -= 1;
		}
		if(mc.player.input.playerInput.left()) {
			distanceStrafe += 1;
		}
		if(mc.player.input.playerInput.jump()) {
			distanceVertical += 1;
		}
		if(mc.player.input.playerInput.sneak()) {
			distanceVertical -= 1;
		}
		distanceForward *= speed.get();
		distanceStrafe *= speed.get();
		distanceVertical *= speed.get();
		double dx = distanceForward * Math.cos(Math.toRadians(mc.player.getYaw() + 90));
		double dy = distanceVertical;
		double dz = distanceForward * Math.sin(Math.toRadians(mc.player.getYaw() + 90));
		dx += distanceStrafe * Math.cos(Math.toRadians(mc.player.getYaw()));
		dz += distanceStrafe * Math.sin(Math.toRadians(mc.player.getYaw()));
		dx = lastDx + (dx / 10f);
		dy = lastDy + (dy / 10f);
		dz = lastDz + (dz / 10f);
		if(dx > speed.get()) {
			dx = speed.get();
		} else if(dx < -speed.get()) {
			dx = -speed.get();
		}
		if(dy > speed.get()) {
			dy = speed.get();
		} else if(dy < -speed.get()) {
			dy = -speed.get();
		}
		if(dz > speed.get()) {
			dz = speed.get();
		} else if(dz < -speed.get()) {
			dz = -speed.get();
		}
		if(!mc.player.input.playerInput.jump() && !mc.player.input.playerInput.sneak()) {
			dy = 0;
		}
		if(mc.player.input.movementForward == 0 && mc.player.input.movementSideways == 0) {
			dx = 0;
			dz = 0;
		}
		mc.player.setVelocity(new Vec3d(dx, dy, dz));
		lastDx = dx * (9 / 10f);
		lastDy = dy * (9 / 10f);
		lastDz = dz * (9 / 10f);
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		ModuleManager.getModule(Speed.class).reEnable();
		if(ModuleManager.getModule(Speed.class).isEnabled()) {
			Notifications.notify(getName(), "Re-Enabled " + ModuleManager.getModule(Speed.class).getName());
		}
		lastDx = 0;
		lastDy = 0;
		lastDz = 0;
		if(mc.player == null) {
			return;
		}
		if(mode.get() == 0) {
			mc.player.getAbilities().flying = false;
		} else if(mode.get() == 1) {
			mc.player.getAbilities().flying = false;
			PositionHack.setOffsetPacket(0, 0, 0);
		}
	}
}
