package net.grilledham.hamhacks.setting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.gui.element.impl.ListSettingElement;

import java.util.ArrayList;
import java.util.List;

public class ListSetting extends Setting<List<String>> {
	
	public ListSetting(String name, List<String> defaultValue, ShouldShow shouldShow) {
		super(name, defaultValue, shouldShow);
		this.value = new ArrayList<>(defaultValue);
	}
	
	@Override
	public void set(List<String> value) {
		this.value.clear();
		this.value.addAll(value);
	}
	
	public void add(String value) {
		this.value.add(value);
	}
	
	public void remove(int index) {
		this.value.remove(index);
	}
	
	@Override
	public void reset() {
		this.value.clear();
		this.value.addAll(defaultValue);
	}
	
	@Override
	public GuiElement getElement(float x, float y, double scale) {
		return new ListSettingElement(x, y, scale, this);
	}
	
	@Override
	public JsonElement save() {
		JsonArray arr = new JsonArray();
		for(String s : value) {
			arr.add(s);
		}
		return arr;
	}
	
	@Override
	public void load(JsonElement e) {
		JsonArray arr = e.getAsJsonArray();
		value.clear();
		for(JsonElement el : arr) {
			value.add(el.getAsString());
		}
	}
}
