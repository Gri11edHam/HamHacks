package net.grilledham.hamhacks.util.math;

import net.minecraft.util.math.MathHelper;

public class DirectionHelper {
	
	private static final String[] DIRECTIONS = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};
	
	public static String fromRotation(double yaw) {
		return fromIndex(MathHelper.floor(yaw / 45.0 + 0.5) & (DIRECTIONS.length - 1));
	}
	
	public static String fromIndex(int i) {
		return DIRECTIONS[MathHelper.abs(i % DIRECTIONS.length)];
	}
}
