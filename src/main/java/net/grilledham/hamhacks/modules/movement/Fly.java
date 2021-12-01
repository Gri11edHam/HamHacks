package net.grilledham.hamhacks.modules.movement;

import com.google.common.collect.Lists;
import net.grilledham.hamhacks.event.Event;
import net.grilledham.hamhacks.event.EventMotion;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.Setting;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class Fly extends Module {
	
	private float updates = 0;
	
	private Setting mode;
	private Setting speed;
	private Setting dropAmount;
	private Setting dropSpeed;
	
	private long lastTime;
	
	public Fly() {
		super("Fly", Category.MOVEMENT, new Keybind(GLFW.GLFW_KEY_F));
	}
	
	@Override
	public void addSettings() {
		mode = new Setting("Mode", "Default", List.of(new String[]{"Default", "Vanilla"})) {
			@Override
			protected void valueChanged() {
				super.valueChanged();
				settings.remove(dropAmount);
				if(getString().equalsIgnoreCase("Vanilla")) {
					settings.add(settings.indexOf(speed) + 1, dropAmount);
					settings.add(settings.indexOf(dropAmount) + 1, dropSpeed);
				}
			}
		};
		speed = new Setting("Speed", 1f, 0f, 10f);
		dropAmount = new Setting("Drop Amount", 0.5f, 0f, 5f);
		dropSpeed = new Setting("Drop Speed", 0.02f, 0f, 1f);
		settings.add(mode);
		settings.add(speed);
	}
	
	private double height;
	private int viewBobbing = 0;
	
	@Override
	public void onEnable() {
		super.onEnable();
		if(mode.getString().equalsIgnoreCase("Default")) {
			if (!Lists.newArrayList(mc.world.getBlockCollisions(mc.player, mc.player.getBoundingBox().offset(0.0D, -0.0001, 0.0D))).isEmpty()) {
				mc.player.setPosition(mc.player.getPos().add(0, 0.5, 0));
			}
		}
		if(mode.getString().equalsIgnoreCase("Vanilla")) {
			if (!Lists.newArrayList(mc.world.getBlockCollisions(mc.player, mc.player.getBoundingBox().offset(0.0D, -0.0001, 0.0D))).isEmpty()) {
				mc.player.setPosition(mc.player.getPos().add(0, dropAmount.getFloat(), 0));
			}
			height = mc.player.getPos().y;
		}
	}
	
	@Override
	public boolean onEvent(Event e) {
		boolean superReturn = super.onEvent(e);
		if(superReturn) {
			if(e instanceof EventMotion && ((EventMotion)e).type == EventMotion.Type.PRE) {
//				if ((mc.options.keyForward.isPressed() || mc.options.keyBack.isPressed() || mc.options.keyLeft.isPressed() || mc.options.keyRight.isPressed()) && mc.options.bobView) {
//					switch(viewBobbing) {
//						case 0 -> {
//							mc.player.setYaw(0.105f);
//							mc.player.setPitch(0.105f);
//							viewBobbing++;
//						}
//						case 1 -> viewBobbing++;
//						case 2 -> viewBobbing = 0;
//					}
//
//				}
				
				switch(mode.getString()) {
					case "Default" -> {
						mc.player.getAbilities().flying = true;
						double x = mc.player.getVelocity().x;
						double y = mc.player.getVelocity().y;
						double z = mc.player.getVelocity().z;
						if(!mc.player.input.jumping && !mc.player.input.sneaking) {
							y = 0;
						}
						if(mc.player.input.movementForward == 0 && mc.player.input.movementSideways == 0) {
							x = 0;
							z = 0;
						}
						mc.player.setVelocity(new Vec3d(x, y, z));
					}
					case "Vanilla" -> {
						mc.player.getAbilities().flying = true;
						double x = mc.player.getVelocity().x;
						double y = mc.player.getVelocity().y;
						double z = mc.player.getVelocity().z;
						if(!mc.player.input.jumping && !mc.player.input.sneaking) {
							y = 0;
							if(mc.player.getPos().y <= height - dropAmount.getFloat()) {
								y = dropAmount.getFloat();
							} else {
								y -= dropSpeed.getFloat();
							}
						} else {
							height = mc.player.getPos().y;
						}
						if(mc.player.input.movementForward == 0 && mc.player.input.movementSideways == 0) {
							x = 0;
							z = 0;
						}
						mc.player.setVelocity(new Vec3d(x, y, z));
					}
				}
			}
		}
		return superReturn;
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		if(mode.getString().equalsIgnoreCase("Default")) {
			mc.player.getAbilities().flying = false;
		} else if (mode.getString().equalsIgnoreCase("Vanilla")) {
			mc.player.getAbilities().flying = false;
		}
	}
}
