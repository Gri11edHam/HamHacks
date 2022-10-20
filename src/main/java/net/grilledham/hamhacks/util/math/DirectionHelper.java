package net.grilledham.hamhacks.util.math;

public class DirectionHelper {
	
	private static final String[] DIRECTIONS = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};
	
	public static String getDirection(double yaw) {
		return DIRECTIONS[(int)(yaw / 45.0 + 0.5) & (DIRECTIONS.length - 1)];
	}
}
