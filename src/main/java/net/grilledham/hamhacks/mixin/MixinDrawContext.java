package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.mixininterface.IDrawContext;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DrawContext.class)
public class MixinDrawContext implements IDrawContext {
	
	@Shadow @Final private VertexConsumerProvider.Immediate vertexConsumers;
	
	@Shadow @Final private ItemRenderState itemRenderState;
	
	@Override
	public VertexConsumerProvider.Immediate hamHacks$getVertexConsumers() {
		return vertexConsumers;
	}
	
	@Override
	public ItemRenderState hamHacks$getItemRenderState() {
		return itemRenderState;
	}
}
