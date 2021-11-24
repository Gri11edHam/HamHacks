package net.grilledham.hamhacks.gui;

import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.render.HUD;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

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
		int barC = (int)HUD.getInstance().barColor.getColor();
		int barR = barC >> 16 & 255;
		int barG = barC >> 8 & 255;
		int barB = barC & 255;
		int barA = barC >> 24 & 255;
		float[] barHSB = Color.RGBtoHSB(barR, barG, barB, null);
		int bgC = (int)HUD.getInstance().bgColor.getColor();
		int bgR = bgC >> 16 & 255;
		int bgG = bgC >> 8 & 255;
		int bgB = bgC & 255;
		int bgA = bgC >> 24 & 255;
		float[] bgHSB = Color.RGBtoHSB(bgR, bgG, bgB, null);
		int textC = (int)HUD.getInstance().textColor.getColor();
		int textR = textC >> 16 & 255;
		int textG = textC >> 8 & 255;
		int textB = textC & 255;
		int textA = textC >> 24 & 255;
		float[] textHSB = Color.RGBtoHSB(textR, textG, textB, null);
		
		int i = 0;
		int yAdd = 0;
		if(HUD.getInstance().showLogo.getBool()) {
			float finalBarHue;
			if(HUD.getInstance().barColor.useChroma()) {
				finalBarHue = (barHSB[0] - (i * 0.025f)) % 1f;
			} else {
				finalBarHue = barHSB[0];
			}
			float finalBGHue;
			if(HUD.getInstance().bgColor.useChroma()) {
				finalBGHue = (bgHSB[0] - (i * 0.025f)) % 1f;
			} else {
				finalBGHue = bgHSB[0];
			}
			float finalTextHue;
			if(HUD.getInstance().textColor.useChroma()) {
				finalTextHue = (textHSB[0] - (i * 0.025f)) % 1f;
			} else {
				finalTextHue = textHSB[0];
			}
			int barColor = (Color.HSBtoRGB(finalBarHue, barHSB[1], barHSB[2]) & 0xffffff) + (barA << 24);
			int bgColor = (Color.HSBtoRGB(finalBGHue, bgHSB[1], bgHSB[2]) & 0xffffff) + (bgA << 24);
			int textColor = (Color.HSBtoRGB(finalTextHue, textHSB[1], textHSB[2])) + (textA << 24);
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
		if(HUD.getInstance().showFPS.getBool()) {
			float finalBarHue;
			if(HUD.getInstance().barColor.useChroma()) {
				finalBarHue = (barHSB[0] - (i * 0.025f)) % 1f;
			} else {
				finalBarHue = barHSB[0];
			}
			float finalBGHue;
			if(HUD.getInstance().bgColor.useChroma()) {
				finalBGHue = (bgHSB[0] - (i * 0.025f)) % 1f;
			} else {
				finalBGHue = bgHSB[0];
			}
			float finalTextHue;
			if(HUD.getInstance().textColor.useChroma()) {
				finalTextHue = (textHSB[0] - (i * 0.025f)) % 1f;
			} else {
				finalTextHue = textHSB[0];
			}
			int barColor = (Color.HSBtoRGB(finalBarHue, barHSB[1], barHSB[2]) & 0xffffff) + (barA << 24);
			int bgColor = (Color.HSBtoRGB(finalBGHue, bgHSB[1], bgHSB[2]) & 0xffffff) + (bgA << 24);
			int textColor = (Color.HSBtoRGB(finalTextHue, textHSB[1], textHSB[2])) + (textA << 24);
			int textX = 2;
			int textY = yAdd + 2;
			String fps = MinecraftClient.getInstance().fpsDebugString;
			fps = fps.split(" ")[0] + " " + fps.split(" ")[1];
			DrawableHelper.fill(matrices, textX - 2, textY - 2, textX + fontRenderer.getWidth(fps) + 2, textY + fontRenderer.fontHeight, bgColor);
			DrawableHelper.fill(matrices, textX + fontRenderer.getWidth(fps) + 2,  textY - 2, textX + fontRenderer.getWidth(fps) + 5, textY + fontRenderer.fontHeight, barColor);
			DrawableHelper.drawStringWithShadow(matrices, fontRenderer, fps, textX, textY, textColor);
			yAdd += fontRenderer.fontHeight + 4;
			i++;
		}
		if(HUD.getInstance().showModules.getBool()) {
			int j = 0;
			for(Module m : ModuleManager.getModules().stream().sorted((a, b) -> Integer.compare(MinecraftClient.getInstance().textRenderer.getWidth(b.getName()), MinecraftClient.getInstance().textRenderer.getWidth(a.getName()))).collect(Collectors.toList())) {
				if(m.isEnabled() && m.shouldShowModule()) {
					float finalBarHue;
					if(HUD.getInstance().barColor.useChroma()) {
						finalBarHue = (barHSB[0] - (i * 0.025f)) % 1f;
					} else {
						finalBarHue = barHSB[0];
					}
					float finalBGHue;
					if(HUD.getInstance().bgColor.useChroma()) {
						finalBGHue = (bgHSB[0] - (i * 0.025f)) % 1f;
					} else {
						finalBGHue = bgHSB[0];
					}
					float finalTextHue;
					if(HUD.getInstance().textColor.useChroma()) {
						finalTextHue = (textHSB[0] - (i * 0.025f)) % 1f;
					} else {
						finalTextHue = textHSB[0];
					}
					int barColor = (Color.HSBtoRGB(finalBarHue, barHSB[1], barHSB[2]) & 0xffffff) + (barA << 24);
					int bgColor = (Color.HSBtoRGB(finalBGHue, bgHSB[1], bgHSB[2]) & 0xffffff) + (bgA << 24);
					int textColor = (Color.HSBtoRGB(finalTextHue, textHSB[1], textHSB[2])) + (textA << 24);
					int textX = MinecraftClient.getInstance().getWindow().getScaledWidth() - fontRenderer.getWidth(m.getName()) - 2;
					int textY = (fontRenderer.fontHeight + 2) * j + 2;
					DrawableHelper.fill(matrices, textX - 2, textY - 2, textX + fontRenderer.getWidth(m.getName()) + 2, textY + fontRenderer.fontHeight, bgColor);
					DrawableHelper.fill(matrices, textX - 5, textY - 2, textX - 2, textY + fontRenderer.fontHeight, barColor);
					DrawableHelper.drawStringWithShadow(matrices, fontRenderer, m.getName(), textX, textY, textColor);
					i++;
					j++;
				}
			}
		}
	}
}
