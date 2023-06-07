package net.grilledham.hamhacks.gui.screen.impl;

import net.grilledham.hamhacks.gui.element.impl.ButtonElement;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class UpdateScreen extends Screen {
	
	private final Screen last;
	
	private ButtonElement continueButton;
	
	private ButtonElement exitButton;
	
	public UpdateScreen(Screen last) {
		super(Text.translatable("hamhacks.menu.update"));
		this.last = last;
	}
	
	@Override
	protected void init() {
		super.init();
		continueButton = new ButtonElement("Continue", width / 2f - 101, height / 2f - 10, 100, 20, (float)client.getWindow().getScaleFactor(), this::close);
		exitButton = new ButtonElement("Exit", width / 2f + 1, height / 2f - 10, 100, 20, (float)client.getWindow().getScaleFactor(), () -> MinecraftClient.getInstance().scheduleStop());
	}
	
	@Override
	public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
		renderBackground(ctx);
		
		super.render(ctx, mouseX, mouseY, delta);
		
		String s = "Would you like to close Minecraft now or continue playing?";
		RenderUtil.drawString(ctx, s, width / 2f - client.textRenderer.getWidth(s) / 2f, height / 2f - 30 - client.textRenderer.fontHeight / 2f, -1, true);
		String s2 = "(The mod won't be fully updated until you restart your game)";
		RenderUtil.drawString(ctx, s2, width / 2f - client.textRenderer.getWidth(s2) / 2f, height / 2f - 20 - client.textRenderer.fontHeight / 2f, -1, true);
		
		continueButton.render(ctx, mouseX, mouseY, 0, 0, delta);
		exitButton.render(ctx, mouseX, mouseY, 0, 0, delta);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(continueButton.click(mouseX, mouseY, 0, 0, button) || exitButton.click(mouseX, mouseY, 0, 0, button)) {
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if(continueButton.release(mouseX, mouseY, 0, 0, button) || exitButton.release(mouseX, mouseY, 0, 0, button)) {
			return true;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}
	
	@Override
	public void close() {
		client.setScreen(last);
	}
}
