package net.grilledham.hamhacks.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.grilledham.hamhacks.event.events.EventRender2D;
import net.grilledham.hamhacks.event.events.EventRender3D;
import net.grilledham.hamhacks.mixininterface.IGameRenderer;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.combat.Reach;
import net.grilledham.hamhacks.modules.render.HUD;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.Camera;
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
public abstract class MixinGameRenderer implements SynchronousResourceReloader, AutoCloseable, IGameRenderer {
	
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "net.minecraft.client.gui.hud.InGameHud.render(Lnet/minecraft/client/util/math/MatrixStack;F)V"))
	public void render2DEvent(InGameHud instance, MatrixStack matrices, float tickDelta) {
		ModuleManager.updateEnabled();
		instance.render(matrices, tickDelta);
		EventRender2D event = new EventRender2D(matrices, tickDelta);
		event.call();
	}
	
	@Inject(method = "renderWorld", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=hand"))
	public void render3DEvent(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci) {
		EventRender3D event = new EventRender3D(tickDelta, matrices);
		event.call();
		
		RenderSystem.applyModelViewMatrix();
	}
	
	@Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;bobView(Lnet/minecraft/client/util/math/MatrixStack;F)V"))
	public void modelBobbingOnly(GameRenderer instance, MatrixStack matrices, float tickDelta) {
		if(!HUD.getInstance().modelBobbingOnly || !HUD.getInstance().isEnabled()) {
			bobView(matrices, tickDelta);
		}
	}
	
	@Inject(method = "bobViewWhenHurt", at = @At("HEAD"), cancellable = true)
	public void noHurtCam(MatrixStack matrices, float f, CallbackInfo ci) {
		if(HUD.getInstance().noHurtCam && HUD.getInstance().isEnabled()) {
			ci.cancel();
		}
	}
	
	@ModifyVariable(method = "updateTargetedEntity", at = @At(value = "STORE"), index = 3)
	public double modifyBlockReach(double d) {
		if(Reach.getInstance() == null) {
			return d;
		}
		return Reach.getInstance().isEnabled() ? Reach.getInstance().blockRange : d;
	}
	
	@ModifyVariable(method = "updateTargetedEntity", at = @At(value = "STORE"), index = 8)
	public double modifyEntityReach(double e) {
		if(Reach.getInstance() == null) {
			return e;
		}
		return Reach.getInstance().isEnabled() ? Reach.getInstance().entityRange * Reach.getInstance().entityRange : e;
	}
	
	@ModifyVariable(method = "updateTargetedEntity", at = @At(value = "STORE"), index = 6)
	public boolean setAlwaysExtendedReach(boolean bl) {
		if(Reach.getInstance() == null) {
			return bl;
		}
		return !Reach.getInstance().isEnabled() && bl;
	}
	
	@Override
	public double getFOV(Camera camera, float tickDelta, boolean changingFov) {
		return getFov(camera, tickDelta, changingFov);
	}
	
	@Shadow protected abstract double getFov(Camera camera, float tickDelta, boolean changingFov);
	
	@Shadow
	public abstract void close();
	
	@Shadow
	public abstract void reload(ResourceManager manager);
	
	@Shadow protected abstract void bobView(MatrixStack matrices, float tickDelta);
}
