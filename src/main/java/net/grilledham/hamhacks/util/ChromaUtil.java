package net.grilledham.hamhacks.util;

public class ChromaUtil {
	
	private static float hueTimer;
	
	private static long lastTime = System.currentTimeMillis();
	
	public static int getColor(float speed) {
		hueTimer += System.currentTimeMillis() - lastTime;
		float hue = ((hueTimer * speed) % 10000) / 10000f;
		lastTime = System.currentTimeMillis();
		return Color.toRGB(hue, 1, 1, 1);
	}
}
