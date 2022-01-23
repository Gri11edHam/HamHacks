package net.grilledham.hamhacks.modules.movement;

import com.google.common.collect.Lists;
import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventMotion;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.setting.settings.FloatSetting;
import net.grilledham.hamhacks.util.setting.settings.SelectionSetting;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class Fly extends Module {
	
	private float updates = 0;
	
	private SelectionSetting mode;
	private FloatSetting speed;
	private FloatSetting dropAmount;
	private FloatSetting dropSpeed;
	
	private long lastTime;
	
	public Fly() {
		super("Fly", Category.MOVEMENT, new Keybind(GLFW.GLFW_KEY_F));
	}
	
	@Override
	public void addSettings() {
		mode = new SelectionSetting("Mode", "Default", "Default", "Vanilla") {
			@Override
			protected void valueChanged() {
				super.valueChanged();
				hideSetting(dropAmount);
				hideSetting(dropSpeed);
				if(getValue().equalsIgnoreCase("Vanilla")) {
					showSetting(dropAmount, shownSettings.indexOf(speed) + 1);
					showSetting(dropSpeed, shownSettings.indexOf(dropAmount) + 1);
				}
				updateScreenIfOpen();
			}
		};
		speed = new FloatSetting("Speed", 1f, 0f, 10f);
		dropAmount = new FloatSetting("Drop Amount", 0.5f, 0f, 5f);
		dropSpeed = new FloatSetting("Drop Speed", 0.02f, 0f, 1f);
		addSetting(mode);
		addSetting(speed);
		addSetting(dropAmount);
		addSetting(dropSpeed);
	}
	
	private double height;
	private int viewBobbing = 0;
	
	@Override
	public void onEnable() {
		super.onEnable();
		if(mode.getValue().equalsIgnoreCase("Default")) {
			if (!Lists.newArrayList(mc.world.getBlockCollisions(mc.player, mc.player.getBoundingBox().offset(0.0D, -0.0001, 0.0D))).isEmpty()) {
				mc.player.setPosition(mc.player.getPos().add(0, 0.5, 0));
			}
		}
		if(mode.getValue().equalsIgnoreCase("Vanilla")) {
			if (!Lists.newArrayList(mc.world.getBlockCollisions(mc.player, mc.player.getBoundingBox().offset(0.0D, -0.0001, 0.0D))).isEmpty()) {
				mc.player.setPosition(mc.player.getPos().add(0, dropAmount.getValue(), 0));
			}
			height = mc.player.getPos().y;
		}
	}
	
	@EventListener
	public void onMove(EventMotion e) {
		if(e.type == EventMotion.Type.PRE) {
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
			
			switch(mode.getValue()) {
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
						if(mc.player.getPos().y <= height - dropAmount.getValue()) {
							y = dropAmount.getValue();
						} else {
							y -= dropSpeed.getValue();
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
	
	@Override
	public void onDisable() {
		super.onDisable();
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
