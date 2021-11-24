package net.grilledham.hamhacks.modules.player;

import net.grilledham.hamhacks.event.Event;
import net.grilledham.hamhacks.event.EventMotion;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.Setting;

public class Step extends Module {
	
	private Setting height;
	
	private float originalStepHeight;
	
	public Step() {
		super("Step", Category.PLAYER, new Keybind(0));
	}
	
	@Override
	public void addSettings() {
		super.addSettings();
		height = new Setting("Height", 1f, 0f, 10f);
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		originalStepHeight = mc.player.stepHeight;
		mc.player.stepHeight = height.getFloat();
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		mc.player.stepHeight = originalStepHeight;
	}
	
	@Override
	public boolean onEvent(Event e) {
		boolean superReturn = super.onEvent(e);
		if(superReturn) {
			if(e instanceof EventMotion) {
//				if(mc.player.horizontalCollision) {
//					mc.player.stepHeight =
//				}
			}
		}
		return superReturn;
	}
}
