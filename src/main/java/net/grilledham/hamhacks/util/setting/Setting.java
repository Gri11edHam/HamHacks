package net.grilledham.hamhacks.util.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class Setting<T> {
	
	protected final String name;
	
	protected String toolTip;
	
	protected JsonObject value;
	
	/**
	 * @param name The name of the setting
	 */
	public Setting(String name) {
		this.name = name;
		value = new JsonObject();
	}
	
	/**
	 * Allows you give the user more information about this setting
	 * @param toolTip What should the user know about this setting
	 */
	public void setToolTip(String toolTip) {
		this.toolTip = toolTip;
	}
	
	public void setValue(T value) {
		T lastValue = getValue();
		
		updateValue(value);
		
		if(this.value != lastValue) {
			valueChanged();
		}
	}
	
	protected abstract void updateValue(T value);
	
	protected void valueChanged() {
	}
	
	public abstract T getValue();
	
	public String getToolTip() {
		return toolTip;
	}
	
	public String getName() {
		return name;
	}
	
	public void set(JsonElement el) {
		value.add(name, el);
	}
	
	public JsonObject getAsJsonObject() {
		return value;
	}
}
