package net.grilledham.hamhacks.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.gui.element.impl.ColorSettingElement;
import net.grilledham.hamhacks.util.Color;

public class ColorSetting extends Setting<Color> {
	
	public ColorSetting(String name, Color defaultValue, ShouldShow shouldShow) {
		super(name, defaultValue, shouldShow);
		this.value = new Color(defaultValue.getRGB(), defaultValue.getChroma());
	}
	
	@Override
	public void set(Color value) {
		set(value.getRGB(), value.getChroma());
	}
	
	public void set(int value, boolean chroma) {
		boolean changed = this.value.getRGB() != value || this.value.getChroma() != chroma;
		this.value.set(value);
		this.value.setChroma(chroma);
		if(changed) {
			onChange();
		}
	}
	
	public void set(int value) {
		set(value, this.value.getChroma());
	}
	
	public void set(boolean chroma) {
		set(this.value.getRGB(), chroma);
	}
	
	public void set(float hue, float saturation, float brightness, float alpha, boolean chroma) {
		boolean changed = this.value.getTrueHue() != hue || this.value.getSaturation() != saturation || this.value.getBrightness() != brightness || this.value.getAlpha() != alpha || this.value.getChroma() != chroma;
		this.value.set(hue, saturation, brightness, alpha);
		this.value.setChroma(chroma);
		if(changed) {
			onChange();
		}
	}
	
	public void set(float hue, float saturation, float brightness, float alpha) {
		set(hue, saturation, brightness, alpha, this.value.getChroma());
	}
	
	@Override
	public void reset() {
		this.value.set(this.defaultValue.getRGB());
		this.value.setChroma(this.defaultValue.getChroma());
	}
	
	@Override
	public GuiElement getElement(float x, float y, double scale) {
		return new ColorSettingElement(x, y, scale, this);
	}
	
	@Override
	public JsonElement save() {
		JsonObject obj = new JsonObject();
		obj.addProperty("hue", value.getHue());
		obj.addProperty("saturation", value.getSaturation());
		obj.addProperty("brightness", value.getBrightness());
		obj.addProperty("alpha", value.getAlpha());
		obj.addProperty("chroma", value.getChroma());
		return obj;
	}
	
	@Override
	public void load(JsonElement e) {
		JsonObject obj = e.getAsJsonObject();
		value.set(obj.get("hue").getAsFloat(), obj.get("saturation").getAsFloat(), obj.get("brightness").getAsFloat(), obj.get("alpha").getAsFloat());
		value.setChroma(obj.get("chroma").getAsBoolean());
	}
}
