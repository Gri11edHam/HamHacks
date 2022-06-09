package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.mixininterface.IKeyBinding;
import net.grilledham.hamhacks.modules.render.ClickGUI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.GameOptions;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class MixinKeyboardInput extends Input {
	
	@Final
	@Shadow
	private GameOptions settings;
	
	@Shadow
	private static float getMovementMultiplier(boolean positive, boolean negative) {
		return 0;
	}
	
	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	public void tick(boolean slowDown, float f, CallbackInfo ci) {
		boolean forward = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), ((IKeyBinding)settings.forwardKey).getBound().getCode()) == GLFW.GLFW_PRESS && ClickGUI.getInstance().moveInScreen(MinecraftClient.getInstance().currentScreen);
		boolean back = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), ((IKeyBinding)settings.backKey).getBound().getCode()) == GLFW.GLFW_PRESS && ClickGUI.getInstance().moveInScreen(MinecraftClient.getInstance().currentScreen);
		boolean left = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), ((IKeyBinding)settings.leftKey).getBound().getCode()) == GLFW.GLFW_PRESS && ClickGUI.getInstance().moveInScreen(MinecraftClient.getInstance().currentScreen);
		boolean right = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), ((IKeyBinding)settings.rightKey).getBound().getCode()) == GLFW.GLFW_PRESS && ClickGUI.getInstance().moveInScreen(MinecraftClient.getInstance().currentScreen);
		boolean jump = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), ((IKeyBinding)settings.jumpKey).getBound().getCode()) == GLFW.GLFW_PRESS && ClickGUI.getInstance().moveInScreen(MinecraftClient.getInstance().currentScreen);
		boolean sneak = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), ((IKeyBinding)settings.sneakKey).getBound().getCode()) == GLFW.GLFW_PRESS && ClickGUI.getInstance().moveInScreen(MinecraftClient.getInstance().currentScreen);
		this.pressingForward = this.settings.forwardKey.isPressed() || forward;
		this.pressingBack = this.settings.backKey.isPressed() || back;
		this.pressingLeft = this.settings.leftKey.isPressed() || left;
		this.pressingRight = this.settings.rightKey.isPressed() || right;
		this.movementForward = getMovementMultiplier(this.pressingForward, this.pressingBack);
		this.movementSideways = getMovementMultiplier(this.pressingLeft, this.pressingRight);
		this.jumping = this.settings.jumpKey.isPressed() || jump;
		this.sneaking = this.settings.sneakKey.isPressed() || sneak;
		if (slowDown) {
			this.movementSideways = (float)((double)this.movementSideways * f);
			this.movementForward = (float)((double)this.movementForward * f);
		}
		ci.cancel();
	}
}
