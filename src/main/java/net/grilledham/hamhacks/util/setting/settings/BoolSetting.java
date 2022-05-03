package net.grilledham.hamhacks.util.setting.settings;

import net.grilledham.hamhacks.util.setting.Setting;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class BoolSetting extends Setting<Boolean> {
	
	public BoolSetting(Text name, boolean bool) {
		super(name);
		value.addProperty(((TranslatableText)name).getKey(), bool);
		def = bool;
	}
	
	@Override
	protected void updateValue(Boolean value) {
		this.value.addProperty(((TranslatableText)name).getKey(), value);
	}
	
	@Override
	public Boolean getValue() {
		return value.get(((TranslatableText)name).getKey()).getAsBoolean();
	}
}
