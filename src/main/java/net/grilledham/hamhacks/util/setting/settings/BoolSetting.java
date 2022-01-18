package net.grilledham.hamhacks.util.setting.settings;

import net.grilledham.hamhacks.util.setting.Setting;

public class BoolSetting extends Setting<Boolean> {
	
	public BoolSetting(String name, boolean bool) {
		super(name);
		value.addProperty(name, bool);
	}
	
	@Override
	protected void updateValue(Boolean value) {
		this.value.addProperty(name, value);
	}
	
	@Override
	public Boolean getValue() {
		return value.get(name).getAsBoolean();
	}
}
