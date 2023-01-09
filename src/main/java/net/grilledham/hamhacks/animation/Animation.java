package net.grilledham.hamhacks.animation;

public class Animation {
	
	private boolean complete = false;
	
	private double progress = 0;
	private double prevProgress = 0;
	private double animationProgress = 0;
	private double prevAnimationAmount = 0;
	private double animationAmount = 0;
	
	private long lastTime = System.currentTimeMillis();
	
	private final AnimationType animationType;
	private double duration;
	
	private final boolean allowReverse;
	
	public Animation(AnimationType animationType, double duration, boolean allowReverse) {
		this.animationType = animationType;
		this.duration = duration;
		this.allowReverse = allowReverse;
	}
	
	public Animation(AnimationType animationType, double duration) {
		this(animationType, duration, false);
	}
	
	public Animation(AnimationType animationType, boolean allowReverse) {
		this(animationType, 1, allowReverse);
	}
	
	public Animation(AnimationType animationType) {
		this(animationType, false);
	}
	
	public void setAbsolute(double progress) {
		this.progress = progress;
		this.prevProgress = progress;
		this.animationProgress = 1;
		this.prevAnimationAmount = progress;
		this.animationAmount = progress;
		complete = true;
	}
	
	public void setAbsolute(boolean progress) {
		if(progress) {
			setAbsolute(1);
		} else {
			setAbsolute(0);
		}
	}
	
	public void set(double progress) {
		this.progress = progress;
		complete = false;
	}
	
	public void set(boolean progress) {
		if(progress) {
			set(1);
		} else {
			set(0);
		}
	}
	
	public double getAbsolute() {
		return progress;
	}
	
	public double get() {
		return animationAmount;
	}
	
	public boolean isComplete() {
		return complete;
	}
	
	public void setDuration(double duration) {
		this.duration = duration;
	}
	
	public void update() {
		if(complete) {
			return;
		}
		long lastTime = this.lastTime;
		long now = System.currentTimeMillis();
		this.lastTime = now;
		double progressMultiplier;
		
		if(prevProgress != progress) {
			animationProgress = 0;
			prevAnimationAmount = animationAmount;
		}
		if(animationProgress < 1 && duration != 0) {
			animationProgress += (now - lastTime) / (duration * 1000);
		} else {
			animationProgress = 1;
		}
		
		if(progress > prevAnimationAmount || !allowReverse) {
			progressMultiplier = animationType.getProgress(animationProgress);
			complete = animationType.isComplete(animationProgress, 1);
		} else {
			progressMultiplier = 1 - animationType.getProgress(1 - animationProgress);
			complete = animationType.isComplete(1 - animationProgress, 0);
		}
		
		if(complete) {
			progressMultiplier = 1;
		}
		
		animationAmount = prevAnimationAmount + (progress - prevAnimationAmount) * progressMultiplier;
		prevProgress = progress;
	}
}
