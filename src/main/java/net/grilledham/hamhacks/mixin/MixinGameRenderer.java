package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.event.events.EventRender;
import net.grilledham.hamhacks.event.events.EventRender2D;
import net.grilledham.hamhacks.event.events.EventRender3D;
import net.grilledham.hamhacks.mixininterface.ICamera;
import net.grilledham.hamhacks.mixininterface.IGameRenderer;
import net.grilledham.hamhacks.mixininterface.IVec3d;
import net.grilledham.hamhacks.modules.ModuleManager;
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
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.BlockView;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer implements SynchronousResourceReloader, AutoCloseable, IGameRenderer {
	
	@Shadow public abstract void updateCrosshairTarget(float tickDelta);
	
	@Shadow @Final private MinecraftClient client;
	
	@Unique
	private boolean wasFreecamEnabled = false;
	
	@Unique
	private boolean calledFromFreecam = false;
	
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix4fStack;popMatrix()Lorg/joml/Matrix4fStack;", remap = false), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void renderEvent(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci, Profiler profiler, boolean bl, int i, int j, Window window, Matrix4f matrix4f, Matrix4fStack matrix4fStack, DrawContext drawContext) {
		EventRender event = new EventRender(drawContext, tickCounter.getTickDelta(false));
		event.call();
	}
	
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V"))
	public void render2DEvent(InGameHud instance, DrawContext context, RenderTickCounter tickCounter) {
		EventRender2D event = new EventRender2D(context, tickCounter.getTickDelta(false));
		event.call();
		instance.render(context, tickCounter);
	}
	
	@Inject(method = "renderWorld", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=hand"), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void render3DEvent(RenderTickCounter renderTickCounter, CallbackInfo ci, float f, Profiler profiler, boolean bl, Camera camera, Entity entity, float g, float h, Matrix4f matrix4f, MatrixStack matrixStack, float i, float j, float n, Matrix4f matrix4f2, Quaternionf quaternionf, Matrix4f matrix4f3) {
		MatrixStack matrices = new MatrixStack();
		matrices.push();
		matrices.multiplyPositionMatrix(matrix4f3);
		
		ProjectionUtil.updateMatrices(matrices, matrix4f);
		
		EventRender3D event = new EventRender3D(renderTickCounter.getTickDelta(false), matrices);
		event.call();
	
		matrices.pop();
	}
	
	@Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Ljava/lang/Boolean;booleanValue()Z"))
	public boolean modelBobbingOnly(Boolean b) {
		if(ModuleManager.getModule(Bob.class).modelBobbingOnly.get() && ModuleManager.getModule(Bob.class).isEnabled()) {
			return false;
		}
		return b;
	}
	
	@Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true)
	public void noHurtCam(MatrixStack matrices, float f, CallbackInfo ci) {
		if(ModuleManager.getModule(Bob.class).noHurtCam.get() && ModuleManager.getModule(Bob.class).isEnabled()) {
			ci.cancel();
		}
	}
	
	@Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
	public void modifyFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Float> cir) {
		float f = cir.getReturnValueF();
		HandRender handRender = ModuleManager.getModule(HandRender.class);
		if(!changingFov && handRender.isEnabled()) {
			f *= handRender.fovMultiplier.get();
		}
		if(changingFov || !ModuleManager.getModule(Zoom.class).renderHand.get()) {
			f = ModuleManager.getModule(Zoom.class).modifyFov(f);
		}
		cir.setReturnValue(f);
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
			((ICamera)instance).hamHacks$setCamPos(ModuleManager.getModule(Freecam.class).getPos(tickDelta).add(0, focusedEntity.getEyeHeight(focusedEntity.getPose()), 0));
			((ICamera)instance).hamHacks$setCamRot(ModuleManager.getModule(Freecam.class).getYaw(tickDelta), ModuleManager.getModule(Freecam.class).getPitch(tickDelta));
		} else {
			instance.update(area, focusedEntity, thirdPerson, inverseView, tickDelta);
		}
		wasFreecamEnabled = ModuleManager.getModule(Freecam.class).isEnabled();
	}
	
	@Inject(method = "updateCrosshairTarget", at = @At("HEAD"), cancellable = true)
	public void updateCrosshairTarget(float tickDelta, CallbackInfo ci) {
		if(client.getCameraEntity() == null) {
			return;
		}
		Freecam freecam = ModuleManager.getModule(Freecam.class);
		if(freecam.isEnabled() && freecam.targetMode.get() == 1 && !calledFromFreecam) {
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
			
			((IVec3d)entity.getPos()).hamHacks$set(freecam.pos);
			entity.prevX = freecam.prevPos.x;
			entity.prevY = freecam.prevPos.y;
			entity.prevZ = freecam.prevPos.z;
			entity.setYaw(freecam.yaw);
			entity.setPitch(freecam.pitch);
			entity.prevYaw = freecam.prevYaw;
			entity.prevPitch = freecam.prevPitch;
			
			calledFromFreecam = true;
			updateCrosshairTarget(tickDelta);
			calledFromFreecam = false;
			
			((IVec3d)entity.getPos()).hamHacks$set(pos);
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
	public double hamHacks$getFOV(Camera camera, float tickDelta, boolean changingFov) {
		return getFov(camera, tickDelta, changingFov);
	}
	
	@Shadow protected abstract float getFov(Camera camera, float tickDelta, boolean changingFov);
	
	@Shadow
	public abstract void close();
}
