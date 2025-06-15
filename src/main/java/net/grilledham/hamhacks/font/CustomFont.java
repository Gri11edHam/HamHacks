package net.grilledham.hamhacks.font;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;

public class CustomFont {
	
	private static final int NUM_CHARS = 0x10000;
	private static final int TEXTURE_SIZE = 4096;

	private final Map<Character, Glyph> glyphs = new HashMap<>();
	private final float fontHeight;
	private final float scale;
	private final float ascent;
	public Identifier textureId;
	public FontTexture texture;
	
	private final Map<Integer, List<Character>> sortedWidths = new HashMap<>();
	private final Random random = new Random();
	
	public CustomFont(String name, ByteBuffer buf, int size) {
		textureId = Identifier.of("hamhacks", "fonts/" + name.toLowerCase().replaceAll(" ", "_").transform(s -> {
			char[] c = s.toCharArray();
			StringBuilder newString = new StringBuilder();
			for(char value : c) {
				if(Identifier.isCharValid(value)) {
					newString.append(value);
				}
			}
			return newString.toString();
		}));
		this.fontHeight = size;
		
		STBTTFontinfo fontinfo = STBTTFontinfo.create();
		STBTruetype.stbtt_InitFont(fontinfo, buf);
		
		STBTTPackedchar.Buffer cdata = STBTTPackedchar.create(NUM_CHARS);
		ByteBuffer bitmap = BufferUtils.createByteBuffer(TEXTURE_SIZE * TEXTURE_SIZE);
		
		STBTTPackContext packContext = STBTTPackContext.create();
		STBTruetype.stbtt_PackBegin(packContext, bitmap, TEXTURE_SIZE, TEXTURE_SIZE, 0, 1);
		STBTruetype.stbtt_PackSetOversampling(packContext, 2, 2);
		STBTruetype.stbtt_PackFontRange(packContext, buf, 0, fontHeight, 0, cdata);
		STBTruetype.stbtt_PackEnd(packContext);
		
		texture = new FontTexture(name, TEXTURE_SIZE, TEXTURE_SIZE, bitmap);
		scale = STBTruetype.stbtt_ScaleForPixelHeight(fontinfo, fontHeight);
		
		MinecraftClient.getInstance().getTextureManager().registerTexture(textureId, texture);
		
		try(MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer ascent = stack.mallocInt(1);
			STBTruetype.stbtt_GetFontVMetrics(fontinfo, ascent, null, null);
			this.ascent = ascent.get(0);
		}
		
		for(int i = 0; i < NUM_CHARS; i++) {
			STBTTPackedchar packedchar = cdata.get(i);
			
			float ipw = 1f / TEXTURE_SIZE;
			float iph = 1f / TEXTURE_SIZE;
			
			glyphs.put((char)i, new Glyph(
					packedchar.xoff(),
					packedchar.yoff(),
					packedchar.xoff2(),
					packedchar.yoff2(),
					packedchar.x0() * ipw,
					packedchar.y0() * iph,
					packedchar.x1() * ipw,
					packedchar.y1() * iph,
					packedchar.xadvance())
			);
			
			if(!sortedWidths.containsKey(Math.round(glyphs.get((char)i).advance))) {
				sortedWidths.put(Math.round(glyphs.get((char)i).advance), new ArrayList<>());
			}
			sortedWidths.get(Math.round(glyphs.get((char)i).advance)).add((char)i);
		}
	}
	
	public float getWidth(String text) {
		float width = 0;
		for(int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			Glyph g = glyphs.get(c);
			if(g == null) continue;
			width += g.advance;
		}
		return width;
	}
	
	public float getHeight() {
		return fontHeight;
	}
	
	public float renderGlyph(VertexConsumer buf, Matrix4f mat, char c, float x, float y, float r, float g, float b, float a) {
		Glyph glyph = glyphs.get(c);
		if(glyph == null) return 0;
		
		y += ascent * this.scale;
		
		buf.vertex(mat, x + glyph.x0, y + glyph.y0, 0).texture(glyph.u0, glyph.v0).color(r, g, b, a).light(0xF000F0);
		buf.vertex(mat, x + glyph.x0, y + glyph.y1, 0).texture(glyph.u0, glyph.v1).color(r, g, b, a).light(0xF000F0);
		buf.vertex(mat, x + glyph.x1, y + glyph.y1, 0).texture(glyph.u1, glyph.v1).color(r, g, b, a).light(0xF000F0);
		buf.vertex(mat, x + glyph.x1, y + glyph.y0, 0).texture(glyph.u1, glyph.v0).color(r, g, b, a).light(0xF000F0);
		
		return glyph.advance;
	}
	
	public char randomize(char c) {
		Glyph glyph = glyphs.get(c);
		List<Character> chars = sortedWidths.get(Math.round(glyph.advance));
		return chars.get(random.nextInt(chars.size()));
	}
	
	public record Glyph(float x0, float y0, float x1, float y1, float u0, float v0, float u1, float v1, float advance) {}
}
