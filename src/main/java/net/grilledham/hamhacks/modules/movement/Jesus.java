package net.grilledham.hamhacks.modules.movement;

import net.grilledham.hamhacks.event.Event;
import net.grilledham.hamhacks.event.EventTick;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.minecraft.fluid.Fluids;
import org.lwjgl.glfw.GLFW;

public class Jesus extends Module {
	
	public Jesus() {
		super("Jesus", Category.MOVEMENT, new Keybind(GLFW.GLFW_KEY_J));
	}
	
	@Override
	public boolean onEvent(Event e) {
		boolean superReturn = super.onEvent(e);
		if(superReturn) {
			if(e instanceof EventTick) {
				if(mc.world.getBlockState(mc.player.getBlockPos().add(0, 0, 0)).getFluidState().getFluid() != Fluids.EMPTY) {
					mc.player.setVelocity(mc.player.getVelocity().x, mc.options.keyJump.isPressed() ? 0.2 : mc.options.keySneak.isPressed() ? -0.2 : mc.player.isTouchingWater() ? 0.05 : 0, mc.player.getVelocity().z);
					mc.player.setOnGround(true);
				}
			}
		}
		return superReturn;
	}
}
