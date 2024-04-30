package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.mixininterface.IVec3d;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Vec3d.class)
public abstract class MixinVec3d implements Position, IVec3d {
	
	@Mutable
	@Shadow @Final public double x;
	
	@Mutable
	@Shadow @Final public double y;
	
	@Mutable
	@Shadow @Final public double z;
	
	@Override
	public void hamHacks$setX(double x) {
		this.x = x;
	}
	
	@Override
	public void hamHacks$setY(double y) {
		this.y = y;
	}
	
	@Override
	public void hamHacks$setZ(double z) {
		this.z = z;
	}
	
	@Override
	public void hamHacks$set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public void hamHacks$set(Vec3d other) {
		hamHacks$set(other.x, other.y, other.z);
	}
}
