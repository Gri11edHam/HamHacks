package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.gui.overlays.IngameGui;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud extends DrawableHelper {
	
	@Shadow public abstract TextRenderer getTextRenderer();
	
	@Inject(method = "render", at = @At("TAIL"))
	public void render(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
		IngameGui.getInstance().render(matrices, tickDelta, getTextRenderer());
	}
	
	@Inject(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V", shift = At.Shift.BEFORE, remap = false))
	public void preRenderStatusEffectOverlay(MatrixStack matrices, CallbackInfo ci) {
		matrices.push();
		matrices.translate(0, IngameGui.getInstance().rightHeight, 0);
	}
	
	@Inject(method = "renderStatusEffectOverlay", at = @At("TAIL"))
	public void postRenderStatusEffectOverlay(MatrixStack matrices, CallbackInfo ci) {
		matrices.pop();
	}
}
