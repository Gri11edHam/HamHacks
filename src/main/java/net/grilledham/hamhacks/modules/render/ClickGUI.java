package net.grilledham.hamhacks.modules.render;

import net.grilledham.hamhacks.gui.ClickGUIScreen;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.Setting;
import org.lwjgl.glfw.GLFW;

public class ClickGUI extends Module {
	
	public Setting barColor;
	public Setting bgColor;
	public Setting textColor;
	
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
		barColor = new Setting("Bar Color", 0xffa40000);
		bgColor = new Setting("Background Color", 0x80000000);
		textColor = new Setting("Text Color", 0xffffffff);
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
