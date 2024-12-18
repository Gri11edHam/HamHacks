package net.grilledham.hamhacks.modules.movement;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class LongJump extends Module {
	
	private final NumberSetting speed = new NumberSetting("hamhacks.module.longJump.speed", 1, () -> true, 0, 20, 1, false);
	
	private boolean wasJumping = false;
	
	public LongJump() {
		super(Text.translatable("hamhacks.module.longJump"), Category.MOVEMENT, new Keybind());
		
		GENERAL_CATEGORY.add(speed);
	}
	
	@Override
	public String getHUDText() {
		return super.getHUDText() + "\u00a77" + String.format("%.2f", speed.get());
	}
	
	@EventListener
	public void onTick(EventTick e) {
		if(mc.player != null) {
			if(mc.player.input.playerInput.jump()) {
				if(!wasJumping) {
					mc.player.addVelocity(Vec3d.fromPolar(0, mc.player.getYaw()).multiply(speed.get() / 4));
				}
			}
			wasJumping = !mc.player.isOnGround();
		}
	}
}
