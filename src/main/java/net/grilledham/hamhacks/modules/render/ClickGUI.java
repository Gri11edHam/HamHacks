package net.grilledham.hamhacks.modules.render;

import net.grilledham.hamhacks.gui.screens.ClickGUIScreen;
import net.grilledham.hamhacks.gui.screens.ModuleSettingsScreen;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.Color;
import net.grilledham.hamhacks.util.setting.ColorSetting;
import net.grilledham.hamhacks.util.setting.NumberSetting;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ClickGUI extends Module {
	
	@ColorSetting(name = "hamhacks.module.clickGui.accentColor")
	public Color accentColor = Color.getDarkRed();
	@ColorSetting(name = "hamhacks.module.clickGui.backgroundColor")
	public Color bgColor = new Color(0, 0, 0, 0.5f);
	@ColorSetting(name = "hamhacks.module.clickGui.backgroundColorHovered")
	public Color bgColorHovered = new Color(0, 0, 1, 0.5f);
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
	
	private static ClickGUI INSTANCE;
	
	public ClickGUI() {
		super(Text.translatable("hamhacks.module.clickGui"), Category.RENDER, new Keybind(GLFW.GLFW_KEY_RIGHT_SHIFT));
		INSTANCE = this;
	}
	
	public static ClickGUI getInstance() {
		return INSTANCE;
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
		return currentScreen instanceof ClickGUIScreen || currentScreen instanceof ModuleSettingsScreen;
	}
}
