package net.grilledham.hamhacks.animation;

public class Animation {
	
	private final AnimationStage[] stages;
	private final double[] originalDurations;
	
	private final double originalDuration;
	
	private int currentStage = 0;
	
	public Animation(double duration, int stages) {
		this.originalDuration = duration;
		this.stages = new AnimationStage[stages];
		this.originalDurations = new double[stages];
	}
	
	public void setStage(int i, AnimationStage stage, double stageDuration) {
		stages[i] = stage;
		originalDurations[i] = stageDuration;
	}
	
	public void setAbsolute(double progress) {
		currentStage = (int)((stages.length - 1) * progress);
		if(currentStage > 0) {
			for(int i = 0; i < currentStage - 1; i++) {
				stages[i].setAbsolute(1);
			}
		}
		stages[currentStage].setAbsolute(progress / stages.length);
		if(currentStage < stages.length - 1) {
			for(int i = currentStage + 1; i < stages.length; i++) {
				stages[i].setAbsolute(0);
			}
		}
	}
	
	public void setAbsolute(boolean progress) {
		if(progress) {
			setAbsolute(1);
		} else {
			setAbsolute(0);
		}
	}
	
	public void set(double progress) {
		int progressStage = (int)((stages.length - 1) * progress);
		if(progressStage > 0) {
			for(int i = 0; i < progressStage; i++) {
				stages[i].set(1);
			}
		}
		stages[progressStage].set(progress / stages.length);
		if(progressStage < stages.length - 1) {
			for(int i = progressStage + 1; i < stages.length; i++) {
				stages[i].set(0);
			}
		}
	}
	
	public void set(boolean progress) {
		if(progress) {
			set(1);
		} else {
			set(0);
		}
	}
	
	public int getCurrentStage() {
		return currentStage;
	}
	
	public double getStage() {
		return stages[currentStage].get();
	}
	
	public double get() {
		return (currentStage + stages[currentStage].get()) / stages.length;
	}
	
	public void setDuration(double duration) {
		double percentageOfOriginal = duration / originalDuration;
		for(int i = 0; i < stages.length; i++) {
			stages[i].setDuration(originalDurations[i] * percentageOfOriginal);
		}
	}
	
	public void update() {
		stages[currentStage].update();
		// if there is more than one stage, the progress can only be between 1 and 0
		if(stages[currentStage].get() >= 1 && currentStage < stages.length - 1) {
			stages[currentStage].setAbsolute(1);
			currentStage++;
			double d = stages[currentStage].getAbsolute();
			stages[currentStage].setAbsolute(0);
			stages[currentStage].set(d);
		} else if(stages[currentStage].get() <= 0 && currentStage > 0) {
			stages[currentStage].setAbsolute(0);
			currentStage--;
			double d = stages[currentStage].getAbsolute();
			stages[currentStage].setAbsolute(1);
			stages[currentStage].set(d);
		}
	}
}
