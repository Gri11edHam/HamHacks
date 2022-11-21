package net.grilledham.hamhacks.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.gui.element.impl.StringSettingElement;
import net.minecraft.text.Text;

public class StringSetting extends Setting<String> {
	
	private final Text placeholder;
	
	public StringSetting(String name, String defaultValue, ShouldShow shouldShow, String placeholder) {
		super(name, defaultValue, shouldShow);
		this.placeholder = Text.translatable(placeholder);
	}
	
	public StringSetting(String name, String defaultValue, ShouldShow shouldShow) {
		this(name, defaultValue, shouldShow, "hamhacks.setting.defaultStringPlaceholder");
	}
	
	public String placeholder() {
		return placeholder.getString();
	}
	
	@Override
	public GuiElement getElement(float x, float y, double scale) {
		return new StringSettingElement(x, y, scale, this);
	}
	
	@Override
	public JsonElement save() {
		return new JsonPrimitive(value);
	}
	
	@Override
	public void load(JsonElement e) {
		value = e.getAsString();
	}
}
