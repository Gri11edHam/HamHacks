package net.grilledham.hamhacks.modules.combat;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventScroll;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class ScrollClicker extends Module {
	
	public ScrollClicker() {
		super("Scroll Clicker", Category.COMBAT, new Keybind(GLFW.GLFW_KEY_L));
	}
	
	@EventListener
	public void onScroll(EventScroll e) {
		e.canceled = true;
		double vertical = ((EventScroll)e).vertical;
		double horizontal = ((EventScroll)e).horizontal;
		if(vertical < 0 || horizontal < 0) {
			KeyBinding.onKeyPressed(mc.options.keyAttack.getDefaultKey());
		}
		if(vertical > 0 || horizontal > 0) {
			KeyBinding.onKeyPressed(mc.options.keyUse.getDefaultKey());
		}
	}
}
