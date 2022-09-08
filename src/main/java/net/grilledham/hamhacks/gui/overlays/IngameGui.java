package net.grilledham.hamhacks.gui.overlays;

import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.render.HUD;
import net.grilledham.hamhacks.util.Animation;
import net.grilledham.hamhacks.util.ConnectionUtil;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IngameGui {
	
	private static IngameGui INSTANCE;
	
	public static void register() {
		INSTANCE = new IngameGui();
	}
	
	public static IngameGui getInstance() {
		return INSTANCE;
	}
	
	private final List<Animation> animations = new ArrayList<>();
	
	public void render(MatrixStack matrices, float tickDelta, TextRenderer fontRenderer) {
		if(MinecraftClient.getInstance().options.debugEnabled) {
			return;
		}
		
		matrices.push();
		
		float[] barC = HUD.getInstance().accentColor.getHSB();
		float[] bgC = HUD.getInstance().bgColor.getHSB();
		float[] textC = HUD.getInstance().textColor.getHSB();
		
		int j = 0;
		int i = 0;
		float yAdd = 0;
		Animation animation = getAnimation(j++);
		if(HUD.getInstance().animate) {
			animation.set(HUD.getInstance().showLogo && HUD.getInstance().isEnabled());
		} else {
			animation.setAbsolute(HUD.getInstance().showLogo && HUD.getInstance().isEnabled());
		}
		if(animation.get() > 0) {
			float finalBarHue;
			if(HUD.getInstance().accentColor.getChroma()) {
				finalBarHue = (barC[0] - (i * 0.025f)) % 1f;
			} else {
				finalBarHue = barC[0];
			}
			float finalBGHue;
			if(HUD.getInstance().bgColor.getChroma()) {
				finalBGHue = (bgC[0] - (i * 0.025f)) % 1f;
			} else {
				finalBGHue = bgC[0];
			}
			float finalTextHue;
			if(HUD.getInstance().textColor.getChroma()) {
				finalTextHue = (textC[0] - (i * 0.025f)) % 1f;
			} else {
				finalTextHue = textC[0];
			}
			int barColor = (int)((Color.HSBtoRGB(finalBarHue, barC[1], barC[2]) & 0xffffff) + (barC[3] * 255));
			int bgColor = (int)((Color.HSBtoRGB(finalBGHue, bgC[1], bgC[2]) & 0xffffff) + (bgC[3] * 255));
			int textColor = (int)((Color.HSBtoRGB(finalTextHue, textC[1], textC[2])) + (textC[3] * 255));
			String text = "§4§o§lHamHacks";
			float textX = 2;
			float textY = (fontRenderer.fontHeight + 2) * i + 2;
//			DrawableHelper.fill(matrices, textX - 2, textY - 2, textX + fontRenderer.getWidth(text) + 2, textY + fontRenderer.fontHeight, bgColor);
//			DrawableHelper.fill(matrices, textX + fontRenderer.getWidth(text) + 2,  textX + fontRenderer.getWidth(MinecraftClient.getInstance().fpsDebugString) + 5, textX - 2, textY + fontRenderer.fontHeight, barColor);
			matrices.push();
			matrices.translate(textX, textY, 0);
			matrices.scale(2, 2, 1);
			matrices.translate(-textX, -textY, 0);
			fontRenderer.drawWithShadow(matrices, text, textX - (int)(fontRenderer.getWidth(text) * (1 - animation.get())), textY, textColor);
			matrices.pop();
			yAdd += ((fontRenderer.fontHeight * 2) + 4) * animation.get();
			i++;
		}
		animation = getAnimation(j++);
		if(HUD.getInstance().animate) {
			animation.set(HUD.getInstance().showFPS && HUD.getInstance().isEnabled());
		} else {
			animation.setAbsolute(HUD.getInstance().showFPS && HUD.getInstance().isEnabled());
		}
		if(animation.get() > 0) {
			String fps = MinecraftClient.getInstance().fpsDebugString;
			fps = fps.split(" ")[0] + " " + fps.split(" ")[1];
			yAdd += drawLeftAligned(matrices, tickDelta, fontRenderer, fps, i, yAdd, animation);
			i++;
		}
		animation = getAnimation(j++);
		if(HUD.getInstance().animate) {
			animation.set(HUD.getInstance().showPing && HUD.getInstance().isEnabled());
		} else {
			animation.setAbsolute(HUD.getInstance().showPing && HUD.getInstance().isEnabled());
		}
		if(animation.get() > 0) {
			String ping = "0 ms";
			if(MinecraftClient.getInstance().player != null) {
				PlayerListEntry playerListEntry = MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry(MinecraftClient.getInstance().player.getUuid());
				if(playerListEntry != null) {
					int latency = playerListEntry.getLatency();
					ServerInfo serverInfo = ConnectionUtil.getServerInfo();
					if(serverInfo != null) {
						ping = (latency <= 0 ? serverInfo.ping : latency) + " ms";
					} else {
						ping = latency + " ms";
					}
				}
			}
			yAdd += drawLeftAligned(matrices, tickDelta, fontRenderer, ping, i, yAdd, animation);
			i++;
		}
		animation = getAnimation(j++);
		if(HUD.getInstance().animate) {
			animation.set(HUD.getInstance().showTPS && HUD.getInstance().isEnabled());
		} else {
			animation.setAbsolute(HUD.getInstance().showTPS && HUD.getInstance().isEnabled());
		}
		if(animation.get() > 0) {
			String tps = String.format("%.2f tps", ConnectionUtil.getTPS());
			yAdd += drawLeftAligned(matrices, tickDelta, fontRenderer, tps, i, yAdd, animation);
			i++;
		}
		animation = getAnimation(j++);
		if(HUD.getInstance().animate) {
			animation.set(HUD.getInstance().showTimeSinceLastTick && HUD.getInstance().isEnabled());
		} else {
			animation.setAbsolute(HUD.getInstance().showTimeSinceLastTick && HUD.getInstance().isEnabled());
		}
		if(animation.get() > 0) {
			float timeSinceLastTick = ConnectionUtil.getTimeSinceLastTick() / 1000f;
			if(timeSinceLastTick >= 2) {
				String timeSinceLastTickString = String.format("Seconds Since Last Tick: %.2f", timeSinceLastTick);
				yAdd += drawLeftAligned(matrices, tickDelta, fontRenderer, timeSinceLastTickString, i, yAdd, animation);
				i++;
			}
		}
		yAdd = 0;
		int k = j;
		Map<Module, Animation> moduleAnimations = new HashMap<>();
		for(Module m : ModuleManager.getModules()) {
			animation = getAnimation(k++);
			if(HUD.getInstance().animate) {
				animation.set(m.isEnabled() && m.shouldShowModule() && HUD.getInstance().showModules && HUD.getInstance().isEnabled());
			} else {
				animation.setAbsolute(m.isEnabled() && m.shouldShowModule() && HUD.getInstance().showModules && HUD.getInstance().isEnabled());
			}
			moduleAnimations.put(m, animation);
		}
		for(Module m : ModuleManager.getModules().stream().sorted((a, b) -> Integer.compare(MinecraftClient.getInstance().textRenderer.getWidth(b.getHUDText()), MinecraftClient.getInstance().textRenderer.getWidth(a.getHUDText()))).toList()) {
			animation = moduleAnimations.get(m);
			j++;
			if(animation.get() > 0) {
				yAdd += drawRightAligned(matrices, tickDelta, fontRenderer, m.getHUDText(), i, yAdd, animation);
				i++;
			}
		}
		
		matrices.pop();
		
		animations.forEach(Animation::update);
	}
	
	private float drawLeftAligned(MatrixStack matrices, float tickDelta, TextRenderer fontRenderer, String text, int i, float yAdd, Animation animation) {
		float[] barC = HUD.getInstance().accentColor.getHSB();
		float[] bgC = HUD.getInstance().bgColor.getHSB();
		float[] textC = HUD.getInstance().textColor.getHSB();
		float finalBarHue;
		if(HUD.getInstance().accentColor.getChroma()) {
			finalBarHue = (barC[0] - (i * 0.025f)) % 1f;
		} else {
			finalBarHue = barC[0];
		}
		float finalBGHue;
		if(HUD.getInstance().bgColor.getChroma()) {
			finalBGHue = (bgC[0] - (i * 0.025f)) % 1f;
		} else {
			finalBGHue = bgC[0];
		}
		float finalTextHue;
		if(HUD.getInstance().textColor.getChroma()) {
			finalTextHue = (textC[0] - (i * 0.025f)) % 1f;
		} else {
			finalTextHue = textC[0];
		}
		int barColor = (Color.HSBtoRGB(finalBarHue, barC[1], barC[2]) & 0xffffff) + ((int)(barC[3] * 255) << 24);
		int bgColor = (Color.HSBtoRGB(finalBGHue, bgC[1], bgC[2]) & 0xffffff) + ((int)(bgC[3] * 255) << 24);
		int textColor = (Color.HSBtoRGB(finalTextHue, textC[1], textC[2])) + ((int)(textC[3] * 255) << 24);
		float textX = (float)(2 - ((fontRenderer.getWidth(text) + 7) * (1 - animation.get())));
		float textY = yAdd + 2;
		RenderUtil.preRender();
		RenderUtil.drawRect(matrices, textX - 2, textY - 2, fontRenderer.getWidth(text) + 4, fontRenderer.fontHeight + 2, bgColor);
		RenderUtil.drawRect(matrices, textX + fontRenderer.getWidth(text) + 2,  textY - 2, 3, fontRenderer.fontHeight + 2, barColor);
		RenderUtil.postRender();
		fontRenderer.drawWithShadow(matrices, text, textX, textY, textColor);
		return (float)((fontRenderer.fontHeight + 2) * animation.get());
	}
	
	private float drawRightAligned(MatrixStack matrices, float tickDelta, TextRenderer fontRenderer, String text, int i, float yAdd, Animation animation) {
		float[] barC = HUD.getInstance().accentColor.getHSB();
		float[] bgC = HUD.getInstance().bgColor.getHSB();
		float[] textC = HUD.getInstance().textColor.getHSB();
		float finalBarHue;
		if(HUD.getInstance().accentColor.getChroma()) {
			finalBarHue = (barC[0] - (i * 0.025f)) % 1f;
		} else {
			finalBarHue = barC[0];
		}
		float finalBGHue;
		if(HUD.getInstance().bgColor.getChroma()) {
			finalBGHue = (bgC[0] - (i * 0.025f)) % 1f;
		} else {
			finalBGHue = bgC[0];
		}
		float finalTextHue;
		if(HUD.getInstance().textColor.getChroma()) {
			finalTextHue = (textC[0] - (i * 0.025f)) % 1f;
		} else {
			finalTextHue = textC[0];
		}
		int barColor = (Color.HSBtoRGB(finalBarHue, barC[1], barC[2]) & 0xffffff) + ((int)(barC[3] * 255) << 24);
		int bgColor = (Color.HSBtoRGB(finalBGHue, bgC[1], bgC[2]) & 0xffffff) + ((int)(bgC[3] * 255) << 24);
		int textColor = (Color.HSBtoRGB(finalTextHue, textC[1], textC[2])) + ((int)(textC[3] * 255) << 24);
		float textX = MinecraftClient.getInstance().getWindow().getScaledWidth() - fontRenderer.getWidth(text) - 2 + (float)((fontRenderer.getWidth(text) + 7 ) * (1 - animation.get()));
		float textY = yAdd + 2;
		RenderUtil.preRender();
		RenderUtil.drawRect(matrices, textX - 2, textY - 2, fontRenderer.getWidth(text) + 4, fontRenderer.fontHeight + 2, bgColor);
		RenderUtil.drawRect(matrices, textX - 5, textY - 2, 3, fontRenderer.fontHeight + 2, barColor);
		RenderUtil.postRender();
		fontRenderer.drawWithShadow(matrices, text, textX, textY, textColor);
		return (float)((fontRenderer.fontHeight + 2) * animation.get());
	}
	
	private Animation getAnimation(int i) {
		while(animations.size() <= i) {
			animations.add(i, Animation.getInOutQuad(0.25));
		}
		return animations.get(i);
	}
}
