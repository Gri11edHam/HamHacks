package net.grilledham.hamhacks.util.setting.settings;

import net.grilledham.hamhacks.util.setting.Setting;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class StringSetting extends Setting<String> {
	
	public StringSetting(Text name, String str) {
		super(name);
		value.addProperty(((TranslatableText)name).getKey(), str);
		def = str;
	}
	
	@Override
	protected void updateValue(String value) {
		this.value.addProperty(((TranslatableText)name).getKey(), value);
	}
	
	@Override
	public String getValue() {
		return value.get(((TranslatableText)name).getKey()).getAsString();
	}
}
