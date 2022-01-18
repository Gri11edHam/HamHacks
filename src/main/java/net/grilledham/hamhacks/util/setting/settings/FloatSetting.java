package net.grilledham.hamhacks.util.setting.settings;

import com.google.gson.JsonObject;
import net.grilledham.hamhacks.util.setting.Setting;

public class FloatSetting extends Setting<Float> {
	
	public FloatSetting(String name, float val, float min, float max) {
		super(name);
		JsonObject obj = new JsonObject();
		obj.addProperty("value", val);
		obj.addProperty("min", min);
		obj.addProperty("max", max);
		value.add(name, obj);
	}
	
	@Override
	protected void updateValue(Float value) {
		this.value.get(name).getAsJsonObject().addProperty("value", value);
	}
	
	@Override
	public Float getValue() {
		return value.get(name).getAsJsonObject().get("value").getAsFloat();
	}
	
	public float getMin() {
		return value.get(name).getAsJsonObject().get("min").getAsFloat();
	}
	
	public float getMax() {
		return value.get(name).getAsJsonObject().get("max").getAsFloat();
	}
}
