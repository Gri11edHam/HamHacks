package net.grilledham.hamhacks.util.setting.settings;

import net.grilledham.hamhacks.util.setting.Setting;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

public class IntSetting extends Setting<Integer> {
	
	private final int min;
	private final int max;
	
	public IntSetting(Text name, int val, int min, int max) {
		super(name);
		this.min = min;
		this.max = max;
		value.addProperty(((TranslatableTextContent)name.getContent()).getKey(), val);
		def = val;
	}
	
	@Override
	protected void updateValue(Integer value) {
		if(value >= min && value <= max) {
			this.value.addProperty(((TranslatableTextContent)name.getContent()).getKey(), value);
		}
	}
	
	@Override
	public Integer getValue() {
		return value.get(((TranslatableTextContent)name.getContent()).getKey()).getAsInt();
	}
	
	public int getMin() {
		return min;
	}
	
	public int getMax() {
		return max;
	}
}
