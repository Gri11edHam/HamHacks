package net.grilledham.hamhacks.util.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.grilledham.hamhacks.gui.screens.ModuleSettingsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

public abstract class Setting<T> {
	
	protected final Text name;
	
	protected Text toolTip;
	
	protected JsonObject value;
	
	protected T def;
	
	/**
	 * @param name The name of the setting
	 */
	public Setting(Text name) {
		this.name = name;
		this.toolTip = Text.translatable(getKey() + ".tooltip");
		value = new JsonObject();
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
		return toolTip.getString();
	}
	
	public boolean hasToolTip() {
		return !getToolTip().equals(getKey() + ".tooltip");
	}
	
	public String getName() {
		return name.getString();
	}
	
	public String getKey() {
		return ((TranslatableTextContent)name.getContent()).getKey();
	}
	
	public void set(JsonElement el) {
		value.add(((TranslatableTextContent)name.getContent()).getKey(), el);
		valueChanged();
	}
	
	public JsonObject getAsJsonObject() {
		return value;
	}
}
