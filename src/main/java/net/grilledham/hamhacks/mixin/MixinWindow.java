package net.grilledham.hamhacks.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.grilledham.hamhacks.mixininterface.IWindow;
import net.grilledham.hamhacks.modules.misc.TitleBar;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.MacWindowUtil;
import net.minecraft.client.util.Window;
import net.minecraft.resource.InputSupplier;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@Mixin(Window.class)
public class MixinWindow implements IWindow {
	
	@Shadow @Final private long handle;
	
	@Override
	public void setIcon(TitleBar.IconProvider provider) throws IOException {
		RenderSystem.assertInInitPhase();
		if (MinecraftClient.IS_SYSTEM_MAC) {
			MacWindowUtil.setApplicationIconImage(provider.getMacIcon());
		} else {
			List<InputSupplier<InputStream>> list = provider.getIcons();
			List<ByteBuffer> list2 = new ArrayList<>(list.size());
			
			try {
				MemoryStack memoryStack = MemoryStack.stackPush();
				
				try {
					GLFWImage.Buffer buffer = GLFWImage.malloc(list.size(), memoryStack);
					
					for(int i = 0; i < list.size(); ++i) {
						NativeImage nativeImage = NativeImage.read((InputStream)((InputSupplier<?>)list.get(i)).get());
						
						try {
							ByteBuffer byteBuffer = MemoryUtil.memAlloc(nativeImage.getWidth() * nativeImage.getHeight() * 4);
							list2.add(byteBuffer);
							byteBuffer.asIntBuffer().put(nativeImage.copyPixelsRgba());
							buffer.position(i);
							buffer.width(nativeImage.getWidth());
							buffer.height(nativeImage.getHeight());
							buffer.pixels(byteBuffer);
						} catch (Throwable var19) {
							try {
								nativeImage.close();
							} catch(Throwable var18) {
								var19.addSuppressed(var18);
							}
							
							throw var19;
						}
						
						nativeImage.close();
					}
					
					GLFW.glfwSetWindowIcon(this.handle, buffer.position(0));
				} catch (Throwable var20) {
					try {
						memoryStack.close();
					} catch(Throwable var17) {
						var20.addSuppressed(var17);
					}
					
					throw var20;
				}
				
				memoryStack.close();
			} finally {
				list2.forEach(MemoryUtil::memFree);
			}
			
		}
	}
}
