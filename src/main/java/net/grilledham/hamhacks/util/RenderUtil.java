package net.grilledham.hamhacks.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.grilledham.hamhacks.font.CustomTextRenderer;
import net.grilledham.hamhacks.font.FontFamily;
import net.grilledham.hamhacks.font.FontInfo;
import net.grilledham.hamhacks.font.FontManager;
import net.grilledham.hamhacks.mixininterface.IDrawContext;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.text.Text;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class RenderUtil {
	
	private static final MinecraftClient mc = MinecraftClient.getInstance();
	
	// 0 - x, 1 - y, 2 - width, 3 - height, 4 - scale
	private static final List<Float[]> scissorStack = new ArrayList<>();
	private static float SCISSOR_TRANSLATION_X = 0;
	private static float SCISSOR_TRANSLATION_Y = 0;
	
	private static int zLevel = 0;
	
	public static CustomTextRenderer customTextRenderer;
	
	private RenderUtil() {}
	
	public static void translateScissor(float x, float y) {
		SCISSOR_TRANSLATION_X += x;
		SCISSOR_TRANSLATION_Y += y;
	}
	
	public static void pushScissor(float x, float y, float width, float height, float scale) {
		scissorStack.add(0, new Float[] {x + SCISSOR_TRANSLATION_X, y + SCISSOR_TRANSLATION_Y, width, height, scale});
		applyScissor();
	}
	
	public static void adjustScissor(float x, float y, float width, float height, float scale) {
		if(!scissorStack.isEmpty()) {
			x = Math.max(x, scissorStack.get(0)[0]);
			y = Math.max(y, scissorStack.get(0)[1]);
			float x1 = Math.min(x + width, scissorStack.get(0)[0] + scissorStack.get(0)[2]);
			float y1 = Math.min(y + height, scissorStack.get(0)[1] + scissorStack.get(0)[3]);
			width = x1 - x;
			height = y1 - y;
		}
		scissorStack.add(0, new Float[] {x + SCISSOR_TRANSLATION_X, y + SCISSOR_TRANSLATION_Y, width, height, scale});
		applyScissor();
	}
	
	public static void popScissor() {
		scissorStack.remove(0);
		RenderSystem.disableScissor();
		if(!scissorStack.isEmpty()) {
			applyScissor();
		}
	}
	
	private static void applyScissor() {
		float scaleFactor = scissorStack.get(0)[4];
		int x = (int)Math.floor(scissorStack.get(0)[0] * scaleFactor);
		int y = (int)Math.floor(scissorStack.get(0)[1] * scaleFactor);
		int w = Math.max((int)Math.ceil(scissorStack.get(0)[2] * scaleFactor), 0);
		int h = Math.max((int)Math.ceil(scissorStack.get(0)[3] * scaleFactor), 0);
		RenderSystem.enableScissor(x, mc.getWindow().getHeight() - y - h, w, h);
	}
	
	public static int mix(int c1, int c2, double by) {
		by = Math.min(1, Math.max(0, by));
		int r1 = (c1 >> 16) & 255;
		int g1 = (c1 >> 8) & 255;
		int b1 = (c1) & 255;
		int a1 = (c1 >> 24) & 255;
		int r2 = (c2 >> 16) & 255;
		int g2 = (c2 >> 8) & 255;
		int b2 = (c2) & 255;
		int a2 = (c2 >> 24) & 255;
		int r = (int)(r1 * by + r2 * (1 - by));
		int g = (int)(g1 * by + g2 * (1 - by));
		int b = (int)(b1 * by + b2 * (1 - by));
		int a = (int)(a1 * by + a2 * (1 - by));
		return (a << 24) + (r << 16) + (g << 8) + b;
	}
	
	public static void setZLevel(int zLevel) {
		RenderUtil.zLevel = zLevel;
	}
	
	public static void resetZLevel() {
		zLevel = 0;
	}
	
	public static void drawRect(MatrixStack stack, float x, float y, float w, float h, int c) {
		Matrix4f mat = stack.peek().getPositionMatrix();
		
		BufferBuilder buf = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		
		buf.vertex(mat, x + w, y, zLevel).color(c);
		buf.vertex(mat, x, y, zLevel).color(c);
		buf.vertex(mat, x, y + h, zLevel).color(c);
		buf.vertex(mat, x + w, y + h, zLevel).color(c);
		
		BufferRenderer.drawWithGlobalProgram(buf.end());
	}
	
	public static void drawHRect(MatrixStack stack, float x, float y, float w, float h, int c) {
		Matrix4f mat = stack.peek().getPositionMatrix();
		
		// Top
		BufferBuilder buf = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		
		buf.vertex(mat, x + w, y, zLevel).color(c);
		buf.vertex(mat, x, y, zLevel).color(c);
		buf.vertex(mat, x, y + 1, zLevel).color(c);
		buf.vertex(mat, x + w, y + 1, zLevel).color(c);
		
		BufferRenderer.drawWithGlobalProgram(buf.end());
		
		// Left
		buf = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		
		buf.vertex(mat, x + 1, y + 1, zLevel).color(c);
		buf.vertex(mat, x, y + 1, zLevel).color(c);
		buf.vertex(mat, x, y + h - 1, zLevel).color(c);
		buf.vertex(mat, x + 1, y + h - 1, zLevel).color(c);
		
		BufferRenderer.drawWithGlobalProgram(buf.end());
		
		// Bottom
		buf = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		
		buf.vertex(mat, x + w, y + h - 1, zLevel).color(c);
		buf.vertex(mat, x, y + h - 1, zLevel).color(c);
		buf.vertex(mat, x, y + h, zLevel).color(c);
		buf.vertex(mat, x + w, y + h, zLevel).color(c);
		
		BufferRenderer.drawWithGlobalProgram(buf.end());
		
		// Right
		buf = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		
		buf.vertex(mat, x + w, y, zLevel).color(c);
		buf.vertex(mat, x + w - 1, y, zLevel).color(c);
		buf.vertex(mat, x + w - 1, y + h - 1, zLevel).color(c);
		buf.vertex(mat, x + w, y + h - 1, zLevel).color(c);
		
		BufferRenderer.drawWithGlobalProgram(buf.end());
	}
	
	public static void drawSBGradient(MatrixStack stack, float x, float y, float width, float height, float hue) {
		Matrix4f mat = stack.peek().getPositionMatrix();
		BufferBuilder buf = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		int tr = Color.toRGB(hue, 1, 1, 1);
		float atr = (float)(tr >> 24 & 255) / 255.0F;
		float rtr = (float)(tr >> 16 & 255) / 255.0F;
		float gtr = (float)(tr >> 8 & 255) / 255.0F;
		float btr = (float)(tr & 255) / 255.0F;
		int br = Color.toRGB(hue, 1, 0, 1);
		float abr = (float)(br >> 24 & 255) / 255.0F;
		float rbr = (float)(br >> 16 & 255) / 255.0F;
		float gbr = (float)(br >> 8 & 255) / 255.0F;
		float bbr = (float)(br & 255) / 255.0F;
		int tl = Color.toRGB(hue, 0, 1, 1);
		float atl = (float)(tl >> 24 & 255) / 255.0F;
		float rtl = (float)(tl >> 16 & 255) / 255.0F;
		float gtl = (float)(tl >> 8 & 255) / 255.0F;
		float btl = (float)(tl & 255) / 255.0F;
		int bl = Color.toRGB(hue, 0, 0, 1);
		float abl = (float)(bl >> 24 & 255) / 255.0F;
		float rbl = (float)(bl >> 16 & 255) / 255.0F;
		float gbl = (float)(bl >> 8 & 255) / 255.0F;
		float bbl = (float)(bl & 255) / 255.0F;
		buf.vertex(mat, x + width, y, zLevel).color(rtr, gtr, btr, atr);
		buf.vertex(mat, x, y, zLevel).color(rtl, gtl, btl, atl);
		buf.vertex(mat, x, y + height, zLevel).color(rbl, gbl, bbl, abl);
		buf.vertex(mat, x + width, y + height, zLevel).color(rbr, gbr, bbr, abr);
		BufferRenderer.drawWithGlobalProgram(buf.end());
	}
	
	public static void drawHueGradient(MatrixStack stack, float x, float y, float width, float height) {
		Matrix4f mat = stack.peek().getPositionMatrix();
		BufferBuilder buf = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		int startC = Color.toRGB(0, 1, 1, 1);
		float endY = y + (height / 6f);
		int endC;
		for(int i = 0; i < 6; i++) {
			switch(i) {
				case 0 -> endC = Color.toRGB(1 / 6f, 1, 1, 1);
				case 1 -> endC = Color.toRGB(2 / 6f, 1, 1, 1);
				case 2 -> endC = Color.toRGB(3 / 6f, 1, 1, 1);
				case 3 -> endC = Color.toRGB(4 / 6f, 1, 1, 1);
				case 4 -> endC = Color.toRGB(5 / 6f, 1, 1, 1);
				case 5 -> endC = Color.toRGB(6 / 6f, 1, 1, 1);
				default -> endC = Color.toRGB(1, 1, 1, 1);
			}
			float sa = (float)(startC >> 24 & 255) / 255.0F;
			float sr = (float)(startC >> 16 & 255) / 255.0F;
			float sg = (float)(startC >> 8 & 255) / 255.0F;
			float sb = (float)(startC & 255) / 255.0F;
			float ea = (float)(endC >> 24 & 255) / 255.0F;
			float er = (float)(endC >> 16 & 255) / 255.0F;
			float eg = (float)(endC >> 8 & 255) / 255.0F;
			float eb = (float)(endC & 255) / 255.0F;
			buf.vertex(mat, x + width, y, zLevel).color(sr, sg, sb, sa);
			buf.vertex(mat, x, y, zLevel).color(sr, sg, sb, sa);
			buf.vertex(mat, x, endY, zLevel).color(er, eg, eb, ea);
			buf.vertex(mat, x + width, endY, zLevel).color(er, eg, eb, ea);
			y = endY;
			endY = y + (height / 6f);
			startC = endC;
		}
		BufferRenderer.drawWithGlobalProgram(buf.end());
	}
	
	public static void drawAlphaGradient(MatrixStack stack, float x, float y, float width, float height) {
		Matrix4f mat = stack.peek().getPositionMatrix();
		BufferBuilder buf = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		int startC = 0xffffffff;
		int endC = 0x00ffffff;
		float sa = (float)(startC >> 24 & 255) / 255.0F;
		float sr = (float)(startC >> 16 & 255) / 255.0F;
		float sg = (float)(startC >> 8 & 255) / 255.0F;
		float sb = (float)(startC & 255) / 255.0F;
		float ea = (float)(endC >> 24 & 255) / 255.0F;
		float er = (float)(endC >> 16 & 255) / 255.0F;
		float eg = (float)(endC >> 8 & 255) / 255.0F;
		float eb = (float)(endC & 255) / 255.0F;
		buf.vertex(mat, x + width, y, zLevel).color(sr, sg, sb, sa);
		buf.vertex(mat, x, y, zLevel).color(sr, sg, sb, sa);
		buf.vertex(mat, x, y + height, zLevel).color(er, eg, eb, ea);
		buf.vertex(mat, x + width, y + height, zLevel).color(er, eg, eb, ea);
		BufferRenderer.drawWithGlobalProgram(buf.end());
	}
	
	public static void preRender() {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}
	
	public static void postRender() {
		RenderSystem.disableBlend();
	}
	
	public static void drawToolTip(DrawContext ctx, String title, String tooltip, double mx, double my, double scale) {
		MatrixStack stack = ctx.getMatrices();
		stack.push();
		stack.translate(0, 0, 200);
		String[] lines = tooltip.split("\n");
		
		float w = RenderUtil.getStringWidth(Arrays.stream(lines).sorted(Comparator.comparingDouble(s -> RenderUtil.getStringWidth((String)s)).reversed()).toList().get(0)) + 8;
		float h = (lines.length + (!title.equals("") ? 1 : 0)) * (RenderUtil.getFontHeight() + 2) + 8;
		float x = (float)(mx - 4);
		float y = (float)(my - 2 - h);
		
		boolean shift = false;
		if(y < 0) {
			y = (float)(my + 4);
			x = (float)(mx + 4);
			shift = true;
		}
		if(x + w > mc.getWindow().getScaledWidth()) {
			x -= w - (shift ? 4 : 8);
		}
		
		pushScissor(x, y, (float)(w * scale), (float)(h * scale), (float)scale);
		
		preRender();
		
		drawRect(stack, x, y, w, h, 0x80000000);
		drawHRect(stack, x, y, w, h, 0xff4040d0);
		
		postRender();
		
		float yAdd = 0;
		if(!title.equals("")) {
			drawString(ctx, title, x + 4, y + 4 + yAdd, 0xffffff20, true);
			yAdd += RenderUtil.getFontHeight() + 2;
		}
		for(String s : lines) {
			drawString(ctx, s, x + 4, y + 4 + yAdd, -1, true);
			yAdd += RenderUtil.getFontHeight() + 2;
		}
		
		popScissor();
		stack.pop();
	}
	
	public static void drawString(DrawContext ctx, String s, float x, float y, int color, boolean shadow) {
		if(PageManager.getPage(ClickGUI.class).font.get() != 0 && customTextRenderer != null) {
			customTextRenderer.render(ctx, s, x, y, color, shadow);
		} else {
			mc.textRenderer.draw(s, x, y, color, shadow, ctx.getMatrices().peek().getPositionMatrix(), ((IDrawContext)ctx).hamHacks$getVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 15728880);
			ctx.draw();
		}
	}
	
	public static void drawString(DrawContext ctx, Text t, float x, float y, int color, boolean shadow) {
		if(PageManager.getPage(ClickGUI.class).font.get() != 0 && customTextRenderer != null) {
			customTextRenderer.render(ctx, t, x, y, color, shadow);
		} else {
			mc.textRenderer.draw(t, x, y, color, shadow, ctx.getMatrices().peek().getPositionMatrix(), ((IDrawContext)ctx).hamHacks$getVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 15728880);
			ctx.draw();
		}
	}
	
	public static float getFontHeight() {
		if(PageManager.getPage(ClickGUI.class).font.get() != 0 && customTextRenderer != null) return customTextRenderer.getHeight();
		return mc.textRenderer.fontHeight;
	}
	
	public static float getStringWidth(String s) {
		if(PageManager.getPage(ClickGUI.class).font.get() != 0 && customTextRenderer != null) return customTextRenderer.getWidth(s);
		return mc.textRenderer.getWidth(s);
	}
	
	public static void drawItem(DrawContext ctx, ItemStack itemStack, float x, float y, float scale, boolean count, boolean damage) {
		MatrixStack matrices = ctx.getMatrices();
		matrices.push();
		matrices.translate(x + 8, y + 8, zLevel);
		matrices.scale(16, -16, 16);
		matrices.scale(scale, scale, 1);
		
		mc.getBufferBuilders().getEntityVertexConsumers().draw();
		
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		
		matrices.peek().getNormalMatrix().set(1, 1, 1);
		
		mc.getItemRenderer().renderItem(itemStack, ModelTransformationMode.GUI, 0xF000F0,
				OverlayTexture.DEFAULT_UV, matrices, mc.getBufferBuilders().getEntityVertexConsumers(), mc.world, 0);
		
		mc.getBufferBuilders().getEntityVertexConsumers().draw();
		
		RenderSystem.disableBlend();
		
		matrices.pop();
		
		if(damage && itemStack.isDamaged()) {
			matrices.push();
			matrices.translate(x + 8, y + 8, zLevel + 100);
			matrices.scale(scale, scale, 1);
			RenderSystem.disableBlend();
			int i = itemStack.getItemBarStep();
			int j = itemStack.getItemBarColor();
			renderGuiQuad(matrices.peek().getPositionMatrix(), -6, 5, 13, 2, 0, 0, 0, 255);
			renderGuiQuad(matrices.peek().getPositionMatrix(), -6, 5, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
			matrices.pop();
		}
		
		if(count && itemStack.getCount() > 1) {
			matrices.push();
			matrices.translate(x + 8, y + 8, zLevel + 200);
			matrices.scale(scale, scale, 1);
			drawString(ctx, String.valueOf(itemStack.getCount()), 9 - RenderUtil.getStringWidth(String.valueOf(itemStack.getCount())), 1, -1, true);
			matrices.pop();
		}
	}
	
	private static void renderGuiQuad(Matrix4f mat, float x, float y, float width, float height, int red, int green, int blue, int alpha) {
		RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
		BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		buffer.vertex(mat, x, y, 0.0F).color(red, green, blue, alpha);
		buffer.vertex(mat, x, y + height, 0.0F).color(red, green, blue, alpha);
		buffer.vertex(mat, x + width, y + height, 0.0F).color(red, green, blue, alpha);
		buffer.vertex(mat, x + width, y, 0.0F).color(red, green, blue, alpha);
		BufferRenderer.drawWithGlobalProgram(buffer.end());
	}
	
	public static void updateFont(int option) {
		if(option == 0) return;
		FontFamily family = FontManager.FONT_FAMILIES.get(option - 1);
		if(family.get(FontInfo.Type.Regular) == null) {
			PageManager.getPage(ClickGUI.class).font.set(0);
			return;
		}
		customTextRenderer = new CustomTextRenderer(family, 9);
	}
	
	public static void reloadFont() {
		customTextRenderer.reload();
	}
}
