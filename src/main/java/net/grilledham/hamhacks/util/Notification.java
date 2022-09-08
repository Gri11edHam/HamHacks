package net.grilledham.hamhacks.util;

import net.grilledham.hamhacks.modules.render.Notifications;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class Notification {
	
	private final Animation inOutAnimation = Animation.getInOutQuad(0.25, true);
	private final Animation dropAnimation = Animation.getInOutQuad(0.25);
	
	private final List<String> titleTexts = new ArrayList<>();
	private final List<String> infoTexts = new ArrayList<>();
	
	private final long completionTime;
	
	private boolean complete = false;
	
	private final MinecraftClient mc = MinecraftClient.getInstance();
	private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
	
	private static final float WIDTH = 200;
	private final float height;
	
	public Notification(String title, String info) {
		int i = 1;
		float titleH = textRenderer.fontHeight;
		float infoH = textRenderer.fontHeight;
		if(textRenderer.getWidth(title) > WIDTH - 10) {
			StringBuilder line = new StringBuilder();
			for(String s : title.split("\\s")) {
				if(textRenderer.getWidth((line + " " + s).trim()) > WIDTH - 10) {
					titleTexts.add(line.toString().trim());
					line = new StringBuilder();
					i++;
				}
				line.append(" ").append(s);
			}
			titleTexts.add(line.toString().trim());
			titleH = (textRenderer.fontHeight + 2) * i;
		} else {
			titleTexts.add(title);
		}
		i = 1;
		if(textRenderer.getWidth(info) > WIDTH - 10) {
			StringBuilder line = new StringBuilder();
			for(String s : info.split("\\s")) {
				if(textRenderer.getWidth((line + " " + s).trim()) > WIDTH - 10) {
					infoTexts.add(line.toString().trim());
					line = new StringBuilder();
					i++;
				}
				line.append(" ").append(s);
			}
			infoTexts.add(line.toString().trim());
			infoH = (textRenderer.fontHeight + 2) * i;
		} else {
			infoTexts.add(info);
		}
		height = 5 + titleH + 5 + infoH + 5 + 2;
		completionTime = System.currentTimeMillis() + (long)(Notifications.getInstance().lifeSpan * 1000);
	}
	
	public float render(MatrixStack matrices, float yAdd, float partialTicks) {
		matrices.push();
		
		float x = mc.getWindow().getScaledWidth() - WIDTH - 5 + ((WIDTH + 5) * (float)(1 - inOutAnimation.get()));
		float y = mc.getWindow().getScaledHeight() - height - 5 - (float)dropAnimation.get();
		
		RenderUtil.preRender();
		RenderUtil.drawRect(matrices, x, y, WIDTH, height - 2, Notifications.getInstance().bgColor.getRGB());
		RenderUtil.drawHRect(matrices, x - 1, y - 1, WIDTH + 2, height + 2, Notifications.getInstance().accentColor.getRGB());
		
		float progressBarPercentage = MathHelper.clamp(1 - ((completionTime - System.currentTimeMillis()) /  (Notifications.getInstance().lifeSpan * 1000)), 0, 1);
		RenderUtil.drawRect(matrices, x + WIDTH * progressBarPercentage, y + height - 2, WIDTH * (1 - progressBarPercentage), 2, Notifications.getInstance().progressColorBG.getRGB());
		RenderUtil.drawRect(matrices, x, y + height - 2, WIDTH * progressBarPercentage, 2, Notifications.getInstance().progressColor.getRGB());
		
		RenderUtil.postRender();
		
		int i = 0;
		for(String s : titleTexts) {
			textRenderer.drawWithShadow(matrices, s, x + 5, y + 5 + (textRenderer.fontHeight + 2) * i, -1);
			i++;
		}
		for(String s : infoTexts) {
			textRenderer.drawWithShadow(matrices, s, x + 5, y + 5 + 5 + (textRenderer.fontHeight + 2) * i, -1);
			i++;
		}
		
		matrices.pop();
		
		inOutAnimation.set(!complete);
		dropAnimation.set(yAdd);
		inOutAnimation.update();
		dropAnimation.update();
		
		if(completionTime <= System.currentTimeMillis()) {
			complete = true;
		}
		
		return height;
	}
	
	public boolean isComplete() {
		return inOutAnimation.get() <= 0 && complete;
	}
}
