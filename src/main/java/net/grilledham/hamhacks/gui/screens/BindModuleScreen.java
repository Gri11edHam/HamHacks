package net.grilledham.hamhacks.gui.screens;

import net.grilledham.hamhacks.command.CommandManager;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.ChatUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class BindModuleScreen extends Screen {
	
	private final Module module;
	
	private boolean isFirstClick = true;
	
	public BindModuleScreen(Module module) {
		super(Text.translatable("menu.hamhacks.bindmodule"));
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
		info(Text.of("Bound " + module.getName() + " to " + module.getKey().getName()));
		close();
		return super.mouseReleased(mouseX, mouseY, button);
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return false;
	}
	
	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		if(isFirstClick) {
			isFirstClick = false;
			return super.keyReleased(keyCode, scanCode, modifiers);
		}
		if(keyCode == GLFW.GLFW_KEY_ESCAPE) {
			module.getKey().setKey(0);
		} else {
			module.getKey().setKey(keyCode, false);
		}
		info(Text.of("Bound " + module.getName() + " to " + module.getKey().getName()));
		close();
		return super.keyReleased(keyCode, scanCode, modifiers);
	}
	
	public void info(Text message, Object... args) {
		MutableText prefix = (MutableText)Text.of("[" + CommandManager.getCommand("bind").getTitle() + "]");
		prefix.setStyle(Style.EMPTY.withFormatting(Formatting.DARK_RED));
		MutableText separator = (MutableText)Text.of(" > ");
		separator.setStyle(Style.EMPTY.withFormatting(Formatting.DARK_GRAY));
		ChatUtil.info(prefix.append(separator), message, args);
	}
}
