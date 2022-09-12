package net.grilledham.hamhacks.modules.player;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventMotion;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.SelectableList;
import net.grilledham.hamhacks.util.setting.SelectionSetting;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class NoFall extends Module {
	
	@SelectionSetting(name = "hamhacks.module.noFall.mode")
	public SelectableList mode = new SelectableList("hamhacks.module.noFall.mode.packet", "hamhacks.module.noFall.mode.packet", "hamhacks.module.noFall.mode.flySpoof");
	
	public NoFall() {
		super(Text.translatable("hamhacks.module.noFall"), Category.PLAYER, new Keybind(GLFW.GLFW_KEY_N));
	}
	
	@Override
	public String getHUDText() {
		return super.getHUDText() + " \u00a77" + Text.translatable(mode.get()).getString();
	}
	
	@EventListener
	public void onMove(EventMotion e) {
		if(e.type == EventMotion.Type.PRE) {
			switch(mode.get()) {
				case "hamhacks.module.noFall.mode.packet" -> {
					if(mc.player.fallDistance >= 2) {
						mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
					}
				}
				case "hamhacks.module.noFall.mode.flySpoof" -> {
					if(mc.player.fallDistance >= 2) {
						boolean wasFlying = mc.player.getAbilities().flying;
						mc.player.getAbilities().flying = true;
						mc.player.networkHandler.sendPacket(new UpdatePlayerAbilitiesC2SPacket(mc.player.getAbilities()));
						mc.player.getAbilities().flying = wasFlying;
						int yAdd = -2;
						if(mc.player.fallDistance >= 3) {
							yAdd = (int)mc.player.fallDistance;
						}
						mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getPos().x, mc.player.getPos().y + yAdd, mc.player.getPos().z, mc.player.isOnGround()));
						mc.player.fallDistance /= 2;
					}
				}
			}
		}
	}
}
