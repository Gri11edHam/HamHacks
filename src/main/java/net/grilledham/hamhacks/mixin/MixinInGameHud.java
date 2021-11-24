package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.gui.IngameGui;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.minecraft.client.MinecraftClient;
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
}
