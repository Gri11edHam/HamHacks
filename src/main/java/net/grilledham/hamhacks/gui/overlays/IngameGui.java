package net.grilledham.hamhacks.gui.overlays;

import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.render.HUD;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
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
		Float[] barC = HUD.getInstance().barColor.getValue();
		Float[] bgC = HUD.getInstance().bgColor.getValue();
		Float[] textC = HUD.getInstance().textColor.getValue();
		
		int i = 0;
		int yAdd = 0;
		if(HUD.getInstance().showLogo.getValue()) {
			float finalBarHue;
			if(HUD.getInstance().barColor.useChroma()) {
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
			float finalBarHue;
			if(HUD.getInstance().barColor.useChroma()) {
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
			String fps = MinecraftClient.getInstance().fpsDebugString;
			fps = fps.split(" ")[0] + " " + fps.split(" ")[1];
			DrawableHelper.fill(matrices, textX - 2, textY - 2, textX + fontRenderer.getWidth(fps) + 2, textY + fontRenderer.fontHeight, bgColor);
			DrawableHelper.fill(matrices, textX + fontRenderer.getWidth(fps) + 2,  textY - 2, textX + fontRenderer.getWidth(fps) + 5, textY + fontRenderer.fontHeight, barColor);
			DrawableHelper.drawStringWithShadow(matrices, fontRenderer, fps, textX, textY, textColor);
			yAdd += fontRenderer.fontHeight + 4;
			i++;
		}
		if(HUD.getInstance().showModules.getValue()) {
			int j = 0;
			for(Module m : ModuleManager.getModules().stream().sorted((a, b) -> Integer.compare(MinecraftClient.getInstance().textRenderer.getWidth(b.getName()), MinecraftClient.getInstance().textRenderer.getWidth(a.getName()))).collect(Collectors.toList())) {
				if(m.isEnabled() && m.shouldShowModule()) {
					float finalBarHue;
					if(HUD.getInstance().barColor.useChroma()) {
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