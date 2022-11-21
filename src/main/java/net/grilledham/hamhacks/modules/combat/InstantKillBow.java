package net.grilledham.hamhacks.modules.combat;

import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;

public class InstantKillBow extends Module {
	
	private final NumberSetting iterations = new NumberSetting("hamhacks.module.instantKillBow.iterations", 100, () -> true, 10, 500, 1);
	
	public InstantKillBow() {
		super(Text.translatable("hamhacks.module.instantKillBow"), Category.COMBAT, new Keybind(0));
		GENERAL_CATEGORY.add(iterations);
	}
	
	public void preBow() {
		if(isEnabled()) {
			mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
			for (int i = 0; i < iterations.get(); i++) {
				mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() - 0.000000001, mc.player.getZ(), true));
				mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + 0.000000001, mc.player.getZ(), false));
			}
		}
	}
	
	@Override
	public String getHUDText() {
		return super.getHUDText() + " \u00a77" + (int)(double)iterations.get();
	}
}
