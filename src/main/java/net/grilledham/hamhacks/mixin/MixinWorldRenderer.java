package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.event.events.EventRenderBlockOverlay;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
	
	@Shadow @Final private BufferBuilderStorage bufferBuilders;
	
	@Inject(method = "drawBlockOutline", at = @At("HEAD"), cancellable = true)
	public void drawBlockOutline(MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity, double cameraX, double cameraY, double cameraZ, BlockPos pos, BlockState state, CallbackInfo ci) {
		EventRenderBlockOverlay e = new EventRenderBlockOverlay(bufferBuilders.getEntityVertexConsumers(), matrices, vertexConsumer, entity, cameraX, cameraY, cameraZ, pos, state);
		e.call();
		if(e.canceled) {
			ci.cancel();
		}
	}
}
