package net.grilledham.hamhacks.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.gui.element.impl.ColorSettingElement;
import net.grilledham.hamhacks.util.Color;

public class ColorSetting extends Setting<Color> {
	
	public ColorSetting(String name, Color defaultValue, ShouldShow shouldShow) {
		super(name, defaultValue, shouldShow);
		this.value = new Color(defaultValue.getRGB(), defaultValue.getChroma(), defaultValue.getChromaSpeed());
	}
	
	@Override
	public void set(Color value) {
		set(value.getRGB(), value.getChroma(), value.getChromaSpeed());
	}
	
	public void set(int value, boolean chroma, float chromaSpeed) {
		boolean changed = this.value.getRGB() != value || this.value.getChroma() != chroma || this.value.getChromaSpeed() != chromaSpeed;
		this.value.set(value);
		this.value.setChroma(chroma);
		this.value.setChromaSpeed(chromaSpeed);
		if(changed) {
			onChange();
		}
	}
	
	public void set(int value) {
		set(value, this.value.getChroma(), this.value.getChromaSpeed());
	}
	
	public void set(boolean chroma) {
		set(this.value.getRGB(), chroma, this.value.getChromaSpeed());
	}
	
	public void set(float chromaSpeed) {
		set(this.value.getRGB(), this.value.getChroma(), chromaSpeed);
	}
	
	public void set(float hue, float saturation, float brightness, float alpha, boolean chroma, float chromaSpeed) {
		boolean changed = this.value.getTrueHue() != hue || this.value.getSaturation() != saturation || this.value.getBrightness() != brightness || this.value.getAlpha() != alpha || this.value.getChroma() != chroma || this.value.getChromaSpeed() != chromaSpeed;
		this.value.set(hue, saturation, brightness, alpha);
		this.value.setChroma(chroma);
		this.value.setChromaSpeed(chromaSpeed);
		if(changed) {
			onChange();
		}
	}
	
	public void set(float hue, float saturation, float brightness, float alpha) {
		set(hue, saturation, brightness, alpha, this.value.getChroma(), this.value.getChromaSpeed());
	}
	
	@Override
	public void reset() {
		this.value.set(this.defaultValue.getRGB());
		this.value.setChroma(this.defaultValue.getChroma());
		this.value.setChromaSpeed(this.defaultValue.getChromaSpeed());
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
		obj.addProperty("chromaSpeed", value.getChromaSpeed());
		return obj;
	}
	
	@Override
	public void load(JsonElement e) {
		JsonObject obj = e.getAsJsonObject();
		float hue = obj.has("hue") ? obj.get("hue").getAsFloat() : defaultValue.getHue();
		float saturation = obj.has("saturation") ? obj.get("saturation").getAsFloat() : defaultValue.getSaturation();
		float brightness = obj.has("brightness") ? obj.get("brightness").getAsFloat() : defaultValue.getBrightness();
		float alpha = obj.has("alpha") ? obj.get("alpha").getAsFloat() : defaultValue.getAlpha();
		boolean chroma = obj.has("chroma") ? obj.get("chroma").getAsBoolean() : defaultValue.getChroma();
		float chromaSpeed = obj.has("chromaSpeed") ? obj.get("chromaSpeed").getAsFloat() : defaultValue.getChromaSpeed();
		set(hue, saturation, brightness, alpha, chroma, chromaSpeed);
	}
}
