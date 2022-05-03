package net.grilledham.hamhacks.util.setting.settings;

import com.google.gson.JsonObject;
import net.grilledham.hamhacks.util.setting.Setting;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class FloatSetting extends Setting<Float> {
	
	public FloatSetting(Text name, float val, float min, float max) {
		super(name);
		JsonObject obj = new JsonObject();
		obj.addProperty("value", val);
		obj.addProperty("min", min);
		obj.addProperty("max", max);
		value.add(((TranslatableText)name).getKey(), obj);
		def = val;
	}
	
	@Override
	protected void updateValue(Float value) {
		if(value >= this.value.get(((TranslatableText)name).getKey()).getAsJsonObject().get("min").getAsFloat() && value <= this.value.get(((TranslatableText)name).getKey()).getAsJsonObject().get("max").getAsFloat()) {
			this.value.get(((TranslatableText)name).getKey()).getAsJsonObject().addProperty("value", value);
		}
	}
	
	@Override
	public Float getValue() {
		return value.get(((TranslatableText)name).getKey()).getAsJsonObject().get("value").getAsFloat();
	}
	
	public float getMin() {
		return value.get(((TranslatableText)name).getKey()).getAsJsonObject().get("min").getAsFloat();
	}
	
	public float getMax() {
		return value.get(((TranslatableText)name).getKey()).getAsJsonObject().get("max").getAsFloat();
	}
}
