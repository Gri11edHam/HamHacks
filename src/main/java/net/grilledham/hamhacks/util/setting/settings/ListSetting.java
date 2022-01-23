package net.grilledham.hamhacks.util.setting.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.grilledham.hamhacks.util.setting.Setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListSetting extends Setting<List<String>> {
	
	public ListSetting(String name, String... vals) {
		super(name);
		JsonArray arr = new JsonArray();
		for(String s : vals) {
			arr.add(s);
		}
		value.add(name, arr);
		def = Arrays.stream(vals).toList();
	}
	
	@Override
	protected void updateValue(List<String> value) {
		JsonArray arr = new JsonArray();
		for(String s : value) {
			arr.add(s);
		}
		this.value.add(name, arr);
	}
	
	@Override
	public List<String> getValue() {
		List<String> toReturn = new ArrayList<>();
		for(JsonElement e : value.get(name).getAsJsonArray()) {
			toReturn.add(e.getAsString());
		}
		return toReturn;
	}
	
	public void add(String s) {
		value.get(name).getAsJsonArray().add(s);
	}
	
	public void remove(String s) {
		value.get(name).getAsJsonArray().remove(new JsonPrimitive(s));
	}
	
	public void removeAll(String s) {
		JsonPrimitive sPrim = new JsonPrimitive(s);
		while(value.get(name).getAsJsonArray().contains(sPrim)) {
			value.get(name).getAsJsonArray().remove(sPrim);
		}
	}
	
	public void clear() {
		for(int i = 0; i < value.get(name).getAsJsonArray().size(); i++) {
			value.get(name).getAsJsonArray().remove(i);
		}
	}
	
	public void set(int i, String val) {
		value.get(name).getAsJsonArray().set(i, new JsonPrimitive(val));
	}
}
