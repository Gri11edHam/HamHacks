package net.grilledham.hamhacks.util.setting.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.util.setting.Setting;

public class KeySetting extends Setting<Integer> {
	
	private final Keybind keybind;
	
	public KeySetting(String name, Keybind keybind) {
		super(name);
		value.addProperty(name, keybind.getKey());
		this.keybind = keybind;
		def = keybind.getKey();
	}
	
	@Override
	protected void updateValue(Integer value) {
		keybind.setKey(value);
		this.value.addProperty(name, keybind.getKey());
	}
	
	@Override
	public Integer getValue() {
		return keybind.getKey();
	}
	
	public Keybind getKeybind() {
		return keybind;
	}
	
	@Override
	public void set(JsonElement el) {
		super.set(el);
		keybind.setKey(el.getAsInt());
	}
	
	@Override
	public JsonObject getAsJsonObject() {
		value.addProperty(name, keybind.getKey());
		return super.getAsJsonObject();
	}
}
