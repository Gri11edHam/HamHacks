package net.grilledham.hamhacks.modules.render;

import net.grilledham.hamhacks.gui.screens.ModuleSettingsScreen;
import net.grilledham.hamhacks.gui.screens.NewClickGUIScreen;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.setting.settings.ColorSetting;
import net.grilledham.hamhacks.util.setting.settings.IntSetting;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class ClickGUI extends Module {
	
	public ColorSetting accentColor;
	public ColorSetting bgColor;
	public ColorSetting textColor;
	public IntSetting scale;
	
	private static ClickGUI INSTANCE;
	
	public ClickGUI() {
		super(new TranslatableText("module.hamhacks.clickgui"), Category.RENDER, new Keybind(GLFW.GLFW_KEY_RIGHT_SHIFT));
		INSTANCE = this;
	}
	
	public static ClickGUI getInstance() {
		return INSTANCE;
	}
	
	@Override
	public void addSettings() {
		super.addSettings();
		float[] barHSB = Color.RGBtoHSB(0xa4, 0, 0, new float[3]);
		accentColor = new ColorSetting(new TranslatableText("setting.clickgui.accentcolor"), barHSB[0], barHSB[1], barHSB[2], 1, false);
		bgColor = new ColorSetting(new TranslatableText("setting.clickgui.backgroundcolor"), 1, 0, 0, 0.5f, false);
		textColor = new ColorSetting(new TranslatableText("setting.clickgui.textcolor"), 1, 0, 1, 1, false);
		scale = new IntSetting(new TranslatableText("setting.clickgui.scale"), 2, 1, 5);
		addSetting(accentColor);
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
