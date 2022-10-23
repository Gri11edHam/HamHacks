package net.grilledham.hamhacks.modules.render;

import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.animation.Animation;
import net.grilledham.hamhacks.util.animation.AnimationBuilder;
import net.grilledham.hamhacks.util.animation.AnimationType;
import net.grilledham.hamhacks.util.setting.BoolSetting;
import net.grilledham.hamhacks.util.setting.NumberSetting;
import net.minecraft.text.Text;

public class Fullbright extends Module {
	
	@NumberSetting(
			name = "hamhacks.module.fullBright.brightness",
			defaultValue = 500,
			min = 0,
			max = 1000,
			step = 1,
			forceStep = false
	)
	public float brightness = 500;
	
	@BoolSetting(name = "hamhacks.module.fullBright.smoothTransition", defaultValue = true)
	public boolean smoothTransition = true;
	
	private final Animation newBrightness = AnimationBuilder.create(AnimationType.IN_OUT_QUAD, 0.25).build();
	
	public Fullbright() {
		super(Text.translatable("hamhacks.module.fullBright"), Category.RENDER, new Keybind(0));
	}
	
	@Override
	public String getHUDText() {
		return super.getHUDText() + " \u00a77" + (int)brightness;
	}
	
	public float getBrightness(float original, float delta) {
		float nextBrightness;
		if(isEnabled()) {
			nextBrightness = brightness / 100f;
		} else {
			nextBrightness = original;
		}
		if(smoothTransition) {
			newBrightness.set(nextBrightness);
			newBrightness.update();
		} else {
			newBrightness.setAbsolute(nextBrightness);
		}
		return (float)newBrightness.get();
	}
}
