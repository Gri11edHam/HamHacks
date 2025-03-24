package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.mixininterface.IEntityVelocityUpdateS2CPacket;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityVelocityUpdateS2CPacket.class)
public abstract class MixinEntityVelocityUpdateS2CPacket implements Packet<ClientPlayPacketListener>, IEntityVelocityUpdateS2CPacket {
	
	@Mutable
	@Shadow @Final private int velocityX;
	
	@Mutable
	@Shadow @Final private int velocityY;
	
	@Mutable
	@Shadow @Final private int velocityZ;
	
	@Override
	public void hamHacks$setX(int vx) {
		this.velocityX = vx;
	}
	
	@Override
	public void hamHacks$setY(int vy) {
		this.velocityY = vy;
	}
	
	@Override
	public void hamHacks$setZ(int vz) {
		this.velocityZ = vz;
	}
	
	@Override
	public int hamHacks$getX() {
		return this.velocityX;
	}
	
	@Override
	public int hamHacks$getY() {
		return this.velocityY;
	}
	
	@Override
	public int hamHacks$getZ() {
		return this.velocityZ;
	}
}
