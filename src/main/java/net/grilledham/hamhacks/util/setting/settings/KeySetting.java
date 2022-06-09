package net.grilledham.hamhacks.util.setting.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.util.setting.Setting;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

public class KeySetting extends Setting<Integer> {
	
	private final Keybind keybind;
	
	public KeySetting(Text name, Keybind keybind) {
		super(name);
		value.addProperty(((TranslatableTextContent)name.getContent()).getKey(), keybind.getKey());
		this.keybind = keybind;
		def = keybind.getKey();
	}
	
	@Override
	protected void updateValue(Integer value) {
		keybind.setKey(value);
		this.value.addProperty(((TranslatableTextContent)name.getContent()).getKey(), keybind.getKey());
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
		value.addProperty(((TranslatableTextContent)name.getContent()).getKey(), keybind.getKey());
		return super.getAsJsonObject();
	}
}
