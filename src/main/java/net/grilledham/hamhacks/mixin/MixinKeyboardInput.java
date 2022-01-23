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
public class MixinKeyboardInput extends Input {
	
	@Final
	@Shadow
	private GameOptions settings;
	
	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	public void tick(boolean slowDown, CallbackInfo ci) {
		boolean forward = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), ((IKeyBinding)settings.keyForward).getBound().getCode()) == GLFW.GLFW_PRESS && ClickGUI.getInstance().moveInScreen(MinecraftClient.getInstance().currentScreen);
		boolean back = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), ((IKeyBinding)settings.keyBack).getBound().getCode()) == GLFW.GLFW_PRESS && ClickGUI.getInstance().moveInScreen(MinecraftClient.getInstance().currentScreen);
		boolean left = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), ((IKeyBinding)settings.keyLeft).getBound().getCode()) == GLFW.GLFW_PRESS && ClickGUI.getInstance().moveInScreen(MinecraftClient.getInstance().currentScreen);
		boolean right = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), ((IKeyBinding)settings.keyRight).getBound().getCode()) == GLFW.GLFW_PRESS && ClickGUI.getInstance().moveInScreen(MinecraftClient.getInstance().currentScreen);
		boolean jump = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), ((IKeyBinding)settings.keyJump).getBound().getCode()) == GLFW.GLFW_PRESS && ClickGUI.getInstance().moveInScreen(MinecraftClient.getInstance().currentScreen);
		boolean sneak = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), ((IKeyBinding)settings.keySneak).getBound().getCode()) == GLFW.GLFW_PRESS && ClickGUI.getInstance().moveInScreen(MinecraftClient.getInstance().currentScreen);
		this.pressingForward = this.settings.keyForward.isPressed() || forward;
		this.pressingBack = this.settings.keyBack.isPressed() || back;
		this.pressingLeft = this.settings.keyLeft.isPressed() || left;
		this.pressingRight = this.settings.keyRight.isPressed() || right;
		this.movementForward = this.pressingForward == this.pressingBack ? 0.0F : (this.pressingForward ? 1.0F : -1.0F);
		this.movementSideways = this.pressingLeft == this.pressingRight ? 0.0F : (this.pressingLeft ? 1.0F : -1.0F);
		this.jumping = this.settings.keyJump.isPressed() || jump;
		this.sneaking = this.settings.keySneak.isPressed() || sneak;
		if (slowDown) {
			this.movementSideways = (float)((double)this.movementSideways * 0.3D);
			this.movementForward = (float)((double)this.movementForward * 0.3D);
		}
		ci.cancel();
	}
}
