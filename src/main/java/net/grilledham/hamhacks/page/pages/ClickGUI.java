package net.grilledham.hamhacks.page.pages;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.gui.screen.impl.ClickGUIScreen;
import net.grilledham.hamhacks.gui.screen.impl.ModuleSettingsScreen;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.page.Page;
import net.grilledham.hamhacks.setting.ColorSetting;
import net.grilledham.hamhacks.setting.KeySetting;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.grilledham.hamhacks.util.Color;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ClickGUI extends Page {
	
	@ColorSetting(name = "hamhacks.page.clickGui.accentColor", category = "hamhacks.page.clickGui.category.appearance")
	public Color accentColor = Color.getDarkRed();
	@ColorSetting(name = "hamhacks.page.clickGui.backgroundColor", category = "hamhacks.page.clickGui.category.appearance")
	public Color bgColor = new Color(0xFF222222);
	@ColorSetting(name = "hamhacks.page.clickGui.backgroundColorHovered", category = "hamhacks.page.clickGui.category.appearance")
	public Color bgColorHovered = new Color(0xFF555555);
	@ColorSetting(name = "hamhacks.page.clickGui.enabledColor", category = "hamhacks.page.clickGui.category.appearance")
	public Color enabledColor = new Color(0xFF880000);
	@ColorSetting(name = "hamhacks.page.clickGui.enabledColorHovered", category = "hamhacks.page.clickGui.category.appearance")
	public Color enabledColorHovered = new Color(0xFFAA5555);
	@ColorSetting(name = "hamhacks.page.clickGui.textColor", category = "hamhacks.page.clickGui.category.appearance")
	public Color textColor = Color.getWhite();
	@NumberSetting(
			name = "hamhacks.page.clickGui.scale",
			defaultValue = 2,
			min = 1,
			max = 5,
			step = 1, category = "hamhacks.page.clickGui.category.appearance"
	)
	public float scale = 2;
	@KeySetting(name = "hamhacks.page.clickGui.openMenu", category = "hamhacks.page.clickGui.category.options")
	public Keybind openMenu = new Keybind(GLFW.GLFW_KEY_RIGHT_SHIFT);
	
	public boolean typing = false;
	
	public ClickGUI() {
		super(Text.translatable("hamhacks.page.clickGui"));
	}
	
	@EventListener
	public void tickEvent(EventTick e) {
		while(openMenu.wasPressed()) {
			if(!(mc.currentScreen instanceof ClickGUIScreen)) {
				mc.setScreen(new ClickGUIScreen(mc.currentScreen));
			}
		}
	}
	
	public boolean moveInScreen(Screen currentScreen) {
		return !typing && (currentScreen instanceof ClickGUIScreen || currentScreen instanceof ModuleSettingsScreen || currentScreen == null);
	}
}
