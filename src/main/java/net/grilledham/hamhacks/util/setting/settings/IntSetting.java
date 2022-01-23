package net.grilledham.hamhacks.util.setting.settings;

import com.google.gson.JsonObject;
import net.grilledham.hamhacks.util.setting.Setting;

public class IntSetting extends Setting<Integer> {
	
	public IntSetting(String name, int val, int min, int max) {
		super(name);
		JsonObject obj = new JsonObject();
		obj.addProperty("value", val);
		obj.addProperty("min", min);
		obj.addProperty("max", max);
		value.add(name, obj);
		def = val;
	}
	
	@Override
	protected void updateValue(Integer value) {
		if(value >= this.value.get(name).getAsJsonObject().get("min").getAsInt() && value <= this.value.get(name).getAsJsonObject().get("max").getAsInt()) {
			this.value.get(name).getAsJsonObject().addProperty("value", value);
		}
	}
	
	@Override
	public Integer getValue() {
		return value.get(name).getAsJsonObject().get("value").getAsInt();
	}
	
	public int getMin() {
		return value.get(name).getAsJsonObject().get("min").getAsInt();
	}
	
	public int getMax() {
		return value.get(name).getAsJsonObject().get("max").getAsInt();
	}
}
