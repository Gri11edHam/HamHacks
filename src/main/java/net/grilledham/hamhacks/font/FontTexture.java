package net.grilledham.hamhacks.font;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.resource.ResourceManager;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL30C.*;

public class FontTexture extends AbstractTexture {
	
	public FontTexture(int width, int height, ByteBuffer buf) {
		RenderSystem.recordRenderCall(() -> upload(width, height, buf));
	}
	
	private void upload(int width, int height, ByteBuffer buffer) {
		ByteBuffer buf = BufferUtils.createByteBuffer(buffer.capacity() * 4);
		for(int i = 0; i < buffer.capacity(); i++) {
			buf.put(i * 4, (byte)255);
			buf.put(i * 4 + 1, (byte)255);
			buf.put(i * 4 + 2, (byte)255);
			buf.put(i * 4 + 3, buffer.get(i));
		}
		buf.flip();
		
		bindTexture();
		
		glPixelStorei(GL_UNPACK_SWAP_BYTES, GL_FALSE);
		glPixelStorei(GL_UNPACK_LSB_FIRST, GL_FALSE);
		glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
		glPixelStorei(GL_UNPACK_IMAGE_HEIGHT, 0);
		glPixelStorei(GL_UNPACK_SKIP_ROWS, 0);
		glPixelStorei(GL_UNPACK_SKIP_PIXELS, 0);
		glPixelStorei(GL_UNPACK_SKIP_IMAGES, 0);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 4);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		
		buf.rewind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
	}
}
