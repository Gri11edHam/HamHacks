package net.grilledham.hamhacks.util.setting.settings;

import net.grilledham.hamhacks.util.setting.Setting;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

public class FloatSetting extends Setting<Float> {
	
	private final float min;
	private final float max;
	
	public FloatSetting(Text name, float val, float min, float max) {
		super(name);
		this.min = min;
		this.max = max;
		value.addProperty(((TranslatableTextContent)name.getContent()).getKey(), val);
		def = val;
	}
	
	@Override
	protected void updateValue(Float value) {
		if(value >= min && value <= max) {
			this.value.addProperty(((TranslatableTextContent)name.getContent()).getKey(), value);
		}
	}
	
	@Override
	public Float getValue() {
		return value.get(((TranslatableTextContent)name.getContent()).getKey()).getAsFloat();
	}
	
	public float getMin() {
		return min;
	}
	
	public float getMax() {
		return max;
	}
}
