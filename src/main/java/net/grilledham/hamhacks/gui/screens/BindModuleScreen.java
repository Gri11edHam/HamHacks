package net.grilledham.hamhacks.gui.screens;

import net.grilledham.hamhacks.modules.Module;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import org.lwjgl.glfw.GLFW;

public class BindModuleScreen extends Screen {
	
	private final Module module;
	
	public BindModuleScreen(Module module) {
		super(new TranslatableText("menu.hamhacks.bindmodule"));
		this.module = module;
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		textRenderer.drawWithShadow(matrices, "Listening...", width / 2f - textRenderer.getWidth("Listening...") / 2f, height / 2f - textRenderer.fontHeight / 2f, -1);
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		module.getKey().setKey(button, true);
		return super.mouseReleased(mouseX, mouseY, button);
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if(keyCode == GLFW.GLFW_KEY_ESCAPE) {
			module.getKey().setKey(0);
		} else {
			module.getKey().setKey(keyCode, false);
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
}
