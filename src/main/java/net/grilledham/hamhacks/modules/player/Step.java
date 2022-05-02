package net.grilledham.hamhacks.modules.player;

import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.setting.settings.FloatSetting;
import net.minecraft.text.TranslatableText;

public class Step extends Module {
	
	private FloatSetting height;
	
	private float originalStepHeight;
	
	public Step() {
		super(new TranslatableText("module.hamhacks.step"), Category.PLAYER, new Keybind(0));
	}
	
	@Override
	public void addSettings() {
		super.addSettings();
		height = new FloatSetting("Height", 1f, 0f, 10f) {
			@Override
			protected void valueChanged() {
				super.valueChanged();
				if(isEnabled()) {
					mc.player.stepHeight = getValue();
				}
			}
		};
		addSetting(height);
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		originalStepHeight = mc.player.stepHeight;
		mc.player.stepHeight = height.getValue();
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		if(mc.player == null) {
			return;
		}
		mc.player.stepHeight = originalStepHeight;
	}
}
