package net.grilledham.hamhacks.notification;

import net.grilledham.hamhacks.animation.Animation;
import net.grilledham.hamhacks.animation.AnimationBuilder;
import net.grilledham.hamhacks.animation.AnimationType;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class Notification {
	
	private final Animation inOutAnimation = AnimationBuilder.create(AnimationType.IN_OUT_QUAD, 0.1, true).then(AnimationType.IN_OUT_QUAD, 0.15, true).build();
	private final Animation dropAnimation = AnimationBuilder.create(AnimationType.IN_OUT_QUAD, 0.25).build();
	
	private final Animation hoverAnimation = AnimationBuilder.create(AnimationType.IN_OUT_QUAD, 0.25).build();
	
	private final Animation progressAnimation = AnimationBuilder.create(AnimationType.LINEAR, PageManager.getPage(Notifications.class).lifeSpan.get()).build();
	
	private final List<String> titleTexts = new ArrayList<>();
	private final List<String> infoTexts = new ArrayList<>();
	
	private boolean complete = false;
	
	private final MinecraftClient mc = MinecraftClient.getInstance();
	private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
	
	private static final float WIDTH = 200;
	private final float height;
	
	private boolean clicked = false;
	
	private final Runnable clickEvent;
	
	public Notification(String title, String info, Runnable clickEvent) {
		this.clickEvent = clickEvent;
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
		progressAnimation.setAbsolute(0);
		progressAnimation.set(1);
	}
	
	public Notification(String title, String info) {
		this(title, info, null);
	}
	
	public float render(MatrixStack matrices, double mx, double my, float yAdd, float partialTicks) {
		matrices.push();
		
		double totalWidth = WIDTH + 5 + 10;
		double inOutAdd;
		if(inOutAnimation.getCurrentStage() == 0) {
			inOutAdd = inOutAnimation.getStage() * totalWidth;
		} else {
			inOutAdd = totalWidth - (inOutAnimation.getStage() * 10);
		}
		
		float x = mc.getWindow().getScaledWidth() - (float)inOutAdd;
		float y = mc.getWindow().getScaledHeight() - height - 5 - (float)dropAnimation.get();
		
		boolean hovered = mx >= x && mx <= x + WIDTH && my >= y && my <= y + height;
		
		int bgColor = RenderUtil.mix(PageManager.getPage(Notifications.class).bgColorHovered.get().getRGB(), PageManager.getPage(Notifications.class).bgColor.get().getRGB(), hoverAnimation.get());
		
		RenderUtil.preRender();
		RenderUtil.drawRect(matrices, x, y, WIDTH, height - 2, bgColor);
		RenderUtil.drawHRect(matrices, x - 1, y - 1, WIDTH + 2, height + 2, PageManager.getPage(Notifications.class).accentColor.get().getRGB());
		
		float progressBarPercentage = (float)progressAnimation.get();
		RenderUtil.drawRect(matrices, x + WIDTH * progressBarPercentage, y + height - 2, WIDTH * (1 - progressBarPercentage), 2, PageManager.getPage(Notifications.class).progressColorBG.get().getRGB());
		RenderUtil.drawRect(matrices, x, y + height - 2, WIDTH * progressBarPercentage, 2, PageManager.getPage(Notifications.class).progressColor.get().getRGB());
		
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
		hoverAnimation.set(hovered);
		inOutAnimation.update();
		dropAnimation.update();
		hoverAnimation.update();
		if(!clicked) {
			if(hovered) {
				progressAnimation.setAbsolute(progressAnimation.get());
				progressAnimation.setDuration(PageManager.getPage(Notifications.class).lifeSpan.get() * (1 - progressAnimation.get()));
			} else {
				progressAnimation.set(1);
			}
		}
		progressAnimation.update();
		
		if(progressAnimation.get() >= 1 && dropAnimation.get() <= 0 && (!hovered || clicked)) {
			complete = true;
		}
		
		return height;
	}
	
	public boolean click(double mx, double my, int button) {
		float x = mc.getWindow().getScaledWidth() - WIDTH - 5 + ((WIDTH + 5) * (float)(1 - inOutAnimation.get()));
		float y = mc.getWindow().getScaledHeight() - height - 5 - (float)dropAnimation.get();
		
		if(mx >= x && mx <= x + WIDTH && my >= y && my <= y + height && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			clicked = true;
			progressAnimation.setDuration(0.25);
			progressAnimation.setAbsolute(progressAnimation.get());
			progressAnimation.set(1);
			if(clickEvent != null) {
				clickEvent.run();
			}
			return true;
		}
		return false;
	}
	
	public boolean isComplete() {
		return inOutAnimation.get() <= 0 && complete;
	}
}
