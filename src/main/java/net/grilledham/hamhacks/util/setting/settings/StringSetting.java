package net.grilledham.hamhacks.util.setting.settings;

import net.grilledham.hamhacks.util.setting.Setting;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

public class StringSetting extends Setting<String> {
	
	public StringSetting(Text name, String str) {
		super(name);
		value.addProperty(((TranslatableTextContent)name.getContent()).getKey(), str);
		def = str;
	}
	
	@Override
	protected void updateValue(String value) {
		this.value.addProperty(((TranslatableTextContent)name.getContent()).getKey(), value);
	}
	
	@Override
	public String getValue() {
		return value.get(((TranslatableTextContent)name.getContent()).getKey()).getAsString();
	}
}
