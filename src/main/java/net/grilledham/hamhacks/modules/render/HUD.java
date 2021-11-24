package net.grilledham.hamhacks.modules.render;

import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.Setting;

public class HUD extends Module {
	
	public Setting showLogo;
	public Setting showFPS;
	public Setting showModules;
	public Setting barColor;
	public Setting bgColor;
	public Setting textColor;
	
	private static HUD INSTANCE;
	
	public HUD() {
		super("HUD", Category.RENDER, new Keybind(0));
		setEnabled(true);
		INSTANCE = this;
	}
	
	public static HUD getInstance() {
		return INSTANCE;
	}
	
	@Override
	public void addSettings() {
		super.addSettings();
		showLogo = new Setting("Show Logo", true);
		showFPS = new Setting("Show FPS", true);
		showModules = new Setting("Show Enabled Modules", true);
		barColor = new Setting("Bar Color", 0xff00a400);
		barColor.setChroma(true);
		bgColor = new Setting("Background Color", 0x80000000);
		textColor = new Setting("Text Color", 0xffffffff);
		textColor.setChroma(true);
		
		settings.add(showLogo);
		settings.add(showFPS);
		settings.add(showModules);
		settings.add(barColor);
		settings.add(bgColor);
		settings.add(textColor);
	}
}
