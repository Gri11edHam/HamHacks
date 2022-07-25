package net.grilledham.hamhacks.modules.combat;

import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.setting.settings.IntSetting;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;

public class InstantKillBow extends Module {
	
	private static InstantKillBow INSTANCE;
	
	private IntSetting iterations;
	
	public InstantKillBow() {
		super(Text.translatable("hamhacks.module.instantKillBow"), Category.COMBAT, new Keybind(0));
		INSTANCE = this;
	}
	
	@Override
	public void addSettings() {
		super.addSettings();
		iterations = new IntSetting(Text.translatable("hamhacks.module.instantKillBow.iterations"), 100, 10, 500);
		addSetting(iterations);
	}
	
	public static InstantKillBow getInstance() {
		return INSTANCE;
	}
	
	public void preBow() {
		if(InstantKillBow.getInstance().isEnabled()) {
			mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
			for (int i = 0; i < iterations.getValue(); i++) {
				mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() - 0.000000001, mc.player.getZ(), true));
				mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + 0.000000001, mc.player.getZ(), false));
			}
		}
	}
}
