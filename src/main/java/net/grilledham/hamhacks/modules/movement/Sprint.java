package net.grilledham.hamhacks.modules.movement;

import net.grilledham.hamhacks.event.Event;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import org.lwjgl.glfw.GLFW;

public class Sprint extends Module {
	
	public Sprint() {
		super("Sprint", Category.MOVEMENT, new Keybind(GLFW.GLFW_KEY_G));
	}
	
	@Override
	public boolean onEvent(Event e) {
		boolean superReturn = super.onEvent(e);
		if(superReturn) {
			if(!mc.player.horizontalCollision && mc.player.forwardSpeed > 0) {
				mc.player.setSprinting(true);
			}
		}
		return superReturn;
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		mc.player.setSprinting(false);
	}
}
