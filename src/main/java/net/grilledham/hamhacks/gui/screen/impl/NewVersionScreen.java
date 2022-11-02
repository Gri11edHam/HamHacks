package net.grilledham.hamhacks.gui.screen.impl;

import net.grilledham.hamhacks.gui.element.impl.ButtonElement;
import net.grilledham.hamhacks.util.Updater;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

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
			Updater.update();
			MinecraftClient.getInstance().setScreen(new UpdateScreen(last));
		});
		cancelButton = new ButtonElement("Cancel", width / 2f - 101, height - 30, 100, 20, (float)client.getWindow().getScaleFactor(), this::close);
		int changelogHeight = (textRenderer.fontHeight + 2) * Updater.getChangelog().split("\n").length;
		float clw = 0;
		for(String s : Updater.getChangelog().split("\n")) {
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
				for(String s : Updater.getChangelog().split("\n")) {
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
		
		updateButton.render(matrices, mouseX, mouseY, 0, 0, delta);
		cancelButton.render(matrices, mouseX, mouseY, 0, 0, delta);
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
