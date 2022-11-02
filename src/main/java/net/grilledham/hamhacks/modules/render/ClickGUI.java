package net.grilledham.hamhacks.modules.render;

import net.grilledham.hamhacks.gui.screens.ClickGUIScreen;
import net.grilledham.hamhacks.gui.screens.ModuleSettingsScreen;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.ColorSetting;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.grilledham.hamhacks.util.Color;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ClickGUI extends Module {
	
	@ColorSetting(name = "hamhacks.module.clickGui.accentColor")
	public Color accentColor = Color.getDarkRed();
	@ColorSetting(name = "hamhacks.module.clickGui.backgroundColor")
	public Color bgColor = new Color(0x80000000);
	@ColorSetting(name = "hamhacks.module.clickGui.backgroundColorHovered")
	public Color bgColorHovered = new Color(0x80777777);
	@ColorSetting(name = "hamhacks.module.clickGui.enabledColor")
	public Color enabledColor = new Color(0x80AA0000);
	@ColorSetting(name = "hamhacks.module.clickGui.enabledColorHovered")
	public Color enabledColorHovered = new Color(0x80AA5555);
	@ColorSetting(name = "hamhacks.module.clickGui.textColor")
	public Color textColor = Color.getWhite();
	@NumberSetting(
			name = "hamhacks.module.clickGui.scale",
			defaultValue = 2,
			min = 1,
			max = 5,
			step = 1
	)
	public float scale = 2;
	
	public boolean typing = false;
	
	public ClickGUI() {
		super(Text.translatable("hamhacks.module.clickGui"), Category.RENDER, new Keybind(GLFW.GLFW_KEY_RIGHT_SHIFT));
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		if(!(mc.currentScreen instanceof ClickGUIScreen)) {
			mc.setScreen(new ClickGUIScreen(mc.currentScreen));
		}
		setEnabled(false);
	}
	
	public boolean moveInScreen(Screen currentScreen) {
		return !typing && (currentScreen instanceof ClickGUIScreen || currentScreen instanceof ModuleSettingsScreen || currentScreen == null);
	}
}
