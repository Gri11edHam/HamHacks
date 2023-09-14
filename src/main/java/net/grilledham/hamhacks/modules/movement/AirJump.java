package net.grilledham.hamhacks.modules.movement;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.minecraft.text.Text;

public class AirJump extends Module {
	
	public AirJump() {
		super(Text.translatable("hamhacks.module.airJump"), Category.MOVEMENT, new Keybind());
	}
	
	@EventListener
	public void tick(EventTick e) {
		if(mc.player == null) return;
		mc.player.setOnGround(true);
	}
}
