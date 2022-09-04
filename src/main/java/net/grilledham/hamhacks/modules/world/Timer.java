package net.grilledham.hamhacks.modules.world;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.setting.NumberSetting;
import net.minecraft.text.Text;

public class Timer extends Module {
	
	@NumberSetting(
			name = "hamhacks.module.timer.speed",
			defaultValue = 20,
			min = 1,
			max = 100,
			step = 0.5f,
			forceStep = false
	)
	public float speed = 20;
	
	public Timer() {
		super(Text.translatable("hamhacks.module.timer"), Category.WORLD, new Keybind(0));
	}
	
	@EventListener
	public void onTick(EventTick e) {
		if(imc.getRenderTickCounter().getTPS() != speed) {
			imc.getRenderTickCounter().setTPS(speed);
		}
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		imc.getRenderTickCounter().setTPS(20);
	}
}
