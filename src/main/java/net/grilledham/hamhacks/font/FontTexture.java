package net.grilledham.hamhacks.font;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.TextureFormat;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

public class FontTexture extends AbstractTexture {
	
	public FontTexture(String name, int width, int height, ByteBuffer buffer) {
		ByteBuffer buf = BufferUtils.createByteBuffer(buffer.capacity() * 4);
		for(int i = 0; i < buffer.capacity(); i++) {
			buf.put(i * 4, (byte)255);
			buf.put(i * 4 + 1, (byte)255);
			buf.put(i * 4 + 2, (byte)255);
			buf.put(i * 4 + 3, buffer.get(i));
		}
		
		this.glTexture = RenderSystem.getDevice().createTexture(() -> name, TextureFormat.RGBA8, width, height, 1);
		this.glTexture.setTextureFilter(FilterMode.NEAREST, false);
		RenderSystem.getDevice().createCommandEncoder().writeToTexture(this.glTexture, buf.asIntBuffer(), NativeImage.Format.RGBA, 0, 0, 0, width, height);
	}
}
