package net.grilledham.hamhacks.gui.screens;

import net.grilledham.hamhacks.gui.parts.impl.ButtonPart;
import net.grilledham.hamhacks.util.Changelog;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ChangelogScreen extends Screen {
	
	private final Screen last;
	
	private ButtonPart backButton;
	private ButtonPart fullChangelogButton;
	
	public ChangelogScreen(Screen last) {
		super(Text.translatable("menu.hamhacks.changelog"));
		this.last = last;
	}
	
	@Override
	protected void init() {
		super.init();
		backButton = new ButtonPart("Back", width / 2 - 100, height - 32, 200, 20, this::close);
		fullChangelogButton = new ButtonPart("Full Changelog", width / 2 - 100, height - 54, 200, 20, () -> client.setScreen(new FullChangelogScreen(this)));
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		matrices.push();
		
		super.render(matrices, mouseX, mouseY, delta);
		renderBackground(matrices);
		float clw = 0;
		for(String s : Changelog.getLatest().split("\n")) {
			clw = Math.max(clw, client.textRenderer.getWidth(s.replace("\t", "    ")));
		}
		int i = 0;
		for(String s : Changelog.getLatest().split("\n")) {
			client.textRenderer.drawWithShadow(matrices, s.replace("\t", "    "), width / 2f - clw / 2f, 12 + i * 12, 0xffffffff);
			i++;
		}
		backButton.draw(matrices, mouseX, mouseY, delta);
		fullChangelogButton.draw(matrices, mouseX, mouseY, delta);
		
		matrices.pop();
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(backButton.click(mouseX, mouseY, button)) {
			return true;
		}
		if(fullChangelogButton.click(mouseX, mouseY, button)) {
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if(backButton.release(mouseX, mouseY, button)) {
			return true;
		}
		if(fullChangelogButton.release(mouseX, mouseY, button)) {
			return true;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}
	
	@Override
	public void close() {
		client.setScreen(last);
	}
}
