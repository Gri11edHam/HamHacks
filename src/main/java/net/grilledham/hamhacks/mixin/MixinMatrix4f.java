package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.mixininterface.IMatrix4f;
import net.grilledham.hamhacks.util.math.Vec4;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Matrix4f.class, remap = false)
public class MixinMatrix4f implements IMatrix4f {
	
	@Shadow float m00;
	
	@Shadow float m01;
	
	@Shadow float m02;
	
	@Shadow float m03;
	
	@Shadow float m10;
	
	@Shadow float m11;
	
	@Shadow float m12;
	
	@Shadow float m13;
	
	@Shadow float m20;
	
	@Shadow float m21;
	
	@Shadow float m22;
	
	@Shadow float m23;
	
	@Shadow float m30;
	
	@Shadow float m31;
	
	@Shadow float m32;
	
	@Shadow float m33;
	
	@Override
	public void hamHacks$multiply(Vec4 in, Vec4 out) {
		out.set(
				m00 * in.getX() + m01 * in.getY() + m02 * in.getZ() + m03 * in.getW(),
				m10 * in.getX() + m11 * in.getY() + m12 * in.getZ() + m13 * in.getW(),
				m20 * in.getX() + m21 * in.getY() + m22 * in.getZ() + m23 * in.getW(),
				m30 * in.getX() + m31 * in.getY() + m32 * in.getZ() + m33 * in.getW()
		);
	}
}
