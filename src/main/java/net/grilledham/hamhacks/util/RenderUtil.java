package net.grilledham.hamhacks.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RenderUtil {
	
	private static final MinecraftClient mc = MinecraftClient.getInstance();
	
	// 0 - x, 1 - y, 2 - width, 3 - height, 4 - scale
	private static final List<Float[]> scissorStack = new ArrayList<>();
	
	private static int zLevel = 0;
	
	private RenderUtil() {}
	
	public static void pushScissor(float x, float y, float width, float height, float scale) {
		scissorStack.add(0, new Float[] {x, y, width, height, scale});
	}
	
	public static void popScissor() {
		scissorStack.remove(0);
		if(scissorStack.isEmpty()) {
			RenderSystem.disableScissor();
		}
	}
	
	public static void applyScissor() {
		float scaleFactor = scissorStack.get(0)[4];
		int x = (int)(scissorStack.get(0)[0] * scaleFactor);
		int y = (int)(scissorStack.get(0)[1] * scaleFactor);
		int w = (int)(scissorStack.get(0)[2] * scaleFactor);
		int h = (int)(scissorStack.get(0)[3] * scaleFactor);
		RenderSystem.enableScissor(x, mc.getWindow().getHeight() - y - h, w, h);
	}
	
	public static int mix(int c1, int c2, float by) {
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
		BufferBuilder buf = Tessellator.getInstance().getBuffer();
		
		Matrix4f mat = stack.peek().getPositionMatrix();
		
		buf.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		
		buf.vertex(mat, x + w, y, zLevel).color(c).next();
		buf.vertex(mat, x, y, zLevel).color(c).next();
		buf.vertex(mat, x, y + h, zLevel).color(c).next();
		buf.vertex(mat, x + w, y + h, zLevel).color(c).next();
		
		buf.end();
		
		BufferRenderer.draw(buf);
	}
	
	public static void drawHRect(MatrixStack stack, float x, float y, float w, float h, int c) {
		BufferBuilder buf = Tessellator.getInstance().getBuffer();
		
		Matrix4f mat = stack.peek().getPositionMatrix();
		
		// Top
		buf.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		
		buf.vertex(mat, x + w, y, zLevel).color(c).next();
		buf.vertex(mat, x, y, zLevel).color(c).next();
		buf.vertex(mat, x, y + 1, zLevel).color(c).next();
		buf.vertex(mat, x + w, y + 1, zLevel).color(c).next();
		
		buf.end();
		// Left
		BufferRenderer.draw(buf);
		
		buf.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		
		buf.vertex(mat, x + 1, y + 1, zLevel).color(c).next();
		buf.vertex(mat, x, y + 1, zLevel).color(c).next();
		buf.vertex(mat, x, y + h - 1, zLevel).color(c).next();
		buf.vertex(mat, x + 1, y + h - 1, zLevel).color(c).next();
		
		buf.end();
		// Bottom
		BufferRenderer.draw(buf);
		
		buf.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		
		buf.vertex(mat, x + w, y + h - 1, zLevel).color(c).next();
		buf.vertex(mat, x, y + h - 1, zLevel).color(c).next();
		buf.vertex(mat, x, y + h, zLevel).color(c).next();
		buf.vertex(mat, x + w, y + h, zLevel).color(c).next();
		
		buf.end();
		// Right
		BufferRenderer.draw(buf);
		
		buf.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		
		buf.vertex(mat, x + w, y, zLevel).color(c).next();
		buf.vertex(mat, x + w - 1, y, zLevel).color(c).next();
		buf.vertex(mat, x + w - 1, y + h - 1, zLevel).color(c).next();
		buf.vertex(mat, x + w, y + h - 1, zLevel).color(c).next();
		
		buf.end();
		
		BufferRenderer.draw(buf);
	}
	
	public static void drawSBGradient(MatrixStack stack, float x, float y, float width, float height, float hue) {
		BufferBuilder buf = Tessellator.getInstance().getBuffer();
		Matrix4f mat = stack.peek().getPositionMatrix();
		buf.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		int tr = Color.HSBtoRGB(hue, 1, 1);
		float atr = (float)(tr >> 24 & 255) / 255.0F;
		float rtr = (float)(tr >> 16 & 255) / 255.0F;
		float gtr = (float)(tr >> 8 & 255) / 255.0F;
		float btr = (float)(tr & 255) / 255.0F;
		int br = Color.HSBtoRGB(hue, 1, 0);
		float abr = (float)(br >> 24 & 255) / 255.0F;
		float rbr = (float)(br >> 16 & 255) / 255.0F;
		float gbr = (float)(br >> 8 & 255) / 255.0F;
		float bbr = (float)(br & 255) / 255.0F;
		int tl = Color.HSBtoRGB(hue, 0, 1);
		float atl = (float)(tl >> 24 & 255) / 255.0F;
		float rtl = (float)(tl >> 16 & 255) / 255.0F;
		float gtl = (float)(tl >> 8 & 255) / 255.0F;
		float btl = (float)(tl & 255) / 255.0F;
		int bl = Color.HSBtoRGB(hue, 0, 0);
		float abl = (float)(bl >> 24 & 255) / 255.0F;
		float rbl = (float)(bl >> 16 & 255) / 255.0F;
		float gbl = (float)(bl >> 8 & 255) / 255.0F;
		float bbl = (float)(bl & 255) / 255.0F;
		buf.vertex(mat, x + width, y, zLevel).color(rtr, gtr, btr, atr).next();
		buf.vertex(mat, x, y, zLevel).color(rtl, gtl, btl, atl).next();
		buf.vertex(mat, x, y + height, zLevel).color(rbl, gbl, bbl, abl).next();
		buf.vertex(mat, x + width, y + height, zLevel).color(rbr, gbr, bbr, abr).next();
		buf.end();
		BufferRenderer.draw(buf);
	}
	
	public static void drawHueGradient(MatrixStack stack, float x, float y, float width, float height) {
		BufferBuilder buf = Tessellator.getInstance().getBuffer();
		Matrix4f mat = stack.peek().getPositionMatrix();
		buf.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		int startC = Color.HSBtoRGB(0, 1, 1);
		float endY = y + (height / 6f);
		int endC;
		for(int i = 0; i < 6; i++) {
			switch(i) {
				case 0 -> endC = Color.HSBtoRGB(1 / 6f, 1, 1);
				case 1 -> endC = Color.HSBtoRGB(2 / 6f, 1, 1);
				case 2 -> endC = Color.HSBtoRGB(3 / 6f, 1, 1);
				case 3 -> endC = Color.HSBtoRGB(4 / 6f, 1, 1);
				case 4 -> endC = Color.HSBtoRGB(5 / 6f, 1, 1);
				case 5 -> endC = Color.HSBtoRGB(6 / 6f, 1, 1);
				default -> endC = Color.HSBtoRGB(1, 1, 1);
			}
			float sa = (float)(startC >> 24 & 255) / 255.0F;
			float sr = (float)(startC >> 16 & 255) / 255.0F;
			float sg = (float)(startC >> 8 & 255) / 255.0F;
			float sb = (float)(startC & 255) / 255.0F;
			float ea = (float)(endC >> 24 & 255) / 255.0F;
			float er = (float)(endC >> 16 & 255) / 255.0F;
			float eg = (float)(endC >> 8 & 255) / 255.0F;
			float eb = (float)(endC & 255) / 255.0F;
			buf.vertex(mat, x + width, y, zLevel).color(sr, sg, sb, sa).next();
			buf.vertex(mat, x, y, zLevel).color(sr, sg, sb, sa).next();
			buf.vertex(mat, x, endY, zLevel).color(er, eg, eb, ea).next();
			buf.vertex(mat, x + width, endY, zLevel).color(er, eg, eb, ea).next();
			y = endY;
			endY = y + (height / 6f);
			startC = endC;
		}
		buf.end();
		BufferRenderer.draw(buf);
	}
	
	public static void drawAlphaGradient(MatrixStack stack, float x, float y, float width, float height) {
		BufferBuilder buf = Tessellator.getInstance().getBuffer();
		Matrix4f mat = stack.peek().getPositionMatrix();
		buf.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
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
		buf.vertex(mat, x + width, y, zLevel).color(sr, sg, sb, sa).next();
		buf.vertex(mat, x, y, zLevel).color(sr, sg, sb, sa).next();
		buf.vertex(mat, x, y + height, zLevel).color(er, eg, eb, ea).next();
		buf.vertex(mat, x + width, y + height, zLevel).color(er, eg, eb, ea).next();
		buf.end();
		BufferRenderer.draw(buf);
	}
	
	public static void preRender() {
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}
	
	public static void postRender() {
		RenderSystem.disableBlend();
		RenderSystem.enableTexture();
	}
}
