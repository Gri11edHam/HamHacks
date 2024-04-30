package net.grilledham.hamhacks.mixininterface;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public interface IClientPlayerInteractionManager {
	
	boolean hamHacks$rightClickBlock(BlockPos pos, Direction side, Vec3d hitVec);
	
	void hamHacks$leftClickEntity(Entity entity);
}
