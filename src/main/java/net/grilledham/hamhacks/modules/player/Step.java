package net.grilledham.hamhacks.modules.player;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.minecraft.text.Text;

public class Step extends Module {
	
	@NumberSetting(
			name = "hamhacks.module.step.height",
			defaultValue = 1,
			min = 0,
			max = 10
	)
	public float height = 1;
	
	private float originalStepHeight;
	
	public Step() {
		super(Text.translatable("hamhacks.module.step"), Category.PLAYER, new Keybind(0));
	}
	
	@Override
	public String getHUDText() {
		return super.getHUDText() + " \u00a77" + String.format("%.2f", height);
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		if(mc.player == null) {
			originalStepHeight = -1;
			return;
		}
		originalStepHeight = mc.player.stepHeight;
		mc.player.stepHeight = height;
	}
	
	@EventListener
	public void onTick(EventTick e) {
		if(mc.player == null) {
			originalStepHeight = -1;
			return;
		}
		if(originalStepHeight == -1) {
			originalStepHeight = mc.player.stepHeight;
		}
		mc.player.stepHeight = height;
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		if(mc.player == null) {
			originalStepHeight = -1;
			return;
		}
		mc.player.stepHeight = originalStepHeight;
	}
}
