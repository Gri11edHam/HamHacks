package net.grilledham.hamhacks.modules.movement;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventPacket;
import net.grilledham.hamhacks.mixininterface.IEntityVelocityUpdateS2CPacket;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.text.Text;

public class Velocity extends Module {
	
	private final NumberSetting horizontal = new NumberSetting("hamhacks.module.velocity.horizontal", 1, () -> true, -1, 4);
	private final NumberSetting vertical = new NumberSetting("hamhacks.module.velocity.vertical", 1, () -> true, -1, 4);
	
	public Velocity() {
		super(Text.translatable("hamhacks.module.velocity"), Category.MOVEMENT, new Keybind());
		GENERAL_CATEGORY.add(horizontal);
		GENERAL_CATEGORY.add(vertical);
	}
	
	@Override
	public String getHUDText() {
		return super.getHUDText() + " \u00a77" + String.format("%.2f|%.2f", horizontal.get(), vertical.get());
	}
	
	@EventListener
	public void onPacket(EventPacket.EventPacketReceived e) {
		if(e.packet instanceof EntityVelocityUpdateS2CPacket packet) {
			if(mc.player == null) return;
			if(packet.getEntityId() == mc.player.getId()) {
				double vx = packet.getVelocityX();
				double vy = packet.getVelocityY();
				double vz = packet.getVelocityZ();
				vx *= horizontal.get();
				vy *= vertical.get();
				vz *= horizontal.get();
				((IEntityVelocityUpdateS2CPacket)packet).set((int)vx, (int)vy, (int)vz);
			}
		}
	}
}
