package net.grilledham.hamhacks.util.setting.settings;

import net.grilledham.hamhacks.util.setting.Setting;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectionSetting extends Setting<Text> {
	
	private final List<String> values = new ArrayList<>();
	
	public SelectionSetting(Text name, Text val, Text... vals) {
		super(name);
		if(!Arrays.asList(vals).contains(val)) {
			throw new IllegalArgumentException("Possibilities do not include the current value: " + Arrays.toString(vals) + " - " + val);
		}
		for(Text s : vals) {
			values.add(((TranslatableText)s).getKey());
		}
		value.addProperty(((TranslatableText)name).getKey(), ((TranslatableText)val).getKey());
		def = val;
	}
	
	@Override
	protected void updateValue(Text value) {
		if(this.values.contains(((TranslatableText)value).getKey())) {
			this.value.addProperty(((TranslatableText)name).getKey(), ((TranslatableText)value).getKey());
		} else {
			throw new IllegalArgumentException("Possibilities do not include the value to be set");
		}
	}
	
	@Override
	public Text getValue() {
		return new TranslatableText(value.get(((TranslatableText)name).getKey()).getAsString());
	}
	
	public List<Text> getPossibleValues() {
		List<Text> l = new ArrayList<>();
		for(String val : values) {
			l.add(new TranslatableText(val));
		}
		return l;
	}
}
