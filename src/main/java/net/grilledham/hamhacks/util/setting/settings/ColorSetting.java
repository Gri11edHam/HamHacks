package net.grilledham.hamhacks.util.setting.settings;

import com.google.gson.JsonObject;
import net.grilledham.hamhacks.util.ChromaUtil;
import net.grilledham.hamhacks.util.setting.Setting;

import java.awt.*;

public class ColorSetting extends Setting<Float[]> {
	
	public ColorSetting(String name, float h, float s, float b, float a, boolean chroma) {
		super(name);
		JsonObject obj = new JsonObject();
		obj.addProperty("hue", h);
		obj.addProperty("saturation", s);
		obj.addProperty("brightness", b);
		obj.addProperty("alpha", a);
		obj.addProperty("chroma", chroma);
		value.add(name, obj);
	}
	
	@Override
	protected void updateValue(Float... value) {
		if(value.length == 3) {
			setHue(value[0]);
			setSaturation(value[1]);
			setBrightness(value[2]);
		} else if(value.length == 4) {
			setHue(value[0]);
			setSaturation(value[1]);
			setBrightness(value[2]);
			setAlpha(value[3]);
		} else {
			throw new IllegalArgumentException("Expected 3 or 4 floats but found " + value.length);
		}
	}
	
	public void setHue(float hue) {
		this.value.get(name).getAsJsonObject().addProperty("hue", hue);
	}
	
	public void setSaturation(float saturation) {
		this.value.get(name).getAsJsonObject().addProperty("saturation", saturation);
	}
	
	public void setBrightness(float brightness) {
		this.value.get(name).getAsJsonObject().addProperty("brightness", brightness);
	}
	
	public void setAlpha(float alpha) {
		this.value.get(name).getAsJsonObject().addProperty("alpha", alpha);
	}
	
	public void setChroma(boolean chroma) {
		this.value.get(name).getAsJsonObject().addProperty("chroma", chroma);
	}
	
	public boolean useChroma() {
		return value.get(name).getAsJsonObject().get("chroma").getAsBoolean();
	}
	
	@Override
	public Float[] getValue() {
		return new Float[]{getHue(), getSaturation(), getBrightness(), getAlpha()};
	}
	
	public float getHue() {
		float hue;
		if(value.get(name).getAsJsonObject().get("chroma").getAsBoolean()) {
			int c = ChromaUtil.getColor();
			int r = c >> 16 & 0xff;
			int g = c >> 8 & 0xff;
			int b = c & 0xff;
			hue = Color.RGBtoHSB(r, g, b, new float[3])[0];
		} else {
			hue = value.get(name).getAsJsonObject().get("hue").getAsFloat();
		}
		return hue;
	}
	
	public float getSaturation() {
		return value.get(name).getAsJsonObject().get("saturation").getAsFloat();
	}
	
	public float getBrightness() {
		return value.get(name).getAsJsonObject().get("brightness").getAsFloat();
	}
	
	public float getAlpha() {
		return value.get(name).getAsJsonObject().get("alpha").getAsFloat();
	}
	
	public int getRGB() {
		Float[] val = getValue();
		return (Color.HSBtoRGB(val[0], val[1], val[2]) & 0xffffff) + ((int)(val[3] * 0xff) << 24);
	}
}
