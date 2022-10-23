package net.grilledham.hamhacks.util.animation;

public class AnimationStage {
	
	private double progress = 0;
	private double prevProgress = 0;
	private double animationProgress = 0;
	private double animationAmount = 0;
	private double prevAnimationAmount = 0;
	
	private long lastTime = System.currentTimeMillis();
	
	private final AnimationType animationType;
	private double duration;
	
	private final boolean allowReverse;
	
	public AnimationStage(AnimationType animationType, double duration, boolean allowReverse) {
		this.animationType = animationType;
		this.duration = duration;
		this.allowReverse = allowReverse;
	}
	
	public void setAbsolute(double progress) {
		this.progress = progress;
		this.prevProgress = progress;
		this.animationProgress = 1;
		this.animationAmount = progress;
		this.prevAnimationAmount = progress;
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
	
	public void setDuration(double duration) {
		this.duration = duration;
	}
	
	public void update() {
		long lastTime = this.lastTime;
		long now = System.currentTimeMillis();
		this.lastTime = now;
		double progressMultiplier;
		
		if(prevProgress != progress) {
			prevAnimationAmount = animationAmount;
			animationProgress = 0;
		}
		if(animationProgress < 1 && duration != 0) {
			animationProgress += (now - lastTime) / (duration * 1000);
		} else {
			animationProgress = 1;
		}
		
		if(progress > animationAmount || !allowReverse) {
			progressMultiplier = animationType.getProgress(animationProgress);
		} else {
			progressMultiplier = 1 - animationType.getProgress(1 - animationProgress);
		}
		
		animationAmount = prevAnimationAmount + (progress - prevAnimationAmount) * progressMultiplier;
		prevProgress = progress;
	}
}
