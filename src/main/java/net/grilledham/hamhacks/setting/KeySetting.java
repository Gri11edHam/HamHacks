package net.grilledham.hamhacks.setting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.gui.element.impl.KeySettingElement;
import net.grilledham.hamhacks.modules.Keybind;

public class KeySetting extends Setting<Keybind> {
	
	public KeySetting(String name, Keybind defaultValue, ShouldShow shouldShow) {
		super(name, defaultValue, shouldShow);
		this.value = new Keybind(defaultValue.getKeyCombo());
	}
	
	@Override
	public void set(Keybind value) {
		this.value.setKey(value.getKeyCombo());
	}
	
	public void set(int... codes) {
		this.value.setKey(codes);
	}
	
	@Override
	public void reset() {
		this.value.setKey(this.defaultValue.getKeyCombo());
	}
	
	@Override
	public GuiElement getElement(float x, float y, double scale) {
		return new KeySettingElement(x, y, scale, this);
	}
	
	@Override
	public JsonElement save() {
		JsonArray arr = new JsonArray();
		for(int i : value.getKeyCombo()) {
			arr.add(i);
		}
		return arr;
	}
	
	@Override
	public void load(JsonElement e) {
		JsonArray arr = e.getAsJsonArray();
		int[] codes = new int[arr.size()];
		for(int i = 0; i < arr.size(); i++) {
			codes[i] = arr.get(i).getAsInt();
		}
		set(codes);
	}
}
