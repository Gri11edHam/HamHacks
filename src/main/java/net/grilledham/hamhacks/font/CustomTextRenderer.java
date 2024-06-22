package net.grilledham.hamhacks.font;

import com.mojang.blaze3d.systems.RenderSystem;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.grilledham.hamhacks.util.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomTextRenderer {
	
	private final HashMap<FontInfo.Type, CustomFont> font = new HashMap<>();
	
	private final FontFamily fontFamily;
	private final int size;
	private int resMultiplier;
	
	public CustomTextRenderer(FontFamily family, int size) {
		this.fontFamily = family;
		this.size = size;
		reload();
	}
	
	public void reload() {
		resMultiplier = PageManager.getPage(ClickGUI.class).fontResMultiplier.get().intValue();
		for(FontInfo.Type type : FontInfo.Type.values()) {
			byte[] bytes;
			FontFace face = fontFamily.get(type);
			if(face == null) {
				font.put(type, null);
				continue;
			}
			try(InputStream stream = face.toStream()) {
				bytes = stream.readAllBytes();
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
			ByteBuffer buf = BufferUtils.createByteBuffer(bytes.length).put(bytes);
			buf.flip();
			
			font.put(type, new CustomFont(buf, size * resMultiplier));
		}
	}
	
	public float render(DrawContext ctx, String text, float x, float y, int color, boolean shadow) {
		if(shadow) {
			Color c = new Color(color);
			c.setBrightness(c.getBrightness() / 4f);
			render(ctx.getMatrices(), text, x + 1, y + 1, c.getRGB(), true);
		}
		return render(ctx.getMatrices(), text, x, y, color, false);
	}
	
	public float render(DrawContext ctx, Text text, float x, float y, int color, boolean shadow) {
		return render(ctx, text.getString(), x, y, color, shadow);
	}

	private float render(MatrixStack matrices, String text, float x, float y, int color, boolean isShadow) {
		x *= resMultiplier;
		y *= resMultiplier;
		float start = x;
		float r = ((color >> 16) & 255) / 255f;
		float g = ((color >> 8) & 255) / 255f;
		float b = (color & 255) / 255f;
		float a = ((color >> 24) & 255) / 255f;
		
		final List<Pair<FontInfo.Type, String>> sections = new ArrayList<>();
		
		boolean escaped = false;
		boolean bold = false;
		boolean italic = false;
		
		StringBuilder current = new StringBuilder();
		for(int i = 0; i < text.length(); i++) {
			if(text.charAt(i) == '\u00a7') {
				escaped = true;
				if(!current.isEmpty() && !current.toString().replaceAll("\u00a7[0-9a-fklmnor]", "").isEmpty()) {
					sections.add(new Pair<>((bold && italic) ? FontInfo.Type.BoldItalic : bold ? FontInfo.Type.Bold : italic ? FontInfo.Type.Italic : FontInfo.Type.Regular, current.toString()));
					current = new StringBuilder();
				}
				i++;
				if(i >= text.length()) {
					current.append(text.charAt(i - 1));
					break;
				}
			}
			if(escaped) {
				switch(text.charAt(i)) {
					case 'l':
						bold = true;
						escaped = false;
						continue;
					case 'o':
						italic = true;
						escaped = false;
						continue;
					case 'r':
						bold = false;
						italic = false;
					default:
						current.append(text.charAt(i - 1));
						escaped = false;
						break;
				}
			}
			current.append(text.charAt(i));
		}
		sections.add(new Pair<>((bold && italic) ? FontInfo.Type.BoldItalic : bold ? FontInfo.Type.Bold : italic ? FontInfo.Type.Italic : FontInfo.Type.Regular, current.toString()));
		
		for(Pair<FontInfo.Type, String> section : sections) {
			CustomFont font = this.font.get(section.getLeft()) == null ? this.font.get(FontInfo.Type.Regular) : this.font.get(section.getLeft());
			text = section.getRight();
			
			matrices.push();
			matrices.scale(1f / resMultiplier, 1f / resMultiplier, 1f / resMultiplier);
			
			x = render(matrices, font, text, x, y, r, g, b, a, isShadow);
			
			matrices.pop();
		}
		
		float width = x - start;
		
		return width / resMultiplier;
	}
	
	private float render(MatrixStack matrices, CustomFont font, String text, float x, float y, float r, float g, float b, float a, boolean isShadow) {
		if(text.isEmpty()) return 0;
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
		RenderSystem.setShaderTexture(0, font.texture.getGlId());
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		
		font.texture.bindTexture();
		
		Matrix4f mat = matrices.peek().getPositionMatrix();
		
		BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		
		Color textColor = null;
		boolean underline = false;
		boolean strikethrough = false;
		boolean obfuscate = false;
		
		boolean escaped = false;
		for(int i = 0; i < text.length(); i++) {
			if(text.charAt(i) == '\u00a7') {
				escaped = true;
				continue;
			}
			label:
			{
				if(escaped) {
					switch(text.charAt(i)) {
						case '0':
							textColor = Color.getBlack();
							break;
						case '1':
							textColor = Color.getDarkBlue();
							break;
						case '2':
							textColor = Color.getDarkGreen();
							break;
						case '3':
							textColor = Color.getDarkAqua();
							break;
						case '4':
							textColor = Color.getDarkRed();
							break;
						case '5':
							textColor = Color.getDarkPurple();
							break;
						case '6':
							textColor = Color.getGold();
							break;
						case '7':
							textColor = Color.getGray();
							break;
						case '8':
							textColor = Color.getDarkGray();
							break;
						case '9':
							textColor = Color.getBlue();
							break;
						case 'a':
							textColor = Color.getGreen();
							break;
						case 'b':
							textColor = Color.getAqua();
							break;
						case 'c':
							textColor = Color.getRed();
							break;
						case 'd':
							textColor = Color.getLightPurple();
							break;
						case 'e':
							textColor = Color.getYellow();
							break;
						case 'f':
							textColor = Color.getWhite();
							break;
						case 'k':
							obfuscate = true;
							escaped = false;
							continue;
						case 'm':
							strikethrough = true;
							escaped = false;
							continue;
						case 'n':
							underline = true;
							escaped = false;
							continue;
						case 'r':
							textColor = null;
							obfuscate = false;
							strikethrough = false;
							underline = false;
							break;
						default:
							break label;
					}
					if(isShadow && textColor != null) textColor.setBrightness(textColor.getBrightness() / 4f);
					escaped = false;
					continue;
				}
			}
			final boolean hasFormatColor = textColor != null;
			float lastX = x;
			x += font.renderGlyph(buffer, mat, obfuscate ? font.randomize(text.charAt(i)) : text.charAt(i), x, y, hasFormatColor ? textColor.getR() / 255f : r, hasFormatColor ? textColor.getG() / 255f : g, hasFormatColor ? textColor.getB() / 255f : b, hasFormatColor ? textColor.getAlpha() : a);
			if(underline) {
				font.renderGlyph(buffer, mat, '_', lastX, y, hasFormatColor ? textColor.getR() / 255f : r, hasFormatColor ? textColor.getG() / 255f : g, hasFormatColor ? textColor.getB() / 255f : b, hasFormatColor ? textColor.getAlpha() : a);
			}
			if(strikethrough) {
				font.renderGlyph(buffer, mat, '-', lastX, y, hasFormatColor ? textColor.getR() / 255f : r, hasFormatColor ? textColor.getG() / 255f : g, hasFormatColor ? textColor.getB() / 255f : b, hasFormatColor ? textColor.getAlpha() : a);
			}
		}
		
		BufferRenderer.drawWithGlobalProgram(buffer.end());
		
		RenderSystem.disableBlend();
		return x;
	}
	
	public float getHeight() {
		return font.get(FontInfo.Type.Regular).getHeight() / resMultiplier;
	}
	
	public float getWidth(String text) {
		if(text.isEmpty()) return 0;
		final List<Pair<FontInfo.Type, String>> sections = new ArrayList<>();
		
		boolean escaped = false;
		boolean bold = false;
		boolean italic = false;
		
		StringBuilder current = new StringBuilder();
		for(int i = 0; i < text.length(); i++) {
			if(text.charAt(i) == '\u00a7') {
				escaped = true;
				if(!current.isEmpty() && !current.toString().replaceAll("\u00a7[0-9a-fklmnor]", "").isEmpty()) {
					sections.add(new Pair<>((bold && italic) ? FontInfo.Type.BoldItalic : bold ? FontInfo.Type.Bold : italic ? FontInfo.Type.Italic : FontInfo.Type.Regular, current.toString()));
					current = new StringBuilder();
				}
				i++;
				if(i >= text.length()) {
					current.append(text.charAt(i - 1));
					break;
				}
			}
			if(escaped) {
				switch(text.charAt(i)) {
					case 'l':
						bold = true;
						escaped = false;
						continue;
					case 'o':
						italic = true;
						escaped = false;
						continue;
					case 'r':
						bold = false;
						italic = false;
					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
					case 'a':
					case 'b':
					case 'c':
					case 'd':
					case 'e':
					case 'f':
					case 'k':
					case 'm':
					case 'n':
						escaped = false;
						continue;
					default:
						escaped = false;
						break;
				}
			}
			current.append(text.charAt(i));
		}
		sections.add(new Pair<>((bold && italic) ? FontInfo.Type.BoldItalic : bold ? FontInfo.Type.Bold : italic ? FontInfo.Type.Italic : FontInfo.Type.Regular, current.toString()));
		
		float width = 0;
		for(Pair<FontInfo.Type, String> section : sections) {
			if(font.get(section.getLeft()) == null) width += font.get(FontInfo.Type.Regular).getWidth(section.getRight());
			else width += font.get(section.getLeft()).getWidth(section.getRight());
		}
		return width / resMultiplier;
	}
}
