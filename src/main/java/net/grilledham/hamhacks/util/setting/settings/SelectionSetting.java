package net.grilledham.hamhacks.util.setting.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.grilledham.hamhacks.util.setting.Setting;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectionSetting extends Setting<Text> {
	
	public SelectionSetting(Text name, Text val, Text... vals) {
		super(name);
		if(!Arrays.asList(vals).contains(val)) {
			throw new IllegalArgumentException("Possibilities do not include the current value: " + Arrays.toString(vals) + " - " + val);
		}
		JsonObject obj = new JsonObject();
		obj.addProperty("selected", ((TranslatableText)val).getKey());
		JsonArray values = new JsonArray();
		for(Text s : vals) {
			values.add(((TranslatableText)s).getKey());
		}
		obj.add("values", values);
		value.add(((TranslatableText)name).getKey(), obj);
		def = val;
	}
	
	@Override
	protected void updateValue(Text value) {
		if(this.value.get(((TranslatableText)name).getKey()).getAsJsonObject().get("values").getAsJsonArray().contains(new JsonPrimitive(((TranslatableText)value).getKey()))) {
			this.value.get(((TranslatableText)name).getKey()).getAsJsonObject().addProperty("selected", ((TranslatableText)value).getKey());
		} else {
			throw new IllegalArgumentException("Possibilities do not include the value to be set");
		}
	}
	
	@Override
	public Text getValue() {
		return new TranslatableText(value.get(((TranslatableText)name).getKey()).getAsJsonObject().get("selected").getAsString());
	}
	
	public List<Text> getPossibleValues() {
		List<Text> l = new ArrayList<>();
		for(JsonElement val : value.get(((TranslatableText)name).getKey()).getAsJsonObject().get("values").getAsJsonArray()) {
			l.add(new TranslatableText(val.getAsString()));
		}
		return l;
	}
}
