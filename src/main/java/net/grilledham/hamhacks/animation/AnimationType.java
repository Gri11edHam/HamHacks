package net.grilledham.hamhacks.animation;

import java.util.function.Function;

public enum AnimationType {
	
	IN_OUT_QUAD(t -> {
		if(t < 0.5) {
			return 2 * Math.pow(t, 2);
		} else {
			return 1 - Math.pow(-2 * t + 2, 2) / 2;
		}
	}),
	LINEAR(t -> t);
	
	private final Function<Double, Double> animation;
	
	AnimationType(Function<Double, Double> animation) {
		this.animation = animation;
	}
	
	double getProgress(double t) {
		return animation.apply(t);
	}
}