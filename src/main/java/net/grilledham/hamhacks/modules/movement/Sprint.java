package net.grilledham.hamhacks.modules.movement;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventMotion;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class Sprint extends Module {
	
	public Sprint() {
		super(Text.translatable("module.hamhacks.sprint"), Text.translatable("module.hamhacks.sprint.tooltip"), Category.MOVEMENT, new Keybind(GLFW.GLFW_KEY_G));
	}
	
	@EventListener
	public void onMove(EventMotion e) {
		if(e.type == EventMotion.Type.PRE) {
			if(!mc.player.horizontalCollision && mc.player.forwardSpeed > 0) {
				mc.player.setSprinting(true);
			}
		}
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		if(mc.player != null) {
			mc.player.setSprinting(false);
		}
	}
}
