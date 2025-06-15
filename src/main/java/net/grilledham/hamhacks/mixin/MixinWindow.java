package net.grilledham.hamhacks.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.grilledham.hamhacks.mixininterface.IWindow;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.misc.BorderlessFullscreen;
import net.grilledham.hamhacks.modules.misc.TitleBar;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.MacWindowUtil;
import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.MonitorTracker;
import net.minecraft.client.util.Window;
import net.minecraft.resource.InputSupplier;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@Mixin(Window.class)
public abstract class MixinWindow implements IWindow {
	
	@Shadow @Final private long handle;
	@Shadow private int x;
	@Shadow private int y;
	@Shadow private int width;
	@Shadow private int height;
	@Shadow private int windowedX;
	@Shadow private int windowedY;
	@Shadow private int windowedHeight;
	@Shadow private int windowedWidth;
	@Shadow @Final private MonitorTracker monitorTracker;
	@Shadow private boolean fullscreen;
	
	@Shadow private boolean fullscreenVideoModeDirty;
	
	@Shadow public abstract void applyFullscreenVideoMode();
	
	@Unique private int oldWindowedX = 0;
	@Unique private int oldWindowedY = 0;
	@Unique private int oldWindowedWidth = 0;
	@Unique private int oldWindowedHeight = 0;
	@Unique private boolean wasEnabled = false;
	
	@Inject(method = "updateWindowRegion", at = @At("HEAD"), cancellable = true)
	private void preUpdate(CallbackInfo ci) {
		if(ModuleManager.getModule(BorderlessFullscreen.class) == null) return;
		boolean fullscreen = GLFW.glfwGetWindowMonitor(this.handle) != 0;
		if(ModuleManager.getModule(BorderlessFullscreen.class).isEnabled() && this.fullscreen) {
			if(!fullscreen && !wasEnabled) {
				windowedX = x;
				windowedY = y;
				windowedHeight = height;
				windowedWidth = width;
			}
			
			GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
			Monitor monitor = monitorTracker.getMonitor((Window)(Object)this);
			if(monitor != null) {
				x = monitor.getViewportX();
				y = monitor.getViewportY();
				width = monitor.getCurrentVideoMode().getWidth();
				height = monitor.getCurrentVideoMode().getHeight();
				
				GLFW.glfwSetWindowMonitor(handle, 0, x, y, width, height, GLFW.GLFW_DONT_CARE);
				
				wasEnabled = true;
				ci.cancel();
			} else {
				GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_DECORATED, GLFW.GLFW_TRUE);
			}
		} else {
			GLFW.glfwSetWindowAttrib(handle, GLFW.GLFW_DECORATED, GLFW.GLFW_TRUE);
		}
		
		oldWindowedX = windowedX;
		oldWindowedY = windowedY;
		oldWindowedWidth = windowedWidth;
		oldWindowedHeight = windowedHeight;
	}
	
	@Inject(method = "updateWindowRegion", at = @At("RETURN"))
	private void postUpdate(CallbackInfo ci) {
		if(wasEnabled) {
			wasEnabled = false;
			
			windowedX = oldWindowedX;
			windowedY = oldWindowedY;
			windowedWidth = oldWindowedWidth;
			windowedHeight = oldWindowedHeight;
		}
	}
	
	@Override
	public void hamHacks$updateVideoMode() {
		fullscreenVideoModeDirty = true;
		applyFullscreenVideoMode();
	}
	
	@Override
	public void hamHacks$setIcon(TitleBar.IconProvider provider) throws IOException {
		RenderSystem.assertOnRenderThread();
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
							byteBuffer.asIntBuffer().put(nativeImage.copyPixelsAbgr());
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
