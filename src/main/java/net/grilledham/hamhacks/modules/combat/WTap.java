package net.grilledham.hamhacks.modules.combat;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventPacket;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.text.Text;

public class WTap extends Module {
	
	public WTap() {
		super(Text.translatable("hamhacks.module.wTap"), Category.COMBAT, new Keybind());
	}
	
	@EventListener
	public void sendPacket(EventPacket.EventPacketSent e) {
		if(e.packet instanceof PlayerInteractEntityC2SPacket && e.type == EventPacket.EventPacketSent.Type.PRE) {
			mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
			mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
		}
	}
}
