package net.grilledham.hamhacks.modules.combat;

import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.setting.settings.FloatSetting;

public class Reach extends Module {
	
	public FloatSetting entityRange;
	public FloatSetting blockRange;
	
	private static Reach INSTANCE;
	
	public Reach() {
		super("Reach", Category.COMBAT, new Keybind(0));
		INSTANCE = this;
	}
	
	@Override
	public void addSettings() {
		entityRange = new FloatSetting("Entity Range", 3.0f, 0, 8);
		blockRange = new FloatSetting("Block Range", 4.5f, 0, 8);
		addSetting(entityRange);
		addSetting(blockRange);
	}
	
	public static Reach getInstance() {
		return INSTANCE;
	}
}
