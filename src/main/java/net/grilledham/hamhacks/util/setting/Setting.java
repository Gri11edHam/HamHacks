package net.grilledham.hamhacks.util.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.grilledham.hamhacks.gui.screens.ModuleSettingsScreen;
import net.minecraft.client.MinecraftClient;

public abstract class Setting<T> {
	
	protected final String name;
	
	protected String toolTip;
	
	protected JsonObject value;
	
	protected T def;
	
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
	
	public void updateScreenIfOpen() {
		if(MinecraftClient.getInstance().currentScreen instanceof ModuleSettingsScreen) {
			MinecraftClient.getInstance().currentScreen.init(MinecraftClient.getInstance(), MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight());
		}
	}
	
	public abstract T getValue();
	
	public void reset() {
		setValue(def);
	}
	
	public String getToolTip() {
		return toolTip;
	}
	
	public String getName() {
		return name;
	}
	
	public void set(JsonElement el) {
		value.add(name, el);
		valueChanged();
	}
	
	public JsonObject getAsJsonObject() {
		return value;
	}
}
