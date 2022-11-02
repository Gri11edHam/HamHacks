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
		float r;
		float g;
		float b;
		if(vals[1] == 0) {
			r = vals[2] * 255;
			g = vals[2] * 255;
			b = vals[2] * 255;
		} else {
			float h = vals[0] * 6;
			if(h == 6) h = 0;
			int i = (int)Math.floor(h);
			float f1 = vals[2] * (1 - vals[1]);
			float f2 = vals[2] * (1 - vals[1] * (h - i));
			float f3 = vals[2] * (1 - vals[1] * (1 - (h - i)));
			switch(i) {
				case 0 ->	{ r = vals[2];	g = f3;			b = f1;		 }
				case 1 ->	{ r = f2;		g = vals[2];	b = f1;		 }
				case 2 ->	{ r = f1;		g = vals[2];	b = f3;		 }
				case 3 ->	{ r = f1;		g = f2;			b = vals[2]; }
				case 4 ->	{ r = f3;		g = f1;			b = vals[2]; }
				default ->	{ r = vals[2];	g = f1;			b = f2;		 }
			}
			r = r * 255;
			g = g * 255;
			b = b * 255;
		}
		return ((int)(vals[3] * 255) << 24) + ((int)r << 16) + ((int)g << 8) + (int)b;
	}
	
	public static float[] toHSB(int c) {
		float[] vals = new float[4];
		float r = ((c >> 16) & 255) / 255f;
		float g = ((c >> 8) & 255) / 255f;
		float b = (c & 255) / 255f;
		float a = ((c >> 24) & 255) / 255f;
		float min = Floats.min(r, g, b);
		float max = Floats.max(r, g, b);
		float d = max - min;
		float H = 0;
		float S;
		float B = max;
		if(d == 0) {
			H = 0;
			S = 0;
		} else {
			S = d / max;
			float dr = (((max - r) / 6) + (d / 2)) / d;
			float dg = (((max - g) / 6) + (d / 2)) / d;
			float db = (((max - b) / 6) + (d / 2)) / d;
			if		(r == max)	H = db - dg;
			else if	(g == max)	H = (1 / 3f) + dr - db;
			else if	(b == max)	H = (2 / 3f) + dg - dr;
			if(H < 0) H += 1;
			if(H > 1) H -= 1;
		}
		vals[0] = H;
		vals[1] = S;
		vals[2] = B;
		vals[3] = a;
		return vals;
	}
}
