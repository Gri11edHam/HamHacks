package net.grilledham.hamhacks.setting;

import com.google.gson.JsonElement;
import net.grilledham.hamhacks.gui.element.GuiElement;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

public abstract class Setting<T> {
	
	protected final Text name;
	protected final Text tooltip;
	
	protected T value;
	protected final T defaultValue;
	
	protected final ShouldShow shouldShow;
	
	public Setting(String name, T defaultValue, ShouldShow shouldShow) {
		this.name = Text.translatable(name);
		this.tooltip = Text.translatable(name + ".tooltip");
		
		this.value = defaultValue;
		this.defaultValue = defaultValue;
		
		this.shouldShow = shouldShow;
	}
	
	public String getName() {
		return name.getString();
	}
	
	public String getConfigName() {
		return ((TranslatableTextContent)name.getContent()).getKey();
	}
	
	public String getTooltip() {
		return tooltip.getString();
	}
	
	public boolean hasTooltip() {
		return !((TranslatableTextContent)tooltip.getContent()).getKey().equals(tooltip.getString());
	}
	
	public boolean shouldShow() {
		return shouldShow.shouldShow();
	}
	
	public T get() {
		return value;
	}
	
	public T getDefault() {
		return defaultValue;
	}
	
	public void set(T value) {
		boolean changed = this.value != value;
		this.value = value;
		if(changed) {
			onChange();
		}
	}
	
	public void reset() {
		boolean changed = this.value != this.defaultValue;
		this.value = this.defaultValue;
		if(changed) {
			onChange();
		}
	}
	
	public abstract GuiElement getElement(float x, float y, double scale);
	
	public abstract JsonElement save();
	
	public abstract void load(JsonElement e);
	
	public void onChange() {}
}
