package net.grilledham.hamhacks.modules.render;

import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.minecraft.text.Text;

public class HandRender extends Module {
	
	public final NumberSetting fovMultiplier = new NumberSetting("hamhacks.module.handRender.fovMulitplier", 1, () -> true, 0.25, 4);
	public final NumberSetting scale = new NumberSetting("hamhacks.module.handRender.scale", 1, () -> true, 0.25, 4);
	
	public HandRender() {
		super(Text.translatable("hamhacks.module.handRender"), Category.RENDER, new Keybind());
		GENERAL_CATEGORY.add(fovMultiplier);
		GENERAL_CATEGORY.add(scale);
	}
}
