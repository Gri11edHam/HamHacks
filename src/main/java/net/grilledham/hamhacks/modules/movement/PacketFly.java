package net.grilledham.hamhacks.modules.movement;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.grilledham.hamhacks.util.math.Vec3;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;

public class PacketFly extends Module {
	
	private final NumberSetting timerMultiplier = new NumberSetting("hamhacks.module.packetFly.timerMultiplier", 1, () -> true, 1, 5);
	
	public PacketFly() {
		super(Text.translatable("hamhacks.module.packetFly"), Category.MOVEMENT, new Keybind(0));
		GENERAL_CATEGORY.add(timerMultiplier);
	}
	
	@Override
	public String getHUDText() {
		return super.getHUDText() + "\u00a77" + String.format("%.2f", timerMultiplier.get());
	}
	
	@EventListener
	public void onTick(EventTick e) {
		if(mc.player == null) {
			return;
		}
		
		if(timerMultiplier.get() > 1) {
			imc.hamHacks$getRenderTickCounter().hamHacks$setTPS((float)(20 * timerMultiplier.get()));
		}
		
		mc.player.setVelocity(0, 0, 0);
		
		double step = 0.03D;
		
		mc.player.noClip = true;
		float distanceForward = 0;
		float distanceStrafe = 0;
		float distanceVertical = 0;
		if(mc.player.input.playerInput.forward()) {
			distanceForward += 1;
		}
		if(mc.player.input.playerInput.backward()) {
			distanceForward -= 1;
		}
		if(mc.player.input.playerInput.left()) {
			distanceStrafe -= 1;
		}
		if(mc.player.input.playerInput.right()) {
			distanceStrafe += 1;
		}
		if(mc.player.input.playerInput.jump()) {
			distanceVertical += 1;
		}
		if(mc.player.input.playerInput.sneak()) {
			distanceVertical -= 1;
		}
		float dx = (float)(distanceForward * Math.cos(Math.toRadians(mc.player.getYaw() + 90)));
		float dy = distanceVertical;
		float dz = (float)(distanceForward * Math.sin(Math.toRadians(mc.player.getYaw() + 90)));
		dx += (float)(distanceStrafe * Math.cos(Math.toRadians(mc.player.getYaw())));
		dz += (float)(distanceStrafe * Math.sin(Math.toRadians(mc.player.getYaw())));
		dx /= 2;
		dz /= 2;
		dx *= step;
		dy *= step;
		dz *= step;
		
		if(mc.player.age % 8 == 0) {
			dy = -0.05F;
		}
		
		Vec3 pos = new Vec3(mc.player.getPos());
		pos.add(dx, dy, dz);
		
		mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.getX(), pos.getY(), pos.getZ(), mc.player.isOnGround(), mc.player.horizontalCollision));
		mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.getX(), pos.getY() - 1337, pos.getZ(), mc.player.isOnGround(), mc.player.horizontalCollision));
		mc.player.setPosition(pos.get());
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		if(mc.player == null) {
			return;
		}
		mc.player.noClip = false;
		imc.hamHacks$getRenderTickCounter().hamHacks$setTPS(20);
	}
}
