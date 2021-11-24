package net.grilledham.hamhacks.util;

import net.minecraft.client.MinecraftClient;

public class MouseUtil {
	
	private static boolean mouseMoved;
	
	private static int delay = 2;
	private static double prevX = -1;
	private static double prevY = -1;
	
	private static final MinecraftClient mc = MinecraftClient.getInstance();
	
	public static void checkForMouseMove() {
		double currX = mc.mouse.getX();
		double currY = mc.mouse.getY();
		
		if (prevX != -1 && prevY != -1) {
			mouseMoved = prevX != currX || prevY != currY;
		}
		
		if (delay == 0) {
			prevX = currX;
			prevY = currY;
			delay = 2;
		} else {
			delay --;
		}
	}
	
	public static boolean mouseMoved() {
		return mouseMoved;
	}
}
