package net.grilledham.hamhacks.util;

public class ChromaUtil {
	
	private static float hue;
	
	private static long lastTime = System.currentTimeMillis();
	
	public static int getColor() {
		hue += (System.currentTimeMillis() - lastTime) / 10000f;
		hue %= 1;
		lastTime = System.currentTimeMillis();
		return Color.toRGB(hue, 1, 1, 1);
	}
}
