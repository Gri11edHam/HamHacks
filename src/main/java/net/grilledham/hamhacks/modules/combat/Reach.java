package net.grilledham.hamhacks.modules.combat;

import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.setting.settings.FloatSetting;

public class Reach extends Module {
	
	public FloatSetting range;
	
	private static Reach INSTANCE;
	
	public Reach() {
		super("Reach", Category.COMBAT, new Keybind(0));
		INSTANCE = this;
	}
	
	@Override
	public void addSettings() {
		range = new FloatSetting("Range", 3.2f, 0, 8);
		addSetting(range);
	}
	
	public static Reach getInstance() {
		return INSTANCE;
	}
}
