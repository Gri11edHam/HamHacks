package net.grilledham.hamhacks.modules.combat;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventScroll;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ScrollClicker extends Module {
	
	public ScrollClicker() {
		super(Text.translatable("module.hamhacks.scrollclick"), Text.translatable("module.hamhacks.scrollclick.tooltip"), Category.COMBAT, new Keybind(GLFW.GLFW_KEY_L));
	}
	
	@EventListener
	public void onScroll(EventScroll e) {
		e.canceled = true;
		double vertical = ((EventScroll)e).vertical;
		double horizontal = ((EventScroll)e).horizontal;
		if(vertical < 0 || horizontal < 0) {
			KeyBinding.onKeyPressed(mc.options.attackKey.getDefaultKey());
		}
		if(vertical > 0 || horizontal > 0) {
			KeyBinding.onKeyPressed(mc.options.useKey.getDefaultKey());
		}
	}
}
