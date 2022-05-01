package net.grilledham.hamhacks.modules.movement;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.minecraft.fluid.Fluids;
import org.lwjgl.glfw.GLFW;

public class Jesus extends Module {
	
	public Jesus() {
		super("Jesus", Category.MOVEMENT, new Keybind(GLFW.GLFW_KEY_J));
	}
	
	@EventListener
	public void onTick(EventTick e) {
		if(mc.world == null) {
			return;
		}
		if(mc.world.getBlockState(mc.player.getBlockPos().add(0, 0, 0)).getFluidState().getFluid() != Fluids.EMPTY) {
			mc.player.setVelocity(mc.player.getVelocity().x, mc.options.jumpKey.isPressed() ? 0.2 : mc.options.sneakKey.isPressed() ? -0.2 : mc.player.isTouchingWater() ? 0.05 : 0, mc.player.getVelocity().z);
			mc.player.setOnGround(true);
		}
	}
}
