package net.grilledham.hamhacks.modules.player;

import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.minecraft.text.Text;

public class Step extends Module {
	
	public final NumberSetting height = new NumberSetting("hamhacks.module.step.height", 1, () -> true, 0, 10);
	
	public Step() {
		super(Text.translatable("hamhacks.module.step"), Category.PLAYER, new Keybind(0));
		GENERAL_CATEGORY.add(height);
	}
	
	@Override
	public String getHUDText() {
		return super.getHUDText() + " \u00a77" + String.format("%.2f", height.get());
	}
}
