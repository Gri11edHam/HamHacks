package net.grilledham.hamhacks.event.events;

import net.grilledham.hamhacks.event.EventCancelable;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public class EventRenderBlockOverlay extends EventCancelable {
	
	public final VertexConsumerProvider.Immediate immediate;
	
	public final MatrixStack matrices;
	public final VertexConsumer vertexConsumer;
	public final Entity entity;
	public final double cameraX;
	public final double cameraY;
	public final double cameraZ;
	public final BlockPos pos;
	public final BlockState state;
	
	public EventRenderBlockOverlay(VertexConsumerProvider.Immediate immediate, MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity, double cameraX, double cameraY, double cameraZ, BlockPos pos, BlockState state) {
		this.immediate = immediate;
		this.matrices = matrices;
		this.vertexConsumer = vertexConsumer;
		this.entity = entity;
		this.cameraX = cameraX;
		this.cameraY = cameraY;
		this.cameraZ = cameraZ;
		this.pos = pos;
		this.state = state;
	}
}
