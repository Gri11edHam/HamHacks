package net.grilledham.hamhacks.mixininterface;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public interface IClientPlayerInteractionManager {
	
	boolean rightClickBlock(BlockPos pos, Direction side, Vec3d hitVec);
	
	void leftClickEntity(Entity entity);
}
