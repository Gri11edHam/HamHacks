package net.grilledham.hamhacks.animation;

public class AnimationType {
	
	public static final AnimationType EASE_IN_BOUNCE = new AnimationType(new Bezier(0.6,-0.28,0.74,0.05));
	public static final AnimationType EASE_OUT_BOUNCE = new AnimationType(new Bezier(0.18,0.89,0.32,1.28));
	public static final AnimationType EASE_IN_OUT_BOUNCE = new AnimationType(new Bezier(0.68,-0.55,0.27,1.55));
	
	public static final AnimationType EASE = new AnimationType(new Bezier(0.25,0.1,0.25,1.0));
	public static final AnimationType EASE_IN = new AnimationType(new Bezier(0.42,0));
	public static final AnimationType EASE_OUT = new AnimationType(new Bezier(0.58,1.0));
	public static final AnimationType EASE_IN_OUT = new AnimationType(new Bezier(0.42,0, 0.58,1.0));
	
	public static final AnimationType LINEAR = new AnimationType(new Bezier());
	
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
	
	public static class Bezier {
		
		private final double[] x;
		private final double[] y;
		
		public Bezier(double... coords) {
			x = new double[coords.length / 2 + 2];
			y = new double[coords.length / 2 + 2];
			x[0] = 0;
			y[0] = 0;
			x[x.length - 1] = 1;
			y[y.length - 1] = 1;
			for(int i = 0; i < coords.length; i += 2) {
				x[i / 2 + 1] = coords[i];
				y[i / 2 + 1] = coords[i + 1];
			}
		}
		
		public double apply(double t) {
			int n = y.length - 1;
			double c = Math.pow(t, n);
			for(int i = 0; i <= n; i++) {
				double bc = binomialCoefficient(n, i);
				c += y[i] * (bc * Math.pow(t, i) * Math.pow(1 - t, n - i));
			}
			return c;
//			return 3 * t * Math.pow(1 - t, 2) * y1 + 3 * (1 - t) * Math.pow(t, 2) * y2 + Math.pow(t, 3);
		}
		
		public boolean isComplete(double t, double check) {
			int n = x.length - 1;
			double c = Math.pow(t, n);
			for(int i = 0; i <= n; i++) {
				c += x[i] * (binomialCoefficient(n, i) * Math.pow(t, i) * Math.pow(1 - t, n - i));
			}
			return c == check;
//			double x = 3 * t * Math.pow(1 - t, 2) * x1 + 3 * (1 - t) * Math.pow(t, 2) * x2 + Math.pow(t, 3);
//			return x == check;
		}
		
		private static double binomialCoefficient(int n, int k) {
			if(k > 0 && n > k) {
				return factorial(n) / (factorial(k) * factorial(n - k));
			} else {
				return 0;
			}
		}
		
		private static double factorial(int a) {
			int b = a--;
			for(; a > 1; a--) {
				b *= a;
			}
			return b;
		}
	}
}