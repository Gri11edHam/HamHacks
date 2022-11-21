package net.grilledham.hamhacks.modules.misc;

import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.StringSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class NameHider extends Module {
	
	public final StringSetting fakeName = new StringSetting("hamhacks.module.nameHider.fakeName", "GrilledHam", () -> true, "hamhacks.module.nameHider.fakeName.placeholder");
	
	public NameHider() {
		super(Text.translatable("hamhacks.module.nameHider"), Category.MISC, new Keybind(0));
		GENERAL_CATEGORY.add(fakeName);
	}
	
	public String modifyName(String text) {
		if(isEnabled() && !fakeName.get().equals("")) {
			return text.replace(MinecraftClient.getInstance().getSession().getUsername(), fakeName.get());
		}
		return text;
	}
}
