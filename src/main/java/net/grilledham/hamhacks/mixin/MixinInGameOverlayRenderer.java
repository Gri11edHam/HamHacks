package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.modules.render.HUD;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameOverlayRenderer.class)
public class MixinInGameOverlayRenderer {
	
	@Inject(method = "renderFireOverlay", at = @At("HEAD"))
	private static void renderFireOverlay(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
		HUD.getInstance().applyFireTransform(matrices);
	}
	
	@ModifyArg(method = {"renderFireOverlay", "renderInWallOverlay"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;color(FFFF)Lnet/minecraft/client/render/VertexConsumer;"), index = 3)
	private static float renderOverlays(float original) {
		return HUD.getInstance().getOverlayTransparency(original);
	}
	
	@ModifyArg(method = "renderUnderwaterOverlay", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V"), index = 3)
	private static float renderWaterOverlay(float original) {
		return HUD.getInstance().getOverlayTransparency(original);
	}
}
