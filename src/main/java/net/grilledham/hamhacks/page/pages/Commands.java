package net.grilledham.hamhacks.page.pages;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.page.Page;
import net.grilledham.hamhacks.setting.KeySetting;
import net.grilledham.hamhacks.setting.SettingCategory;
import net.grilledham.hamhacks.setting.StringSetting;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class Commands extends Page {
	
	private final SettingCategory GENERAL_CATEGORY = new SettingCategory("hamhacks.page.command.category.general");
	
	public final StringSetting prefix = new StringSetting("hamhacks.page.command.prefix", ".", () -> true, ".");
	public final KeySetting quickCommand = new KeySetting("hamhacks.page.command.quickCommand", new Keybind(GLFW.GLFW_KEY_PERIOD), () -> true);
	
	public Commands() {
		super(Text.translatable("hamhacks.page.command"));
		settingCategories.add(0, GENERAL_CATEGORY);
		GENERAL_CATEGORY.add(prefix);
		GENERAL_CATEGORY.add(quickCommand);
	}
	
	@EventListener
	public void tick(EventTick e) {
		while(quickCommand.get().wasPressed()) {
			mc.setScreen(new ChatScreen(getPrefix()));
		}
	}
	
	public String getPrefix() {
		return prefix.get().equals("") ? "." : prefix.get();
	}
}
