package net.grilledham.hamhacks.util.setting.settings;

import net.grilledham.hamhacks.util.setting.Setting;

public class StringSetting extends Setting<String> {
	
	public StringSetting(String name, String str) {
		super(name);
		value.addProperty(name, str);
		def = str;
	}
	
	@Override
	protected void updateValue(String value) {
		this.value.addProperty(name, value);
	}
	
	@Override
	public String getValue() {
		return value.get(name).getAsString();
	}
}
