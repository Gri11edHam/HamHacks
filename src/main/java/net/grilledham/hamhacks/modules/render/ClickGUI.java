package net.grilledham.hamhacks.modules.render;

import net.grilledham.hamhacks.gui.screens.ModuleSettingsScreen;
import net.grilledham.hamhacks.gui.screens.NewClickGUIScreen;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.setting.settings.ColorSetting;
import net.grilledham.hamhacks.util.setting.settings.IntSetting;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class ClickGUI extends Module {
	
	public ColorSetting barColor;
	public ColorSetting bgColor;
	public ColorSetting textColor;
	public IntSetting scale;
	
	private static ClickGUI INSTANCE;
	
	public ClickGUI() {
		super("Click GUI", Category.RENDER, new Keybind(GLFW.GLFW_KEY_RIGHT_SHIFT));
		INSTANCE = this;
	}
	
	public static ClickGUI getInstance() {
		return INSTANCE;
	}
	
	@Override
	public void addSettings() {
		super.addSettings();
		float[] barHSB = Color.RGBtoHSB(0xa4, 0, 0, new float[3]);
		barColor = new ColorSetting("Bar Color", barHSB[0], barHSB[1], barHSB[2], 1, false);
		bgColor = new ColorSetting("Background Color", 1, 0, 0, 0.5f, false);
		textColor = new ColorSetting("Text Color", 1, 0, 1, 1, false);
		scale = new IntSetting("Scale", 2, 1, 5);
		addSetting(barColor);
		addSetting(bgColor);
		addSetting(textColor);
		addSetting(scale);
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		if(!(mc.currentScreen instanceof NewClickGUIScreen)) {
			mc.setScreen(new NewClickGUIScreen(mc.currentScreen));
		}
		enabled.setValue(false);
	}
	
	public boolean moveInScreen(Screen currentScreen) {
		return currentScreen instanceof NewClickGUIScreen || currentScreen instanceof ModuleSettingsScreen;
	}
}
