package net.grilledham.hamhacks.modules.player;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.minecraft.text.Text;

public class Step extends Module {
	
	private final NumberSetting height = new NumberSetting("hamhacks.module.step.height", 1, () -> true, 0, 10);
	
	private float originalStepHeight;
	
	public Step() {
		super(Text.translatable("hamhacks.module.step"), Category.PLAYER, new Keybind(0));
		GENERAL_CATEGORY.add(height);
	}
	
	@Override
	public String getHUDText() {
		return super.getHUDText() + " \u00a77" + String.format("%.2f", height.get());
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		if(mc.player == null) {
			originalStepHeight = -1;
			return;
		}
		originalStepHeight = mc.player.getStepHeight();
		mc.player.setStepHeight((float)(double)height.get());
	}
	
	@EventListener
	public void onTick(EventTick e) {
		if(mc.player == null) {
			originalStepHeight = -1;
			return;
		}
		if(originalStepHeight == -1) {
			originalStepHeight = mc.player.getStepHeight();
		}
		mc.player.setStepHeight((float)(double)height.get());
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		if(mc.player == null) {
			originalStepHeight = -1;
			return;
		}
		mc.player.setStepHeight(originalStepHeight);
	}
}
