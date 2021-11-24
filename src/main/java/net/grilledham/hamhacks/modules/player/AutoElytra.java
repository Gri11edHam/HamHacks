package net.grilledham.hamhacks.modules.player;

import net.grilledham.hamhacks.client.HamHacksClient;
import net.grilledham.hamhacks.event.Event;
import net.grilledham.hamhacks.event.EventTick;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import org.lwjgl.glfw.GLFW;

public class AutoElytra extends Module {
	
	public AutoElytra() {
		super("Auto Elytra", Category.PLAYER, new Keybind(GLFW.GLFW_KEY_Y));
	}
	
	@Override
	public boolean onEvent(Event e) {
		boolean superReturn = super.onEvent(e);
		if(superReturn) {
			if(e instanceof EventTick) {
				if(!mc.player.isFallFlying()) {
					mc.player.startFallFlying();
				}
			}
		}
		return superReturn;
	}
	
	@Override
	public void onDisable() {
		mc.player.stopFallFlying();
		super.onDisable();
	}
}
