package net.grilledham.hamhacks.modules.combat;

import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.setting.NumberSetting;
import net.minecraft.text.Text;

public class Reach extends Module {
	
	@NumberSetting(
			name = "hamhacks.module.reach.entityRange",
			defaultValue = 3,
			min = 0,
			max = 8
	)
	public float entityRange = 3;
	
	@NumberSetting(
			name = "hamhacks.module.reach.blockRange",
			defaultValue = 4.5f,
			min = 0,
			max = 8
	)
	public float blockRange = 4.5f;
	
	private static Reach INSTANCE;
	
	public Reach() {
		super(Text.translatable("hamhacks.module.reach"), Category.COMBAT, new Keybind(0));
		INSTANCE = this;
	}
	
	public static Reach getInstance() {
		return INSTANCE;
	}
}
