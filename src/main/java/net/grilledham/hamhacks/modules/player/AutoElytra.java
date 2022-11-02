package net.grilledham.hamhacks.modules.player;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class AutoElytra extends Module {
	
	public AutoElytra() {
		super(Text.translatable("hamhacks.module.autoElytra"), Category.PLAYER, new Keybind(GLFW.GLFW_KEY_Y));
	}
	
	@EventListener
	public void onTick(EventTick e) {
		if(mc.player == null) {
			return;
		}
		if(!mc.player.isFallFlying()) {
			mc.player.startFallFlying();
		}
	}
	
	@Override
	public void onDisable() {
		if(mc.player == null) {
			return;
		}
		mc.player.stopFallFlying();
		super.onDisable();
	}
}
