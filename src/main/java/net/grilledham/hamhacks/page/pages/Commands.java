package net.grilledham.hamhacks.page.pages;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.page.Page;
import net.grilledham.hamhacks.setting.KeySetting;
import net.grilledham.hamhacks.setting.SettingCategory;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class Commands extends Page {
	
	private final SettingCategory GENERAL_CATEGORY = new SettingCategory("hamhacks.page.command.category.general");
	
	public final KeySetting prefix = new KeySetting("hamhacks.page.command.prefix", new Keybind(GLFW.GLFW_KEY_PERIOD), () -> true);
	
	public Commands() {
		super(Text.translatable("hamhacks.page.command"));
		settingCategories.add(0, GENERAL_CATEGORY);
		GENERAL_CATEGORY.add(prefix);
	}
	
	@EventListener
	public void tick(EventTick e) {
		while(prefix.get().wasPressed()) {
			mc.setScreen(new ChatScreen(prefix.get().getCombinedString()));
		}
	}
}
