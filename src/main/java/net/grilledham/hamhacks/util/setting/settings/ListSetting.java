package net.grilledham.hamhacks.util.setting.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.grilledham.hamhacks.util.setting.Setting;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListSetting extends Setting<List<String>> {
	
	public ListSetting(Text name, String... vals) {
		super(name);
		JsonArray arr = new JsonArray();
		for(String s : vals) {
			arr.add(s);
		}
		value.add(((TranslatableTextContent)name.getContent()).getKey(), arr);
		def = Arrays.stream(vals).toList();
	}
	
	@Override
	protected void updateValue(List<String> value) {
		JsonArray arr = new JsonArray();
		for(String s : value) {
			arr.add(s);
		}
		this.value.add(((TranslatableTextContent)name.getContent()).getKey(), arr);
	}
	
	@Override
	public List<String> getValue() {
		List<String> toReturn = new ArrayList<>();
		for(JsonElement e : value.get(((TranslatableTextContent)name.getContent()).getKey()).getAsJsonArray()) {
			toReturn.add(e.getAsString());
		}
		return toReturn;
	}
	
	public void add(String s) {
		value.get(((TranslatableTextContent)name.getContent()).getKey()).getAsJsonArray().add(s);
	}
	
	public void remove(String s) {
		value.get(((TranslatableTextContent)name.getContent()).getKey()).getAsJsonArray().remove(new JsonPrimitive(s));
	}
	
	public void removeAll(String s) {
		JsonPrimitive sPrim = new JsonPrimitive(s);
		while(value.get(((TranslatableTextContent)name.getContent()).getKey()).getAsJsonArray().contains(sPrim)) {
			value.get(((TranslatableTextContent)name.getContent()).getKey()).getAsJsonArray().remove(sPrim);
		}
	}
	
	public void clear() {
		for(int i = 0; i < value.get(((TranslatableTextContent)name.getContent()).getKey()).getAsJsonArray().size(); i++) {
			value.get(((TranslatableTextContent)name.getContent()).getKey()).getAsJsonArray().remove(i);
		}
	}
	
	public void set(int i, String val) {
		value.get(((TranslatableTextContent)name.getContent()).getKey()).getAsJsonArray().set(i, new JsonPrimitive(val));
	}
}
