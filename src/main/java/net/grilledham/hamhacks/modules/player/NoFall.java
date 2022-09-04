package net.grilledham.hamhacks.modules.player;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventMotion;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.SelectableList;
import net.grilledham.hamhacks.util.setting.SelectionSetting;
import net.minecraft.block.Material;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

public class NoFall extends Module {
	
	@SelectionSetting(name = "hamhacks.module.noFall.mode")
	public SelectableList mode = new SelectableList("hamhacks.module.noFall.mode.packet", "hamhacks.module.noFall.mode.packet", "hamhacks.module.noFall.mode.momentum");
	
	private float updates = 0;
	private long lastTime;
	
	public NoFall() {
		super(Text.translatable("hamhacks.module.noFall"), Category.PLAYER, new Keybind(GLFW.GLFW_KEY_N));
	}
	
	@EventListener
	public void onMove(EventMotion e) {
		if(e.type == EventMotion.Type.PRE) {
			switch(mode.get()) {
				case "hamhacks.module.noFall.mode.packet" -> {
					if(mc.player.fallDistance >= 3) {
						mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
					}
				}
				case "hamhacks.module.noFall.mode.momentum" -> {
					if(updates >= 0.5f) {
						updates = 0;
					}
					
					boolean isAboveBlock = false;
					for(int xAdd = -1; xAdd < 2; xAdd++) {
						for(int zAdd = -1; zAdd < 2; zAdd++) {
							if(mc.world.getBlockState(new BlockPos(mc.player.getPos().subtract(0.3f * xAdd, 1, 0.3f * zAdd))).getMaterial() != Material.AIR) {
								isAboveBlock = true;
								break;
							}
						}
					}
					if(!isAboveBlock) {
						mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getPos().x, mc.player.getPos().y - updates, mc.player.getPos().z, mc.player.isOnGround()));
					}
					
					updates += (System.currentTimeMillis() - lastTime) / 1000f;
					lastTime = System.currentTimeMillis();
				}
			}
		}
	}
}
