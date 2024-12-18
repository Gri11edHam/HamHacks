package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.mixininterface.IDrawContext;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DrawContext.class)
public class MixinDrawContext implements IDrawContext {
	
	@Shadow @Final private VertexConsumerProvider.Immediate vertexConsumers;
	
	@Override
	public VertexConsumerProvider hamHacks$getVertexConsumers() {
		return vertexConsumers;
	}
}
