package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.event.events.EventRender2D;
import net.grilledham.hamhacks.event.events.EventRender3D;
import net.grilledham.hamhacks.modules.combat.Reach;
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
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer implements SynchronousResourceReloader, AutoCloseable {
	
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "net.minecraft.client.gui.hud.InGameHud.render(Lnet/minecraft/client/util/math/MatrixStack;F)V"))
	public void render2DEvent(InGameHud instance, MatrixStack matrices, float tickDelta) {
		instance.render(matrices, tickDelta);
		EventRender2D event = new EventRender2D(matrices, tickDelta);
		event.call();
	}
	
	@Inject(method = "renderWorld", at = @At("HEAD"))
	public void render3DEvent(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci) {
		EventRender3D event = new EventRender3D(tickDelta, matrices);
		event.call();
	}
	
	@Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;bobView(Lnet/minecraft/client/util/math/MatrixStack;F)V"))
	public void modelBobbingOnly(GameRenderer instance, MatrixStack matrices, float tickDelta) {
		if(!HUD.getInstance().modelBobbingOnly.getValue() && HUD.getInstance().isEnabled()) {
			bobView(matrices, tickDelta);
		}
	}
	
	@Inject(method = "bobViewWhenHurt", at = @At("HEAD"), cancellable = true)
	public void noHurtCam(MatrixStack matrices, float f, CallbackInfo ci) {
		if(!(!HUD.getInstance().noHurtCam.getValue() && HUD.getInstance().isEnabled())) {
			ci.cancel();
		}
	}
	
	@ModifyVariable(method = "updateTargetedEntity", at = @At(value = "STORE"), index = 3)
	public double modifyBlockReach(double d) {
		if(Reach.getInstance() == null) {
			return d;
		}
		return Reach.getInstance().isEnabled() ? Reach.getInstance().blockRange.getValue() : d;
	}
	
	@ModifyVariable(method = "updateTargetedEntity", at = @At(value = "STORE"), index = 8)
	public double modifyEntityReach(double e) {
		if(Reach.getInstance() == null) {
			return e;
		}
		return Reach.getInstance().isEnabled() ? Reach.getInstance().entityRange.getValue() * Reach.getInstance().entityRange.getValue() : e;
	}
	
	@ModifyVariable(method = "updateTargetedEntity", at = @At(value = "STORE"), index = 6)
	public boolean setAlwaysExtendedReach(boolean bl) {
		if(Reach.getInstance() == null) {
			return bl;
		}
		return !Reach.getInstance().isEnabled() && bl;
	}
	
	@Shadow
	public abstract void close();
	
	@Shadow
	public abstract void reload(ResourceManager manager);
	
	@Shadow protected abstract void bobView(MatrixStack matrices, float tickDelta);
}
