package net.grilledham.hamhacks.modules.movement;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventMotion;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class LongJump extends Module {
	
	private final NumberSetting speed = new NumberSetting("hamhacks.module.longJump.speed", 1, () -> true, 0, 20, 1, false);
	
	public LongJump() {
		super(Text.translatable("hamhacks.module.longJump"), Category.MOVEMENT, new Keybind());
		
		GENERAL_CATEGORY.add(speed);
	}
	
	@EventListener
	public void onJump(EventMotion e) {
		if(mc.player != null && e.type == EventMotion.Type.PRE) {
			if(mc.options.jumpKey.isPressed() && mc.player.isOnGround()) {
				mc.player.addVelocity(Vec3d.fromPolar(0, mc.player.getYaw()).multiply(speed.get() / 2));
			}
		}
	}
}
