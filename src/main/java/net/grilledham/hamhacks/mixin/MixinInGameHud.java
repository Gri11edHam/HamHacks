package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.render.HUD;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {
	
	@Shadow public abstract TextRenderer getTextRenderer();
	
	@Inject(method = "render", at = @At("TAIL"))
	public void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
		ModuleManager.getModule(HUD.class).render(context, tickCounter.getTickDelta(false), getTextRenderer());
	}
	
	@Inject(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getStatusEffectSpriteManager()Lnet/minecraft/client/texture/StatusEffectSpriteManager;"))
	public void preRenderStatusEffectOverlay(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
		context.getMatrices().push();
		context.getMatrices().translate(0, ModuleManager.getModule(HUD.class).rightHeight, 0);
	}
	
	@Inject(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V", shift = At.Shift.AFTER))
	public void postRenderStatusEffectOverlay(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
		context.getMatrices().pop();
	}
}
