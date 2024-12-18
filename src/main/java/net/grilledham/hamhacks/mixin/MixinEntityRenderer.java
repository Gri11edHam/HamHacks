package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.render.Nametags;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer<T extends Entity, S extends EntityRenderState> {
	
	@Unique
	private T entity;
	
	@Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
	public void renderNameTag(S state, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		if(ModuleManager.getModule(Nametags.class).shouldRender(entity)) {
			ci.cancel();
		}
	}
	
	@Inject(method = "updateRenderState", at = @At("HEAD"))
	public void getEntity(T entity, S state, float tickDelta, CallbackInfo ci) {
		this.entity = entity;
	}
}
