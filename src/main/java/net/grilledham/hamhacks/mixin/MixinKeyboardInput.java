package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.gui.ClickGUIScreen;
import net.grilledham.hamhacks.mixininterface.IKeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.GameOptions;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(KeyboardInput.class)
public class MixinKeyboardInput extends Input {
	
	@Final
	@Shadow
	private GameOptions settings;
	
	public void tick(boolean slowDown) {
		boolean forward = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), ((IKeyBinding)settings.keyForward).getBound().getCode()) == GLFW.GLFW_PRESS && MinecraftClient.getInstance().currentScreen instanceof ClickGUIScreen;
		boolean back = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), ((IKeyBinding)settings.keyBack).getBound().getCode()) == GLFW.GLFW_PRESS && MinecraftClient.getInstance().currentScreen instanceof ClickGUIScreen;
		boolean left = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), ((IKeyBinding)settings.keyLeft).getBound().getCode()) == GLFW.GLFW_PRESS && MinecraftClient.getInstance().currentScreen instanceof ClickGUIScreen;
		boolean right = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), ((IKeyBinding)settings.keyRight).getBound().getCode()) == GLFW.GLFW_PRESS && MinecraftClient.getInstance().currentScreen instanceof ClickGUIScreen;
		boolean jump = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), ((IKeyBinding)settings.keyJump).getBound().getCode()) == GLFW.GLFW_PRESS && MinecraftClient.getInstance().currentScreen instanceof ClickGUIScreen;
		boolean sneak = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), ((IKeyBinding)settings.keySneak).getBound().getCode()) == GLFW.GLFW_PRESS && MinecraftClient.getInstance().currentScreen instanceof ClickGUIScreen;
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
	}
}
