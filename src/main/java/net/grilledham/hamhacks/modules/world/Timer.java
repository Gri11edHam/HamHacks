package net.grilledham.hamhacks.modules.world;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.minecraft.text.Text;

public class Timer extends Module {
	
	private final NumberSetting speed = new NumberSetting("hamhacks.module.timer.speed", 20, () -> true, 1, 100, 0.5, false);
	
	public Timer() {
		super(Text.translatable("hamhacks.module.timer"), Category.WORLD, new Keybind(0));
		GENERAL_CATEGORY.add(speed);
	}
	
	@Override
	public String getHUDText() {
		return super.getHUDText() + " \u00a77" + String.format("%.2f", speed.get());
	}
	
	@EventListener
	public void onTick(EventTick e) {
		if(imc.getRenderTickCounter().getTPS() != speed.get()) {
			imc.getRenderTickCounter().setTPS((float)(double)speed.get());
		}
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		imc.getRenderTickCounter().setTPS(20);
	}
}
