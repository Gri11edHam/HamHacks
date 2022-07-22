package net.grilledham.hamhacks.modules.combat;

import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.setting.settings.FloatSetting;
import net.minecraft.text.Text;

public class Reach extends Module {
	
	public FloatSetting entityRange;
	public FloatSetting blockRange;
	
	private static Reach INSTANCE;
	
	public Reach() {
		super(Text.translatable("hamhacks.module.reach"), Text.translatable("hamhacks.module.reach.tooltip"), Category.COMBAT, new Keybind(0));
		INSTANCE = this;
	}
	
	@Override
	public void addSettings() {
		entityRange = new FloatSetting(Text.translatable("hamhacks.module.reach.entityRange"), 3.0f, 0, 8);
		blockRange = new FloatSetting(Text.translatable("hamhacks.module.reach.blockRange"), 4.5f, 0, 8);
		addSetting(entityRange);
		addSetting(blockRange);
	}
	
	public static Reach getInstance() {
		return INSTANCE;
	}
}
