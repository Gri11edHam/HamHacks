package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.event.events.EventRender2D;
import net.grilledham.hamhacks.event.events.EventRender3D;
import net.grilledham.hamhacks.modules.render.HUD;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer implements SynchronousResourceReloader, AutoCloseable {
	
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "net.minecraft.client.gui.hud.InGameHud.render(Lnet/minecraft/client/util/math/MatrixStack;F)V"))
	public void render2DEvent(InGameHud instance, MatrixStack matrices, float tickDelta) {
		instance.render(matrices, tickDelta);
		EventRender2D event = new EventRender2D(matrices, tickDelta);
		event.call();
	}
	
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "net.minecraft.client.render.GameRenderer.renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V"))
	public void render3DEvent(GameRenderer instance, float tickDelta, long limitTime, MatrixStack matrices) {
		instance.renderWorld(tickDelta, limitTime, matrices);
		EventRender3D event = new EventRender3D(tickDelta, limitTime, matrices);
		event.call();
	}
	
	@Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;bobView(Lnet/minecraft/client/util/math/MatrixStack;F)V"))
	public void modelBobbingOnly(GameRenderer instance, MatrixStack matrices, float f) {
		if(!HUD.getInstance().modelBobbingOnly.getValue() && HUD.getInstance().isEnabled()) {
			bobView(matrices, f);
		}
	}
	
	@Inject(method = "bobViewWhenHurt", at = @At("HEAD"), cancellable = true)
	public void noHurtCam(MatrixStack matrices, float f, CallbackInfo ci) {
		if(!(!HUD.getInstance().noHurtCam.getValue() && HUD.getInstance().isEnabled())) {
			ci.cancel();
		}
	}
	
	@Shadow
	public void close() {}
	
	@Shadow
	public void reload(ResourceManager manager) {}
	
	@Shadow
	private void bobView(MatrixStack matrices, float f) {}
}