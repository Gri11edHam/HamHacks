package net.grilledham.hamhacks.gui.screen.impl;

import net.grilledham.hamhacks.gui.element.impl.ButtonElement;
import net.grilledham.hamhacks.util.RenderUtil;
import net.grilledham.hamhacks.util.Updater;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class NewVersionScreen extends Screen {
	
	private final Screen last;
	
	private ButtonElement updateButton;
	
	private ButtonElement cancelButton;
	
	private ScrollableWidget changelog;
	
	public NewVersionScreen(Screen last) {
		super(Text.translatable("hamhacks.menu.newVersion"));
		this.last = last;
	}
	
	@Override
	protected void init() {
		super.init();
		updateButton = new ButtonElement("Update", width / 2f + 1, height - 30, 100, 20, (float)client.getWindow().getScaleFactor(), () -> {
			if(Updater.isDevUpdate()) {
				Util.getOperatingSystem().open(Updater.getDownloadURL());
			}
			Updater.update();
			MinecraftClient.getInstance().setScreen(new UpdateScreen(last));
		});
		cancelButton = new ButtonElement("Cancel", width / 2f - 101, height - 30, 100, 20, (float)client.getWindow().getScaleFactor(), this::close);
		float changelogHeight = (RenderUtil.getFontHeight() + 2) * Updater.getChangelog().split("\n").length;
		float clw = 0;
		for(String s : Updater.getChangelog().split("\n")) {
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
			protected void renderContents(DrawContext ctx, int mouseX, int mouseY, float delta) {
				int i = 0;
				for(String s : Updater.getChangelog().split("\n")) {
					RenderUtil.drawString(ctx, s.replace("\t", "    ").replace("\r", ""), getX() + 4, getY() + 4 + i * (RenderUtil.getFontHeight() + 2), 0xffffffff, true);
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
		
		updateButton.render(ctx, mouseX, mouseY, 0, 0, delta);
		cancelButton.render(ctx, mouseX, mouseY, 0, 0, delta);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(updateButton.click(mouseX, mouseY, 0, 0, button) || cancelButton.click(mouseX, mouseY, 0, 0, button)) {
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if(updateButton.release(mouseX, mouseY, 0, 0, button) || cancelButton.release(mouseX, mouseY, 0, 0, button)) {
			return true;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}
	
	@Override
	public void close() {
		client.setScreen(last);
	}
}
