package net.grilledham.hamhacks.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.grilledham.hamhacks.event.events.EventRender;
import net.grilledham.hamhacks.event.events.EventRender2D;
import net.grilledham.hamhacks.event.events.EventRender3D;
import net.grilledham.hamhacks.mixininterface.ICamera;
import net.grilledham.hamhacks.mixininterface.IGameRenderer;
import net.grilledham.hamhacks.mixininterface.IVec3d;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.combat.Reach;
import net.grilledham.hamhacks.modules.render.Freecam;
import net.grilledham.hamhacks.modules.render.HUD;
import net.grilledham.hamhacks.modules.render.Zoom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer implements SynchronousResourceReloader, AutoCloseable, IGameRenderer {
	
	@Shadow public abstract void updateTargetedEntity(float tickDelta);
	
	@Shadow @Final private MinecraftClient client;
	
	private boolean wasFreecamEnabled = false;
	
	private boolean calledFromFreecam = false;
	
	@Inject(method = "render", at = @At("TAIL"))
	public void renderEvent(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
		ModuleManager.updateEnabled();
		EventRender event = new EventRender(new MatrixStack(), tickDelta);
		event.call();
	}
	
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "net.minecraft.client.gui.hud.InGameHud.render(Lnet/minecraft/client/util/math/MatrixStack;F)V"))
	public void render2DEvent(InGameHud instance, MatrixStack matrices, float tickDelta) {
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
	
	@Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
	public void modifyFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
		if(changingFov || !Zoom.getInstance().renderHand) {
			double d = cir.getReturnValueD();
			cir.setReturnValue(Zoom.getInstance().modifyFov(d));
		}
	}
	
	@Redirect(method = "bobView", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F"))
	public float mitigateBob(float delta, float start, float end) {
		if(Zoom.getInstance().isEnabled()) {
			double divisor = Math.sqrt(Zoom.getInstance().getZoomAmount());
			start = (float)(start / divisor);
			end = (float)(end / divisor);
		}
		return MathHelper.lerp(delta, start, end);
	}
	
	@Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V"))
	public void overwriteCamera(Camera instance, BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta) {
		if(Freecam.getInstance().isEnabled()) {
			if(!wasFreecamEnabled) {
				instance.update(area, focusedEntity, true, inverseView, tickDelta);
			}
			((ICamera)instance).setCamPos(Freecam.getInstance().getPos(tickDelta).add(0, focusedEntity.getEyeHeight(focusedEntity.getPose()), 0));
			((ICamera)instance).setCamRot(Freecam.getInstance().getYaw(tickDelta), Freecam.getInstance().getPitch(tickDelta));
		} else {
			instance.update(area, focusedEntity, thirdPerson, inverseView, tickDelta);
		}
		wasFreecamEnabled = Freecam.getInstance().isEnabled();
	}
	
	@Inject(method = "updateTargetedEntity", at = @At("HEAD"), cancellable = true)
	public void updateTargetedEntity(float tickDelta, CallbackInfo ci) {
		if(client.getCameraEntity() == null) {
			return;
		}
		if(Freecam.getInstance().isEnabled() && !calledFromFreecam) {
			ci.cancel();
			
			Entity entity = client.getCameraEntity();
			
			Vec3d pos = entity.getPos().multiply(1);
			double prevX = entity.prevX;
			double prevY = entity.prevY;
			double prevZ = entity.prevZ;
			float yaw = entity.getYaw();
			float pitch = entity.getPitch();
			float prevYaw = entity.prevYaw;
			float prevPitch = entity.prevPitch;
			
			((IVec3d)entity.getPos()).set(Freecam.getInstance().pos);
			entity.prevX = Freecam.getInstance().prevPos.x;
			entity.prevY = Freecam.getInstance().prevPos.y;
			entity.prevZ = Freecam.getInstance().prevPos.z;
			entity.setYaw(Freecam.getInstance().yaw);
			entity.setPitch(Freecam.getInstance().pitch);
			entity.prevYaw = Freecam.getInstance().prevYaw;
			entity.prevPitch = Freecam.getInstance().prevPitch;
			
			calledFromFreecam = true;
			updateTargetedEntity(tickDelta);
			calledFromFreecam = false;
			
			((IVec3d)entity.getPos()).set(pos);
			entity.prevX = prevX;
			entity.prevY = prevY;
			entity.prevZ = prevZ;
			entity.setYaw(yaw);
			entity.setPitch(pitch);
			entity.prevYaw = prevYaw;
			entity.prevPitch = prevPitch;
		}
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
