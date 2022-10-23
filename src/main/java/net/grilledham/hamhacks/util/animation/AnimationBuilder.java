package net.grilledham.hamhacks.util.animation;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;

public class AnimationBuilder {
	
	private final List<Triple<AnimationType, Double, Boolean>> animationData;
	private double totalDuration;
	
	public AnimationBuilder(AnimationType animation, double duration, boolean allowReverse) {
		animationData = new ArrayList<>();
		animationData.add(new ImmutableTriple<>(animation, duration, allowReverse));
		totalDuration = duration;
	}
	
	public Animation build() {
		Animation animation = new Animation(totalDuration, animationData.size());
		for(int i = 0; i < animationData.size(); i++) {
			Triple<AnimationType, Double, Boolean> currentAnimationData = animationData.get(i);
			AnimationType animationType = currentAnimationData.getLeft();
			double duration = currentAnimationData.getMiddle();
			boolean allowReverse = currentAnimationData.getRight();
			animation.setStage(i, new AnimationStage(animationType, duration, allowReverse), duration);
		}
		return animation;
	}
	
	public AnimationBuilder then(AnimationType animation, double duration, boolean allowReverse) {
		animationData.add(new ImmutableTriple<>(animation, duration, allowReverse));
		totalDuration += duration;
		return this;
	}
	
	public AnimationBuilder then(AnimationType animation, double duration) {
		return then(animation, duration, false);
	}
	
	public AnimationBuilder then(AnimationType animation, boolean allowReverse) {
		return then(animation, 1, allowReverse);
	}
	
	public AnimationBuilder then(AnimationType animation) {
		return then(animation, false);
	}
	
	public static AnimationBuilder create(AnimationType animation, double duration, boolean allowReverse) {
		return new AnimationBuilder(animation, duration, allowReverse);
	}
	
	public static AnimationBuilder create(AnimationType animation, double duration) {
		return create(animation, duration, false);
	}
	
	public static AnimationBuilder create(AnimationType animation, boolean allowReverse) {
		return create(animation, 1, allowReverse);
	}
	
	public static AnimationBuilder create(AnimationType animation) {
		return create(animation, false);
	}
}
