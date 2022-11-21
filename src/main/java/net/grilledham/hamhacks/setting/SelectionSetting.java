package net.grilledham.hamhacks.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.gui.element.impl.SelectionSettingElement;
import net.minecraft.text.Text;

public class SelectionSetting extends Setting<Integer> {
	
	private final Text[] options;
	
	public SelectionSetting(String name, int defaultValue, ShouldShow shouldShow, String... options) {
		super(name, defaultValue, shouldShow);
		this.options = new Text[options.length];
		for(int i = 0; i < options.length; i++) {
			this.options[i] = Text.translatable(options[i]);
		}
	}
	
	public String[] options() {
		String[] options = new String[this.options.length];
		for(int i = 0; i < this.options.length; i++) {
			options[i] = this.options[i].getString();
		}
		return options;
	}
	
	@Override
	public void set(Integer value) {
		if(value > options.length || value < 0) {
			throw new IllegalArgumentException("Out of bounds: " + value + " | 0-" + options.length);
		}
		super.set(value);
	}
	
	@Override
	public GuiElement getElement(float x, float y, double scale) {
		return new SelectionSettingElement(x, y, scale, this);
	}
	
	@Override
	public JsonElement save() {
		return new JsonPrimitive(value);
	}
	
	@Override
	public void load(JsonElement e) {
		value = e.getAsInt();
	}
}
