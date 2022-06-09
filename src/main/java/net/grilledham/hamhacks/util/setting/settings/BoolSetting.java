package net.grilledham.hamhacks.util.setting.settings;

import net.grilledham.hamhacks.util.setting.Setting;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

public class BoolSetting extends Setting<Boolean> {
	
	public BoolSetting(Text name, boolean bool) {
		super(name);
		value.addProperty(((TranslatableTextContent)name.getContent()).getKey(), bool);
		def = bool;
	}
	
	@Override
	protected void updateValue(Boolean value) {
		this.value.addProperty(((TranslatableTextContent)name.getContent()).getKey(), value);
	}
	
	@Override
	public Boolean getValue() {
		return value.get(((TranslatableTextContent)name.getContent()).getKey()).getAsBoolean();
	}
}
