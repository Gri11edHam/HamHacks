package net.grilledham.hamhacks.gui.screens;

import net.grilledham.hamhacks.gui.parts.impl.ButtonPart;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class UpdateScreen extends Screen {
	
	private final Screen last;
	
	private ButtonPart continueButton;
	
	private ButtonPart exitButton;
	
	public UpdateScreen(Screen last) {
		super(Text.translatable("hamhacks.menu.update"));
		this.last = last;
	}
	
	@Override
	protected void init() {
		super.init();
		continueButton = new ButtonPart("Continue", width / 2 - 101, height / 2 - 10, 100, 20, this::close);
		exitButton = new ButtonPart("Exit", width / 2 + 1, height / 2 - 10, 100, 20, () -> MinecraftClient.getInstance().scheduleStop());
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		
		super.render(matrices, mouseX, mouseY, delta);
		
		String s = "Would you like to close Minecraft now or continue playing?";
		client.textRenderer.drawWithShadow(matrices, s, width / 2f - client.textRenderer.getWidth(s) / 2f, height / 2f - 30 - client.textRenderer.fontHeight / 2f, -1);
		String s2 = "(The mod won't be fully updated until you restart your game)";
		client.textRenderer.drawWithShadow(matrices, s2, width / 2f - client.textRenderer.getWidth(s2) / 2f, height / 2f - 20 - client.textRenderer.fontHeight / 2f, -1);
		
		continueButton.draw(matrices, mouseX, mouseY, delta);
		exitButton.draw(matrices, mouseX, mouseY, delta);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(continueButton.click(mouseX, mouseY, button) || exitButton.click(mouseX, mouseY, button)) {
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if(continueButton.release(mouseX, mouseY, button) || exitButton.release(mouseX, mouseY, button)) {
			return true;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}
	
	@Override
	public void close() {
		client.setScreen(last);
	}
}
