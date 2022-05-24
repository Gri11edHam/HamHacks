package net.grilledham.hamhacks.modules.combat;

import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.setting.settings.FloatSetting;
import net.minecraft.text.TranslatableText;

public class Reach extends Module {
	
	public FloatSetting entityRange;
	public FloatSetting blockRange;
	
	private static Reach INSTANCE;
	
	public Reach() {
		super(new TranslatableText("module.hamhacks.reach"), new TranslatableText("module.hamhacks.reach.tooltip"), Category.COMBAT, new Keybind(0));
		INSTANCE = this;
	}
	
	@Override
	public void addSettings() {
		entityRange = new FloatSetting(new TranslatableText("setting.reach.entityrange"), 3.0f, 0, 8);
		blockRange = new FloatSetting(new TranslatableText("setting.reach.blockrange"), 4.5f, 0, 8);
		addSetting(entityRange);
		addSetting(blockRange);
	}
	
	public static Reach getInstance() {
		return INSTANCE;
	}
}
