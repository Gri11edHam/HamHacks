package net.grilledham.hamhacks.modules.render;

import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.setting.settings.BoolSetting;
import net.grilledham.hamhacks.util.setting.settings.IntSetting;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class Fullbright extends Module {
	
	private IntSetting brightness;
	private BoolSetting smoothTransition;
	
	private static Fullbright INSTANCE;
	
	private float newBrightness;
	
	public Fullbright() {
		super(Text.translatable("module.hamhacks.fullbright"), Category.RENDER, new Keybind(0));
		INSTANCE = this;
	}
	
	@Override
	public void addSettings() {
		super.addSettings();
		brightness = new IntSetting(Text.translatable("setting.fullbright.brightness"), 500, 0, 1000);
		smoothTransition = new BoolSetting(Text.translatable("setting.fullbright.smoothtransition"), true);
		addSetting(brightness);
		addSetting(smoothTransition);
	}
	
	public static Fullbright getInstance() {
		return INSTANCE;
	}
	
	public float getBrightness(float original, float delta) {
		float nextBrightness;
		if(isEnabled()) {
			nextBrightness = brightness.getValue() / 100f;
		} else {
			nextBrightness = original;
		}
		if(smoothTransition.getValue()) {
			newBrightness = MathHelper.lerp(delta / 16f, newBrightness, nextBrightness);
		} else {
			newBrightness = nextBrightness;
		}
		return newBrightness;
	}
}
