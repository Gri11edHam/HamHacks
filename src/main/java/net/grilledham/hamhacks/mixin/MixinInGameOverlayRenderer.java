package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.render.Overlays;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameOverlayRenderer.class)
public class MixinInGameOverlayRenderer {
	
	@Inject(method = "renderFireOverlay", at = @At("HEAD"))
	private static void renderFireOverlay(MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
		ModuleManager.getModule(Overlays.class).applyFireTransform(matrices);
	}
	
	@ModifyArg(method = {"renderFireOverlay", "renderInWallOverlay"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;color(FFFF)Lnet/minecraft/client/render/VertexConsumer;"), index = 3)
	private static float renderOverlays(float original) {
		return (float)ModuleManager.getModule(Overlays.class).getOverlayTransparency(original);
	}
	
	@ModifyArg(method = "renderUnderwaterOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/ColorHelper;fromFloats(FFFF)I"), index = 0, remap = false)
	private static float renderWaterOverlay(float original) {
		return (float)ModuleManager.getModule(Overlays.class).getOverlayTransparency(original);
	}
}
