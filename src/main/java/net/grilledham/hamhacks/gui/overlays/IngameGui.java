package net.grilledham.hamhacks.gui.overlays;

import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.render.HUD;
import net.grilledham.hamhacks.util.ConnectionUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.stream.Collectors;

public class IngameGui {
	
	private static IngameGui INSTANCE;
	
	public static void register() {
		INSTANCE = new IngameGui();
	}
	
	public static IngameGui getInstance() {
		return INSTANCE;
	}
	
	public void render(MatrixStack matrices, float tickDelta, TextRenderer fontRenderer) {
		if(!HUD.getInstance().isEnabled()) {
			return;
		}
		Float[] barC = HUD.getInstance().accentColor.getValue();
		Float[] bgC = HUD.getInstance().bgColor.getValue();
		Float[] textC = HUD.getInstance().textColor.getValue();
		
		int i = 0;
		int yAdd = 0;
		if(HUD.getInstance().showLogo.getValue()) {
			float finalBarHue;
			if(HUD.getInstance().accentColor.useChroma()) {
				finalBarHue = (barC[0] - (i * 0.025f)) % 1f;
			} else {
				finalBarHue = barC[0];
			}
			float finalBGHue;
			if(HUD.getInstance().bgColor.useChroma()) {
				finalBGHue = (bgC[0] - (i * 0.025f)) % 1f;
			} else {
				finalBGHue = bgC[0];
			}
			float finalTextHue;
			if(HUD.getInstance().textColor.useChroma()) {
				finalTextHue = (textC[0] - (i * 0.025f)) % 1f;
			} else {
				finalTextHue = textC[0];
			}
			int barColor = (int)((Color.HSBtoRGB(finalBarHue, barC[1], barC[2]) & 0xffffff) + (barC[3] * 255));
			int bgColor = (int)((Color.HSBtoRGB(finalBGHue, bgC[1], bgC[2]) & 0xffffff) + (bgC[3] * 255));
			int textColor = (int)((Color.HSBtoRGB(finalTextHue, textC[1], textC[2])) + (textC[3] * 255));
			int textX = 2;
			int textY = (fontRenderer.fontHeight + 2) * i + 2;
			String text = "§4§o§lHamHacks";
//			DrawableHelper.fill(matrices, textX - 2, textY - 2, textX + fontRenderer.getWidth(text) + 2, textY + fontRenderer.fontHeight, bgColor);
//			DrawableHelper.fill(matrices, textX + fontRenderer.getWidth(text) + 2,  textX + fontRenderer.getWidth(MinecraftClient.getInstance().fpsDebugString) + 5, textX - 2, textY + fontRenderer.fontHeight, barColor);
			matrices.push();
			matrices.translate(textX, textY, 0);
			matrices.scale(2, 2, 1);
			matrices.translate(-textX, -textY, 0);
			DrawableHelper.drawStringWithShadow(matrices, fontRenderer, text, textX, textY, textColor);
			matrices.pop();
			yAdd += (fontRenderer.fontHeight * 2) + 4;
			i++;
		}
		if(HUD.getInstance().showFPS.getValue()) {
			String fps = MinecraftClient.getInstance().fpsDebugString;
			fps = fps.split(" ")[0] + " " + fps.split(" ")[1];
			yAdd += drawLeftAligned(matrices, tickDelta, fontRenderer, fps, i, yAdd);
			i++;
		}
		if(HUD.getInstance().showPing.getValue()) {
			String ping = "0 ms";
			if(MinecraftClient.getInstance().player != null) {
				PlayerListEntry playerListEntry = MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry(MinecraftClient.getInstance().player.getUuid());
				if(playerListEntry != null) {
					int latency = playerListEntry.getLatency();
					if(MinecraftClient.getInstance().getCurrentServerEntry() != null) {
						ping = (latency < 0 ? MinecraftClient.getInstance().getCurrentServerEntry().ping : latency) + " ms";
					}
				}
			}
			yAdd += drawLeftAligned(matrices, tickDelta, fontRenderer, ping, i, yAdd);
			i++;
		}
		if(HUD.getInstance().showTPS.getValue()) {
			String tps = String.format("%.2f tps", ConnectionUtil.getTPS());
			yAdd += drawLeftAligned(matrices, tickDelta, fontRenderer, tps, i, yAdd);
			i++;
		}
		if(HUD.getInstance().showTimeSinceLastTick.getValue()) {
			float timeSinceLastTick = ConnectionUtil.getTimeSinceLastTick() / 1000f;
			if(timeSinceLastTick >= 1) {
				String timeSinceLastTickString = String.format("Seconds Since Last Tick: %.2f", timeSinceLastTick);
				yAdd += drawLeftAligned(matrices, tickDelta, fontRenderer, timeSinceLastTickString, i, yAdd);
				i++;
			}
		}
		if(HUD.getInstance().showModules.getValue()) {
			yAdd = 0;
			for(Module m : ModuleManager.getModules().stream().sorted((a, b) -> Integer.compare(MinecraftClient.getInstance().textRenderer.getWidth(b.getName()), MinecraftClient.getInstance().textRenderer.getWidth(a.getName()))).collect(Collectors.toList())) {
				if(m.isEnabled() && m.shouldShowModule()) {
					yAdd += drawRightAligned(matrices, tickDelta, fontRenderer, m.getName(), i, yAdd);
					i++;
				}
			}
		}
	}
	
	private int drawLeftAligned(MatrixStack matrices, float tickDelta, TextRenderer fontRenderer, String text, int i, int yAdd) {
		Float[] barC = HUD.getInstance().accentColor.getValue();
		Float[] bgC = HUD.getInstance().bgColor.getValue();
		Float[] textC = HUD.getInstance().textColor.getValue();
		float finalBarHue;
		if(HUD.getInstance().accentColor.useChroma()) {
			finalBarHue = (barC[0] - (i * 0.025f)) % 1f;
		} else {
			finalBarHue = barC[0];
		}
		float finalBGHue;
		if(HUD.getInstance().bgColor.useChroma()) {
			finalBGHue = (bgC[0] - (i * 0.025f)) % 1f;
		} else {
			finalBGHue = bgC[0];
		}
		float finalTextHue;
		if(HUD.getInstance().textColor.useChroma()) {
			finalTextHue = (textC[0] - (i * 0.025f)) % 1f;
		} else {
			finalTextHue = textC[0];
		}
		int barColor = (Color.HSBtoRGB(finalBarHue, barC[1], barC[2]) & 0xffffff) + ((int)(barC[3] * 255) << 24);
		int bgColor = (Color.HSBtoRGB(finalBGHue, bgC[1], bgC[2]) & 0xffffff) + ((int)(bgC[3] * 255) << 24);
		int textColor = (Color.HSBtoRGB(finalTextHue, textC[1], textC[2])) + ((int)(textC[3] * 255) << 24);
		int textX = 2;
		int textY = yAdd + 2;
		DrawableHelper.fill(matrices, textX - 2, textY - 2, textX + fontRenderer.getWidth(text) + 2, textY + fontRenderer.fontHeight, bgColor);
		DrawableHelper.fill(matrices, textX + fontRenderer.getWidth(text) + 2,  textY - 2, textX + fontRenderer.getWidth(text) + 5, textY + fontRenderer.fontHeight, barColor);
		DrawableHelper.drawStringWithShadow(matrices, fontRenderer, text, textX, textY, textColor);
		return fontRenderer.fontHeight + 2;
	}
	
	private int drawRightAligned(MatrixStack matrices, float tickDelta, TextRenderer fontRenderer, String text, int i, int yAdd) {
		Float[] barC = HUD.getInstance().accentColor.getValue();
		Float[] bgC = HUD.getInstance().bgColor.getValue();
		Float[] textC = HUD.getInstance().textColor.getValue();
		float finalBarHue;
		if(HUD.getInstance().accentColor.useChroma()) {
			finalBarHue = (barC[0] - (i * 0.025f)) % 1f;
		} else {
			finalBarHue = barC[0];
		}
		float finalBGHue;
		if(HUD.getInstance().bgColor.useChroma()) {
			finalBGHue = (bgC[0] - (i * 0.025f)) % 1f;
		} else {
			finalBGHue = bgC[0];
		}
		float finalTextHue;
		if(HUD.getInstance().textColor.useChroma()) {
			finalTextHue = (textC[0] - (i * 0.025f)) % 1f;
		} else {
			finalTextHue = textC[0];
		}
		int barColor = (Color.HSBtoRGB(finalBarHue, barC[1], barC[2]) & 0xffffff) + ((int)(barC[3] * 255) << 24);
		int bgColor = (Color.HSBtoRGB(finalBGHue, bgC[1], bgC[2]) & 0xffffff) + ((int)(bgC[3] * 255) << 24);
		int textColor = (Color.HSBtoRGB(finalTextHue, textC[1], textC[2])) + ((int)(textC[3] * 255) << 24);
		int textX = MinecraftClient.getInstance().getWindow().getScaledWidth() - fontRenderer.getWidth(text) - 2;
		int textY = yAdd + 2;
		DrawableHelper.fill(matrices, textX - 2, textY - 2, textX + fontRenderer.getWidth(text) + 2, textY + fontRenderer.fontHeight, bgColor);
		DrawableHelper.fill(matrices, textX - 5, textY - 2, textX - 2, textY + fontRenderer.fontHeight, barColor);
		DrawableHelper.drawStringWithShadow(matrices, fontRenderer, text, textX, textY, textColor);
		return fontRenderer.fontHeight + 2;
	}
}
