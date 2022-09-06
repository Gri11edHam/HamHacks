package net.grilledham.hamhacks.util;

import com.google.common.primitives.Floats;

public class Color {
	
	public static Color getBlack() {
		return new Color(0xFF000000); // 0
	}
	
	public static Color getDarkBlue() {
		return new Color(0xFF0000AA); // 1
	}
	public static Color getDarkGreen() {
		return new Color(0xFF00AA00); // 2
	}
	public static Color getDarkAqua() {
		return new Color(0xFF00AAAA); // 3
	}
	public static Color getDarkRed() {
		return new Color(0xFFAA0000); // 4
	}
	public static Color getDarkPurple() {
		return new Color(0xFFAA00AA); // 5
	}
	public static Color getGold() {
		return new Color(0xFFFFAA00); // 6
	}
	public static Color getGray() {
		return new Color(0xFFAAAAAA); // 7
	}
	public static Color getDarkGray() {
		return new Color(0xFF555555); // 8
	}
	public static Color getBlue() {
		return new Color(0xFF5555FF); // 9
	}
	public static Color getGreen() {
		return new Color(0xFF55FF55); // a
	}
	public static Color getAqua() {
		return new Color(0xFF55FFFF); // b
	}
	public static Color getRed() {
		return new Color(0xFFFF5555); // c
	}
	public static Color getLightPurple() {
		return new Color(0xFFFF55FF); // d
	}
	public static Color getYellow() {
		return new Color(0xFFFFFF55); // e
	}
	public static Color getWhite() {
		return new Color(0xFFFFFFFF); // f
	}
	
	private final float[] defaults;
	private final boolean defaultChroma;
	
	private float hue;
	private float saturation;
	private float brightness;
	private float alpha;
	
	private boolean chroma;
	
	public Color(int c) {
		this(c, false);
	}
	
	public Color(int c, boolean chroma) {
		this(toHSB(c), chroma);
	}
	
	public Color(float[] hsb) {
		this(hsb, false);
	}
	
	public Color(float[] hsb, boolean chroma) {
		this(hsb[0], hsb[1], hsb[2], hsb[3], chroma);
	}
	
	public Color(float h, float s, float b, float a) {
		this(h, s, b, a, false);
	}
	
	public Color(float h, float s, float b, float a, boolean chroma) {
		this.hue = h;
		this.saturation = s;
		this.brightness = b;
		this.alpha = a;
		this.chroma = chroma;
		defaults = new float[] {h, s, b, a};
		defaultChroma = chroma;
	}
	
	public void reset() {
		set(defaults);
		setChroma(defaultChroma);
	}
	
	public float[] getHSB() {
		return new float[] {getHue(), saturation, brightness, alpha};
	}
	
	public float getTrueHue() {
		return hue;
	}
	
	public float getHue() {
		if(chroma) {
			return toHSB(ChromaUtil.getColor())[0];
		}
		return hue;
	}
	
	public float getSaturation() {
		return saturation;
	}
	
	public float getBrightness() {
		return brightness;
	}
	
	public float getAlpha() {
		return alpha;
	}
	
	public boolean getChroma() {
		return chroma;
	}
	
	public int getRGB() {
		return toRGB(getHue(), getSaturation(), getBrightness(), getAlpha());
	}
	
	public void set(float[] vals) {
		set(vals[0], vals[1], vals[2], vals[3]);
	}
	
	public void set(float hue, float saturation, float brightness, float alpha) {
		this.hue = hue;
		this.saturation = saturation;
		this.brightness = brightness;
		this.alpha = alpha;
	}
	
	public void setHue(float hue) {
		this.hue = hue;
	}
	
	public void setSaturation(float saturation) {
		this.saturation = saturation;
	}
	
	public void setBrightness(float brightness) {
		this.brightness = brightness;
	}
	
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
	
	public void set(int rgb) {
		set(toHSB(rgb));
	}
	
	public void setRed(int r) {
		int rgb = getRGB();
		int a = rgb & 0xFF000000;
		int gb = rgb & 0x0000FFFF;
		set(a + r + gb);
	}
	
	public void setGreen(int g) {
		int rgb = getRGB();
		int ar = rgb & 0xFFFF0000;
		int b = rgb & 0x000000FF;
		set(ar + g + b);
	}
	
	public void setBlue(int b) {
		int rgb = getRGB();
		int arg = rgb & 0xFFFFFF00;
		set(arg + b);
	}
	
	public void setChroma(boolean chroma) {
		this.chroma = chroma;
	}
	
	public static int toRGB(float h, float s, float b, float a) {
		return toRGB(new float[] {h, s, b, a});
	}
	
	public static int toRGB(float[] vals) {
		vals[0] = vals[0] * 360;
		float c = vals[1] * vals[2];
		float x = c * (1 - Math.abs((vals[0] / 60) % 2 - 1));
		float m = vals[2] - c;
		float r;
		float g;
		float b;
		if(0 <= vals[0] && vals[0] < 60) {
			r = c;
			g = x;
			b = 0;
		} else if(60 <= vals[0] && vals[0] < 120) {
			r = x;
			g = c;
			b = 0;
		} else if(120 <= vals[0] && vals[0] < 180) {
			r = 0;
			g = c;
			b = x;
		} else if(180 <= vals[0] && vals[0] < 240) {
			r = 0;
			g = x;
			b = c;
		} else if(240 <= vals[0] && vals[0] < 300) {
			r = x;
			g = 0;
			b = c;
		} else {
			r = c;
			g = 0;
			b = x;
		}
		return ((int)(vals[3] * 255) << 24) + ((int)((r + m) * 255) << 16) + ((int)((g + m) * 255) << 8) + (int)((b + m) * 255);
	}
	
	public static float[] toHSB(int c) {
		float[] vals = new float[4];
		float r = ((c >> 16) & 255) / 255f;
		float g = ((c >> 8) & 255) / 255f;
		float b = (c & 255) / 255f;
		float a = ((c >> 24) & 255) / 255f;
		float cmax = Floats.max(r, g, b);
		float cmin = Floats.min(r, g, b);
		float delta = cmax - cmin;
		// alpha
		vals[3] = a;
		// brightness
		vals[2] = cmax;
		// saturation
		if (cmax != 0) {
			vals[1] = delta / cmax;
		} else {
			vals[1] = 0;
		}
		// hue
		if (vals[1] == 0) {
			vals[0] = 0;
		} else {
			float redc = (cmax - r) / delta;
			float greenc = (cmax - g) / delta;
			float bluec = (cmax - b) / delta;
			if (r == cmax) {
				vals[0] = bluec - greenc;
			} else if (g == cmax) {
				vals[0] = 2.0f + redc - bluec;
			} else {
				vals[0] = 4.0f + greenc - redc;
			}
			vals[0] = vals[0] / 6.0f;
			if (vals[0] < 0) {
				vals[0] = vals[0] + 1.0f;
			}
		}
		return vals;
	}
}
