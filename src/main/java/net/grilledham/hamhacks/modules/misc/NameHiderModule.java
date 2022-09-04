package net.grilledham.hamhacks.modules.misc;

import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.setting.StringSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class NameHiderModule extends Module {
	
	@StringSetting(
			name = "hamhacks.module.nameHider.fakeName",
			defaultValue = "GrilledHam",
			placeholder = "Fake Name"
	)
	public String fakeName = "GrilledHam";
	
	private static NameHiderModule INSTANCE;
	
	public NameHiderModule() {
		super(Text.translatable("hamhacks.module.nameHider"), Category.MISC, new Keybind(0));
		INSTANCE = this;
	}
	
	public static NameHiderModule getInstance() {
		return INSTANCE;
	}
	
	public String modifyName(String text) {
		if(isEnabled()) {
			return text.replace(MinecraftClient.getInstance().getSession().getUsername(), fakeName);
		}
		return text;
	}
}
