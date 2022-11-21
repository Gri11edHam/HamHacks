package net.grilledham.hamhacks.modules.render;

import net.grilledham.hamhacks.animation.Animation;
import net.grilledham.hamhacks.animation.AnimationBuilder;
import net.grilledham.hamhacks.animation.AnimationType;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.BoolSetting;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.minecraft.text.Text;

public class Fullbright extends Module {
	
	private final NumberSetting brightness = new NumberSetting("hamhacks.module.fullBright.brightness", 500, () -> true, 0, 1000, 1, false);
	
	private final BoolSetting smoothTransition = new BoolSetting("hamhacks.module.fullBright.smoothTransition", true, () -> true);
	
	private final Animation newBrightness = AnimationBuilder.create(AnimationType.IN_OUT_QUAD, 0.25).build();
	
	public Fullbright() {
		super(Text.translatable("hamhacks.module.fullBright"), Category.RENDER, new Keybind(0));
		GENERAL_CATEGORY.add(brightness);
		GENERAL_CATEGORY.add(smoothTransition);
	}
	
	@Override
	public String getHUDText() {
		return super.getHUDText() + " \u00a77" + (int)(double)brightness.get();
	}
	
	public float getBrightness(float original, float delta) {
		double nextBrightness;
		if(isEnabled()) {
			nextBrightness = brightness.get() / 100;
		} else {
			nextBrightness = original;
		}
		if(smoothTransition.get()) {
			newBrightness.set(nextBrightness);
			newBrightness.update();
		} else {
			newBrightness.setAbsolute(nextBrightness);
		}
		return (float)newBrightness.get();
	}
}
