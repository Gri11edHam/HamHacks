package net.grilledham.hamhacks.mixininterface;

import net.grilledham.hamhacks.util.math.Vec4;

public interface IMatrix4f {
	
	void multiply(Vec4 vec4, Vec4 modelVec);
}
