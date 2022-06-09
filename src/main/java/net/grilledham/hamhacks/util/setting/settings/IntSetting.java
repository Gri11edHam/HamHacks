package net.grilledham.hamhacks.util.setting.settings;

import com.google.gson.JsonObject;
import net.grilledham.hamhacks.util.setting.Setting;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

public class IntSetting extends Setting<Integer> {
	
	public IntSetting(Text name, int val, int min, int max) {
		super(name);
		JsonObject obj = new JsonObject();
		obj.addProperty("value", val);
		obj.addProperty("min", min);
		obj.addProperty("max", max);
		value.add(((TranslatableTextContent)name.getContent()).getKey(), obj);
		def = val;
	}
	
	@Override
	protected void updateValue(Integer value) {
		if(value >= this.value.get(((TranslatableTextContent)name.getContent()).getKey()).getAsJsonObject().get("min").getAsInt() && value <= this.value.get(((TranslatableTextContent)name.getContent()).getKey()).getAsJsonObject().get("max").getAsInt()) {
			this.value.get(((TranslatableTextContent)name.getContent()).getKey()).getAsJsonObject().addProperty("value", value);
		}
	}
	
	@Override
	public Integer getValue() {
		return value.get(((TranslatableTextContent)name.getContent()).getKey()).getAsJsonObject().get("value").getAsInt();
	}
	
	public int getMin() {
		return value.get(((TranslatableTextContent)name.getContent()).getKey()).getAsJsonObject().get("min").getAsInt();
	}
	
	public int getMax() {
		return value.get(((TranslatableTextContent)name.getContent()).getKey()).getAsJsonObject().get("max").getAsInt();
	}
}
