package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.mixininterface.IKeyBinding;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.render.Freecam;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
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
		boolean forward = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), ((IKeyBinding)settings.forwardKey).hamHacks$getBound().getCode()) == GLFW.GLFW_PRESS && PageManager.getPage(ClickGUI.class).moveInScreen(MinecraftClient.getInstance().currentScreen) && !ModuleManager.getModule(Freecam.class).isEnabled();
		boolean back = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), ((IKeyBinding)settings.backKey).hamHacks$getBound().getCode()) == GLFW.GLFW_PRESS && PageManager.getPage(ClickGUI.class).moveInScreen(MinecraftClient.getInstance().currentScreen) && !ModuleManager.getModule(Freecam.class).isEnabled();
		boolean left = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), ((IKeyBinding)settings.leftKey).hamHacks$getBound().getCode()) == GLFW.GLFW_PRESS && PageManager.getPage(ClickGUI.class).moveInScreen(MinecraftClient.getInstance().currentScreen) && !ModuleManager.getModule(Freecam.class).isEnabled();
		boolean right = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), ((IKeyBinding)settings.rightKey).hamHacks$getBound().getCode()) == GLFW.GLFW_PRESS && PageManager.getPage(ClickGUI.class).moveInScreen(MinecraftClient.getInstance().currentScreen) && !ModuleManager.getModule(Freecam.class).isEnabled();
		boolean jump = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), ((IKeyBinding)settings.jumpKey).hamHacks$getBound().getCode()) == GLFW.GLFW_PRESS && PageManager.getPage(ClickGUI.class).moveInScreen(MinecraftClient.getInstance().currentScreen) && !ModuleManager.getModule(Freecam.class).isEnabled();
		boolean sneak = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), ((IKeyBinding)settings.sneakKey).hamHacks$getBound().getCode()) == GLFW.GLFW_PRESS && PageManager.getPage(ClickGUI.class).moveInScreen(MinecraftClient.getInstance().currentScreen) && !ModuleManager.getModule(Freecam.class).isEnabled();
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
