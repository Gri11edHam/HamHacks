package net.grilledham.hamhacks.modules.render;

import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.BoolSetting;
import net.minecraft.text.Text;

public class Bob extends Module {

	public BoolSetting modelBobbingOnly = new BoolSetting("hamhacks.module.bob.modelBobbingOnly", true, () -> true);
	
	public BoolSetting noHurtCam = new BoolSetting("hamhacks.module.bob.noHurtCam", false, () -> true);
	
	public Bob() {
		super(Text.translatable("hamhacks.module.bob"), Category.RENDER, new Keybind());
		GENERAL_CATEGORY.add(modelBobbingOnly);
		GENERAL_CATEGORY.add(noHurtCam);
	}
}
