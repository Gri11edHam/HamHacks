package net.grilledham.hamhacks.mixininterface;

import net.minecraft.client.render.VertexConsumerProvider;

public interface IDrawContext {
	
	VertexConsumerProvider hamHacks$getVertexConsumers();
}
