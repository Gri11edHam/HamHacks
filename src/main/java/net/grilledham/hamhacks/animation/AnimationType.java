package net.grilledham.hamhacks.animation;

public class AnimationType {
	
	public static final AnimationType EASE_IN_BACKWARD = new AnimationType(new CubicBezier(0.6,-0.28,0.74,0.05));
	public static final AnimationType EASE_OUT_BACKWARD = new AnimationType(new CubicBezier(0.18,0.89,0.32,1.28));
	public static final AnimationType EASE_IN_OUT_BACKWARD = new AnimationType(new CubicBezier(0.68,-0.55,0.27,1.55));
	
	public static final AnimationType EASE = new AnimationType(new CubicBezier(0.25, 0.1, 0.25, 1.0));
	public static final AnimationType EASE_IN = new AnimationType(new CubicBezier(0.42, 0, 1.0, 1.0));
	public static final AnimationType EASE_OUT = new AnimationType(new CubicBezier(0, 0, 0.58, 1.0));
	public static final AnimationType EASE_IN_OUT = new AnimationType(new CubicBezier(0.42, 0, 0.58, 1.0));
	
	public static final AnimationType LINEAR = new AnimationType(new LinearBezier());
	
	private final Bezier animation;
	
	public AnimationType(Bezier animation) {
		this.animation = animation;
	}
	
	public double getProgress(double t) {
		return animation.apply(t);
	}
	
	public boolean isComplete(double t, double check) {
		return animation.isComplete(t, check);
	}
	
	public interface Bezier {
	
		double apply(double t);
		boolean isComplete(double t, double check);
	}
	
	public static class LinearBezier implements Bezier {
		
		@Override
		public double apply(double t) {
			return t;
		}
		
		@Override
		public boolean isComplete(double t, double check) {
			return t == check;
		}
	}
	
	public static class CubicBezier implements Bezier {
		
		private final double x1, y1, x2, y2;
		
		public CubicBezier(double x1, double y1, double x2, double y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}
		
		@Override
		public double apply(double t) {
			return 3 * t * Math.pow(1 - t, 2) * y1 + 3 * (1 - t) * Math.pow(t, 2) * y2 + Math.pow(t, 3);
		}
		
		@Override
		public boolean isComplete(double t, double check) {
			double x = 3 * t * Math.pow(1 - t, 2) * x1 + 3 * (1 - t) * Math.pow(t, 2) * x2 + Math.pow(t, 3);
			return x == check;
		}
	}
}