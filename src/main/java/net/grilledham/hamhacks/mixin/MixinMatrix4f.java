package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.mixininterface.IMatrix4f;
import net.grilledham.hamhacks.util.math.Vec4;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Matrix4f.class)
public class MixinMatrix4f implements IMatrix4f {
	
	@Shadow protected float a00;
	
	@Shadow protected float a01;
	
	@Shadow protected float a02;
	
	@Shadow protected float a03;
	
	@Shadow protected float a10;
	
	@Shadow protected float a11;
	
	@Shadow protected float a12;
	
	@Shadow protected float a13;
	
	@Shadow protected float a20;
	
	@Shadow protected float a21;
	
	@Shadow protected float a22;
	
	@Shadow protected float a23;
	
	@Shadow protected float a30;
	
	@Shadow protected float a31;
	
	@Shadow protected float a32;
	
	@Shadow protected float a33;
	
	@Override
	public void multiply(Vec4 in, Vec4 out) {
		out.set(
				a00 * in.getX() + a01 * in.getY() + a02 * in.getZ() + a03 * in.getW(),
				a10 * in.getX() + a11 * in.getY() + a12 * in.getZ() + a13 * in.getW(),
				a20 * in.getX() + a21 * in.getY() + a22 * in.getZ() + a23 * in.getW(),
				a30 * in.getX() + a31 * in.getY() + a32 * in.getZ() + a33 * in.getW()
		);
	}
}
