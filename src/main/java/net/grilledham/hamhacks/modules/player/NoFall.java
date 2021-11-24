package net.grilledham.hamhacks.modules.player;

import net.grilledham.hamhacks.event.Event;
import net.grilledham.hamhacks.event.EventMotion;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.lwjgl.glfw.GLFW;

public class NoFall extends Module {
	
	public NoFall() {
		super("No Fall", Category.PLAYER, new Keybind(GLFW.GLFW_KEY_N));
	}
	
	@Override
	public boolean onEvent(Event e) {
		boolean superReturn = super.onEvent(e);
		if(superReturn) {
			if(e instanceof EventMotion) {
				if(mc.player.fallDistance >= 3) {
					mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
				}
			}
		}
		return superReturn;
	}
}
