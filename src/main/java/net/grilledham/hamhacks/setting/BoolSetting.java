package net.grilledham.hamhacks.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.gui.element.impl.BoolSettingElement;

public class BoolSetting extends Setting<Boolean> {
	
	public BoolSetting(String name, boolean defaultValue, ShouldShow shouldShow) {
		super(name, defaultValue, shouldShow);
	}
	
	public void toggle() {
		this.value = !this.value;
	}
	
	@Override
	public GuiElement getElement(float x, float y, double scale) {
		return new BoolSettingElement(x, y, scale, this);
	}
	
	@Override
	public JsonElement save() {
		return new JsonPrimitive(value);
	}
	
	@Override
	public void load(JsonElement e) {
		value = e.getAsBoolean();
	}
}
