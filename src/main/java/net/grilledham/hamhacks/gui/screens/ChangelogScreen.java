package net.grilledham.hamhacks.gui.screens;

import net.grilledham.hamhacks.gui.parts.impl.ButtonPart;
import net.grilledham.hamhacks.util.Changelog;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ChangelogScreen extends Screen {
	
	private final Screen last;
	
	private ButtonPart backButton;
	private ButtonPart fullChangelogButton;
	
	private ScrollableWidget changelog;
	
	public ChangelogScreen(Screen last) {
		super(Text.translatable("hamhacks.menu.changelog"));
		this.last = last;
	}
	
	@Override
	protected void init() {
		super.init();
		backButton = new ButtonPart("Back", width / 2f - 102, height - 32, 100, 20, this::close);
		fullChangelogButton = new ButtonPart("Full Changelog", width / 2f + 2, height - 32, 100, 20, () -> client.setScreen(new FullChangelogScreen(this)));
		int changelogHeight = (textRenderer.fontHeight + 2) * Changelog.getLatest().split("\n").length;
		float clw = 0;
		for(String s : Changelog.getLatest().split("\n")) {
			clw = Math.max(clw, client.textRenderer.getWidth(s.replace("\t", "    ").replace("\r", "")));
		}
		int finalChangelogHeight = Math.min(changelogHeight + 8, height - 38);
		boolean overflows = changelogHeight + 8 > height - 38;
		addDrawableChild(changelog = new ScrollableWidget((int)(width / 2f - clw / 2f) - 4, 4, (int)(clw + 8), finalChangelogHeight, Text.empty()) {
			@Override
			protected int getContentsHeight() {
				return changelogHeight;
			}
			
			@Override
			protected boolean overflows() {
				return overflows;
			}
			
			@Override
			protected double getDeltaYPerScroll() {
				return 10;
			}
			
			@Override
			protected void renderContents(MatrixStack matrices, int mouseX, int mouseY, float delta) {
				int i = 0;
				for(String s : Changelog.getLatest().split("\n")) {
					client.textRenderer.drawWithShadow(matrices, s.replace("\t", "    ").replace("\r", ""), x + 4, y + 4 + i * (textRenderer.fontHeight + 2), 0xffffffff);
					i++;
				}
			}
			
			@Override
			public void appendNarrations(NarrationMessageBuilder builder) {
			
			}
		});
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		
		super.render(matrices, mouseX, mouseY, delta);
		
		backButton.draw(matrices, mouseX, mouseY, 0, 0, delta);
		fullChangelogButton.draw(matrices, mouseX, mouseY, 0, 0, delta);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(backButton.click(mouseX, mouseY, 0, 0, button)) {
			return true;
		}
		if(fullChangelogButton.click(mouseX, mouseY, 0, 0, button)) {
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if(backButton.release(mouseX, mouseY, 0, 0, button)) {
			return true;
		}
		if(fullChangelogButton.release(mouseX, mouseY, 0, 0, button)) {
			return true;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}
	
	@Override
	public void close() {
		client.setScreen(last);
	}
}
