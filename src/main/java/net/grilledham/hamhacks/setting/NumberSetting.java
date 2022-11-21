package net.grilledham.hamhacks.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.gui.element.impl.NumberSettingElement;

public class NumberSetting extends Setting<Double> {
	
	private final double min;
	private final double max;
	
	private final double step;
	private final boolean forceStep;
	
	public NumberSetting(String name, double defaultValue, ShouldShow shouldShow, double min, double max, double step, boolean forceStep) {
		super(name, defaultValue, shouldShow);
		this.min = min;
		this.max = max;
		this.step = step;
		this.forceStep = forceStep;
	}
	
	public NumberSetting(String name, double defaultValue, ShouldShow shouldShow, double min, double max, double step) {
		this(name, defaultValue, shouldShow, min, max, step, true);
	}
	
	public NumberSetting(String name, double defaultValue, ShouldShow shouldShow, double min, double max) {
		this(name, defaultValue, shouldShow, min, max, -1);
	}
	
	public double min() {
		return min;
	}
	
	public double max() {
		return max;
	}
	
	public double step() {
		return step;
	}
	
	public boolean forceStep() {
		return forceStep;
	}
	
	@Override
	public void set(Double value) {
		if(value > max) {
			this.value = max;
		} else if(value < min) {
			this.value = min;
		} else {
			this.value = value;
		}
	}
	
	public void increment() {
		set(value + 1);
	}
	
	public void decrement() {
		set(value - 1);
	}
	
	@Override
	public GuiElement getElement(float x, float y, double scale) {
		return new NumberSettingElement(x, y, scale, this);
	}
	
	@Override
	public JsonElement save() {
		return new JsonPrimitive(value);
	}
	
	@Override
	public void load(JsonElement e) {
		value = e.getAsDouble();
	}
}
