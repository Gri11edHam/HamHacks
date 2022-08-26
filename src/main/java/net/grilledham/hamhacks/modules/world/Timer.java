package net.grilledham.hamhacks.modules.world;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.setting.settings.FloatSetting;
import net.minecraft.text.Text;

public class Timer extends Module {
	
	private FloatSetting speed;
	
	public Timer() {
		super(Text.translatable("hamhacks.module.timer"), Category.WORLD, new Keybind(0));
	}
	
	@Override
	public void addSettings() {
		super.addSettings();
		speed = new FloatSetting(Text.translatable("hamhacks.module.timer.speed"), 20, 1, 100);
		addSetting(speed);
	}
	
	@EventListener
	public void onTick(EventTick e) {
		if(imc.getRenderTickCounter().getTPS() != speed.getValue()) {
			imc.getRenderTickCounter().setTPS(speed.getValue());
		}
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		imc.getRenderTickCounter().setTPS(20);
	}
}
