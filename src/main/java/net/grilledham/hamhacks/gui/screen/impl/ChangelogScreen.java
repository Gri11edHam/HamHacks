package net.grilledham.hamhacks.gui.screen.impl;

import net.grilledham.hamhacks.gui.element.impl.ButtonElement;
import net.grilledham.hamhacks.util.Changelog;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;

public class ChangelogScreen extends Screen {
	
	private final Screen last;
	
	private ButtonElement backButton;
	private ButtonElement fullChangelogButton;
	
	private ScrollableWidget changelog;
	
	public ChangelogScreen(Screen last) {
		super(Text.translatable("hamhacks.menu.changelog"));
		this.last = last;
	}
	
	@Override
	protected void init() {
		super.init();
		backButton = new ButtonElement("Back", width / 2f - 102, height - 32, 100, 20, (float)client.getWindow().getScaleFactor(), this::close);
		fullChangelogButton = new ButtonElement("Full Changelog", width / 2f + 2, height - 32, 100, 20, (float)client.getWindow().getScaleFactor(), () -> client.setScreen(new FullChangelogScreen(this)));
		float changelogHeight = (RenderUtil.getFontHeight() + 2) * Changelog.getLatest().split("\n").length;
		float clw = 0;
		for(String s : Changelog.getLatest().split("\n")) {
			clw = Math.max(clw, RenderUtil.getStringWidth(s.replace("\t", "    ").replace("\r", "")));
		}
		float finalChangelogHeight = Math.min(changelogHeight + 8, height - 38);
		boolean overflows = changelogHeight + 8 > height - 38;
		addDrawableChild(changelog = new ScrollableWidget((int)(width / 2f - clw / 2f) - 4, 4, (int)(clw + 8), (int)finalChangelogHeight, Text.empty()) {
			@Override
			protected int getContentsHeight() {
				return (int)changelogHeight;
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
			protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
				int i = 0;
				for(String s : Changelog.getLatest().split("\n")) {
					RenderUtil.drawString(context, s.replace("\t", "    ").replace("\r", ""), getX() + 4, getY() + 4 + i * (RenderUtil.getFontHeight() + 2), 0xffffffff, true);
					i++;
				}
			}
			
			@Override
			protected void appendClickableNarrations(NarrationMessageBuilder builder) {
			}
		});
	}
	
	@Override
	public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
		super.render(ctx, mouseX, mouseY, delta);
		
		backButton.render(ctx, mouseX, mouseY, 0, 0, delta);
		fullChangelogButton.render(ctx, mouseX, mouseY, 0, 0, delta);
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
