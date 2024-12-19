package net.grilledham.hamhacks.modules.player;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventMotion;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.SelectionSetting;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class NoFall extends Module {
	
	private final SelectionSetting mode = new SelectionSetting("hamhacks.module.noFall.mode", 0, () -> true, "hamhacks.module.noFall.mode.packet", "hamhacks.module.noFall.mode.flySpoof");
	
	private double fallStart;
	private float fallDistance;
	
	public NoFall() {
		super(Text.translatable("hamhacks.module.noFall"), Category.PLAYER, new Keybind(GLFW.GLFW_KEY_N));
		GENERAL_CATEGORY.add(mode);
	}
	
	@Override
	public String getHUDText() {
		return super.getHUDText() + " \u00a77" + mode.options()[mode.get()];
	}
	
	@EventListener
	public void onMove(EventMotion e) {
		if(e.type == EventMotion.Type.PRE) {
			if(mc.player.getVelocity().getY() >= 0) {
				fallStart = mc.player.getPos().getY();
			} else {
				fallDistance = (float)(fallStart - mc.player.getPos().getY());
			}
			switch(mode.get()) {
				case 0 -> {
					if(fallDistance >= 2) {
						mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true, mc.player.horizontalCollision));
					}
				}
				case 1 -> {
					if(fallDistance >= 2) {
						boolean wasFlying = mc.player.getAbilities().flying;
						mc.player.getAbilities().flying = true;
						mc.player.networkHandler.sendPacket(new UpdatePlayerAbilitiesC2SPacket(mc.player.getAbilities()));
						mc.player.getAbilities().flying = wasFlying;
						int yAdd = -2;
						if(mc.player.fallDistance >= 3) {
							yAdd = (int)mc.player.fallDistance;
						}
						mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getPos().x, mc.player.getPos().y + yAdd, mc.player.getPos().z, mc.player.isOnGround(), mc.player.horizontalCollision));
						mc.player.fallDistance /= 2;
					}
				}
			}
		}
	}
}
