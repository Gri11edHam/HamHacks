package net.grilledham.hamhacks.mixininterface;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderState;

public interface IDrawContext {
	
	VertexConsumerProvider.Immediate hamHacks$getVertexConsumers();
	
	ItemRenderState hamHacks$getItemRenderState();
}
