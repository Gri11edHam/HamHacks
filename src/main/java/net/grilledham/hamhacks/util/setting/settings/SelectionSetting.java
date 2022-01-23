package net.grilledham.hamhacks.util.setting.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.grilledham.hamhacks.util.setting.Setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectionSetting extends Setting<String> {
	
	public SelectionSetting(String name, String val, String... vals) {
		super(name);
		if(!Arrays.asList(vals).contains(val)) {
			throw new IllegalArgumentException("Possibilities do not include the current value: " + Arrays.toString(vals) + " - " + val);
		}
		JsonObject obj = new JsonObject();
		obj.addProperty("selected", val);
		JsonArray values = new JsonArray();
		for(String s : vals) {
			values.add(s);
		}
		obj.add("values", values);
		value.add(name, obj);
		def = val;
	}
	
	@Override
	protected void updateValue(String value) {
		if(this.value.get(name).getAsJsonObject().get("values").getAsJsonArray().contains(new JsonPrimitive(value))) {
			this.value.get(name).getAsJsonObject().addProperty("selected", value);
		} else {
			throw new IllegalArgumentException("Possibilities do not include the value to be set");
		}
	}
	
	@Override
	public String getValue() {
		return value.get(name).getAsJsonObject().get("selected").getAsString();
	}
	
	public List<String> getPossibleValues() {
		List<String> l = new ArrayList<>();
		for(JsonElement val : value.get(name).getAsJsonObject().get("values").getAsJsonArray()) {
			l.add(val.getAsString());
		}
		return l;
	}
}
