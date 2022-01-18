package net.grilledham.hamhacks.modules.render;

import net.grilledham.hamhacks.gui.screens.ClickGUIScreen;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.setting.settings.ColorSetting;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class ClickGUI extends Module {
	
	public ColorSetting barColor;
	public ColorSetting bgColor;
	public ColorSetting textColor;
	
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
		settings.add(barColor);
		settings.add(bgColor);
		settings.add(textColor);
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		mc.setScreen(new ClickGUIScreen());
		enabled.setValue(false);
	}
}
