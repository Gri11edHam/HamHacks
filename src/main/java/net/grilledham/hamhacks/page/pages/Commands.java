package net.grilledham.hamhacks.page.pages;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.page.Page;
import net.grilledham.hamhacks.setting.KeySetting;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class Commands extends Page {
	
	@KeySetting(name = "hamhacks.page.command.prefix")
	public Keybind prefix = new Keybind(GLFW.GLFW_KEY_PERIOD);
	
	public Commands() {
		super(Text.translatable("hamhacks.page.command"));
	}
	
	@EventListener
	public void tick(EventTick e) {
		while(prefix.wasPressed()) {
			mc.setScreen(new ChatScreen(prefix.getCombinedString()));
		}
	}
}
