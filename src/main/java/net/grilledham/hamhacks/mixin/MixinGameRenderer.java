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
import net.grilledham.hamhacks.modules.render.Bob;
import net.grilledham.hamhacks.modules.render.Freecam;
import net.grilledham.hamhacks.modules.render.HandRender;
import net.grilledham.hamhacks.modules.render.Zoom;
import net.grilledham.hamhacks.util.ProjectionUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer implements SynchronousResourceReloader, AutoCloseable, IGameRenderer {
	
	@Shadow public abstract void updateTargetedEntity(float tickDelta);
	
	@Shadow @Final MinecraftClient client;
	
	@Unique
	private boolean wasFreecamEnabled = false;
	
	@Unique
	private boolean calledFromFreecam = false;
	
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V"), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void renderEvent(float tickDelta, long startTime, boolean tick, CallbackInfo ci, float f, boolean bl, int i, int j, Window window, Matrix4f matrix4f, MatrixStack matrixStack, DrawContext drawContext) {
		EventRender event = new EventRender(drawContext, tickDelta);
		event.call();
	}
	
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;render(Lnet/minecraft/client/gui/DrawContext;F)V"))
	public void render2DEvent(InGameHud instance, DrawContext context, float tickDelta) {
		EventRender2D event = new EventRender2D(context, tickDelta);
		event.call();
		instance.render(context, tickDelta);
	}
	
	@Inject(method = "renderWorld", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=hand"), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void render3DEvent(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci, boolean bl, Camera camera, Entity entity, MatrixStack matrixStack, double d, float f, float g, Matrix4f matrix4f, Matrix3f matrix3f) {
		ProjectionUtil.updateMatrices(matrices, matrix4f);
		
		EventRender3D event = new EventRender3D(tickDelta, matrices);
		event.call();
		
		RenderSystem.applyModelViewMatrix();
	}
	
	@Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;bobView(Lnet/minecraft/client/util/math/MatrixStack;F)V"))
	public void modelBobbingOnly(GameRenderer instance, MatrixStack matrices, float tickDelta) {
		if(!ModuleManager.getModule(Bob.class).modelBobbingOnly.get() || !ModuleManager.getModule(Bob.class).isEnabled()) {
			bobView(matrices, tickDelta);
		}
	}
	
	@Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true)
	public void noHurtCam(MatrixStack matrices, float f, CallbackInfo ci) {
		if(ModuleManager.getModule(Bob.class).noHurtCam.get() && ModuleManager.getModule(Bob.class).isEnabled()) {
			ci.cancel();
		}
	}
	
	@ModifyVariable(method = "updateTargetedEntity", at = @At(value = "STORE"), index = 3)
	public double modifyBlockReach(double d) {
		return ModuleManager.getModule(Reach.class).isEnabled() ? ModuleManager.getModule(Reach.class).blockRange.get() : d;
	}
	
	@ModifyVariable(method = "updateTargetedEntity", at = @At(value = "STORE"), index = 8)
	public double modifyEntityReach(double e) {
		return ModuleManager.getModule(Reach.class).isEnabled() ? ModuleManager.getModule(Reach.class).entityRange.get() * ModuleManager.getModule(Reach.class).entityRange.get() : e;
	}
	
	@ModifyVariable(method = "updateTargetedEntity", at = @At(value = "STORE"), index = 7)
	public boolean setAlwaysExtendedReach(boolean bl) {
		if(ModuleManager.getModule(Reach.class) == null) {
			return bl;
		}
		return !ModuleManager.getModule(Reach.class).isEnabled() && bl;
	}
	
	@Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
	public void modifyFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
		double d = cir.getReturnValueD();
		HandRender handRender = ModuleManager.getModule(HandRender.class);
		if(!changingFov && handRender.isEnabled()) {
			d *= handRender.fovMultiplier.get();
		}
		if(changingFov || !ModuleManager.getModule(Zoom.class).renderHand.get()) {
			d = ModuleManager.getModule(Zoom.class).modifyFov(d);
		}
		cir.setReturnValue(d);
	}
	
	@Redirect(method = "bobView", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F"))
	public float mitigateBob(float delta, float start, float end) {
		if(ModuleManager.getModule(Zoom.class).isEnabled()) {
			double divisor = Math.sqrt(ModuleManager.getModule(Zoom.class).getZoomAmount());
			start = (float)(start / divisor);
			end = (float)(end / divisor);
		}
		return MathHelper.lerp(delta, start, end);
	}
	
	@Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V"))
	public void overwriteCamera(Camera instance, BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta) {
		if(ModuleManager.getModule(Freecam.class).isEnabled()) {
			if(!wasFreecamEnabled) {
				instance.update(area, focusedEntity, true, inverseView, tickDelta);
			}
			((ICamera)instance).setCamPos(ModuleManager.getModule(Freecam.class).getPos(tickDelta).add(0, focusedEntity.getEyeHeight(focusedEntity.getPose()), 0));
			((ICamera)instance).setCamRot(ModuleManager.getModule(Freecam.class).getYaw(tickDelta), ModuleManager.getModule(Freecam.class).getPitch(tickDelta));
		} else {
			instance.update(area, focusedEntity, thirdPerson, inverseView, tickDelta);
		}
		wasFreecamEnabled = ModuleManager.getModule(Freecam.class).isEnabled();
	}
	
	@Inject(method = "updateTargetedEntity", at = @At("HEAD"), cancellable = true)
	public void updateTargetedEntity(float tickDelta, CallbackInfo ci) {
		if(client.getCameraEntity() == null) {
			return;
		}
		if(ModuleManager.getModule(Freecam.class).isEnabled() && !calledFromFreecam) {
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
			
			((IVec3d)entity.getPos()).set(ModuleManager.getModule(Freecam.class).pos);
			entity.prevX = ModuleManager.getModule(Freecam.class).prevPos.x;
			entity.prevY = ModuleManager.getModule(Freecam.class).prevPos.y;
			entity.prevZ = ModuleManager.getModule(Freecam.class).prevPos.z;
			entity.setYaw(ModuleManager.getModule(Freecam.class).yaw);
			entity.setPitch(ModuleManager.getModule(Freecam.class).pitch);
			entity.prevYaw = ModuleManager.getModule(Freecam.class).prevYaw;
			entity.prevPitch = ModuleManager.getModule(Freecam.class).prevPitch;
			
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
	
	@Shadow protected abstract void bobView(MatrixStack matrices, float tickDelta);
}
