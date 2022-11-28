package net.grilledham.hamhacks.modules.combat;

import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.minecraft.text.Text;

public class Reach extends Module {
	
	public final NumberSetting entityRange = new NumberSetting("hamhacks.module.reach.entityRange", 3, () -> true, 0, 6);
	
	public final NumberSetting blockRange = new NumberSetting("hamhacks.module.reach.blockRange", 4.5, () -> true, 0, 6);
	
	public Reach() {
		super(Text.translatable("hamhacks.module.reach"), Category.COMBAT, new Keybind(0));
		GENERAL_CATEGORY.add(entityRange);
		GENERAL_CATEGORY.add(blockRange);
	}
	
	@Override
	public String getHUDText() {
		return super.getHUDText() + " \u00a77" + String.format("%.2f|%.2f", entityRange.get(), blockRange.get());
	}
}
