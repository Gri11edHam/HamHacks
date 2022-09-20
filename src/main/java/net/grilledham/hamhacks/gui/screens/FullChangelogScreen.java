package net.grilledham.hamhacks.gui.screens;

import net.grilledham.hamhacks.gui.parts.impl.ButtonPart;
import net.grilledham.hamhacks.util.Changelog;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class FullChangelogScreen extends Screen {
	
	private final Screen last;
	
	private ButtonPart backButton;
	
	private ScrollableWidget changelog;
	
	protected FullChangelogScreen(Screen last) {
		super(Text.translatable("hamhacks.menu.fullchangelog"));
		this.last = last;
	}
	
	@Override
	protected void init() {
		super.init();
		backButton = new ButtonPart("Back", width / 2f - 100, height - 32, 200, 20, this::close);
		int changelogHeight = (textRenderer.fontHeight + 2) * Changelog.getChangelog().split("\n").length;
		float clw = 0;
		for(String s : Changelog.getChangelog().split("\n")) {
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
				for(String s : Changelog.getChangelog().split("\n")) {
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
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(backButton.click(mouseX, mouseY, 0, 0, button)) {
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if(backButton.release(mouseX, mouseY, 0, 0, button)) {
			return true;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}
	
	@Override
	public void close() {
		client.setScreen(last);
	}
}
