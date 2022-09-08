package net.grilledham.hamhacks.util;

public class Animation {
	
	private double animation = 0;
	private double prevAnimation = 0;
	private double animationProgress = 0;
	private double animationAmount = 0;
	private double prevAnimationAmount = 0;
	
	private long lastTime = System.currentTimeMillis();
	
	private final AnimationProgress progress;
	private double speed;
	
	private final boolean allowReverse;
	
	private Animation(AnimationProgress animation, double speed, boolean allowReverse) {
		this.progress = animation;
		this.speed = speed;
		this.allowReverse = allowReverse;
	}
	
	public void setAbsolute(double animation) {
		this.animation = animation;
		this.prevAnimation = animation;
		this.animationProgress = 1;
		this.animationAmount = animation;
		this.prevAnimationAmount = animation;
	}
	
	public void setAbsolute(boolean animation) {
		if(animation) {
			setAbsolute(1);
		} else {
			setAbsolute(0);
		}
	}
	
	public void set(double animation) {
		this.animation = animation;
	}
	
	public void set(boolean animation) {
		if(animation) {
			set(1);
		} else {
			set(0);
		}
	}
	
	public double get() {
		return animationAmount;
	}
	
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	public void update() {
		long lastTime = this.lastTime;
		long now = System.currentTimeMillis();
		this.lastTime = now;
		double progressMultiplier;
		
		if(prevAnimation != animation) {
			prevAnimationAmount = animationAmount;
			animationProgress = 0;
		}
		if(animationProgress < 1 && speed != 0) {
			animationProgress += (now - lastTime) / (speed * 1000);
		} else {
			animationProgress = 1;
		}
		
		if(animation > animationAmount || !allowReverse) {
			progressMultiplier = progress.getProgress(animationProgress);
		} else {
			progressMultiplier = 1 - progress.getProgress(1 - animationProgress);
		}
		
		animationAmount = prevAnimationAmount + (animation - prevAnimationAmount) * progressMultiplier;
		prevAnimation = animation;
	}
	
	public static Animation getAnimation(AnimationProgress animation, double speed, boolean allowReverse) {
		return new Animation(animation, speed, allowReverse);
	}
	
	public static Animation getInOutQuad(double speed, boolean allowReverse) {
		return getAnimation(t -> {
			if(t < 0.5) {
				return 2 * Math.pow(t, 2);
			} else {
				return 1 - Math.pow(-2 * t + 2, 2) / 2;
			}
		}, speed, allowReverse);
	}
	
	public static Animation getInOutQuad(double speed) {
		return getInOutQuad(speed, false);
	}
	
	public static Animation getInOutQuad(boolean allowReverse) {
		return getInOutQuad(0.5, allowReverse);
	}
	
	public static Animation getInOutQuad() {
		return getInOutQuad(0.5);
	}
	
	@FunctionalInterface
	public interface AnimationProgress {
		double getProgress(double t);
	}
}
