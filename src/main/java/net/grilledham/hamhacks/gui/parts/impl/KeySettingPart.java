package net.grilledham.hamhacks.gui.parts.impl;

import net.grilledham.hamhacks.modules.render.ClickGUI;
import net.grilledham.hamhacks.util.RenderUtil;
import net.grilledham.hamhacks.util.setting.settings.KeySetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

public class KeySettingPart extends SettingPart {
	
	private float hoverAnimation;
	
	private final KeySetting setting;
	
	private boolean listening = false;
	
	public KeySettingPart(int x, int y, KeySetting setting) {
		super(x, y, MinecraftClient.getInstance().textRenderer.getWidth(setting.getName() + "    [________________]") + 4, setting);
		this.setting = setting;
	}
	
	@Override
	public void render(MatrixStack stack, int mx, int my, float partialTicks) {
		stack.push();
		RenderUtil.preRender();
		
		int bgC = ClickGUI.getInstance().bgColor.getRGB();
		boolean hovered = mx >= x && mx < x + width && my >= y && my < y + height;
		bgC = RenderUtil.mix(ClickGUI.getInstance().bgColorHovered.getRGB(), bgC, hoverAnimation);
		RenderUtil.drawRect(stack, x, y, width, height, bgC);
		
		mc.textRenderer.drawWithShadow(stack, setting.getName(), x + 2, y + 4, ClickGUI.getInstance().textColor.getRGB());
		String text = "[" + (listening ? "Listening..." : setting.getKeybind().getName()) + "]";
		mc.textRenderer.drawWithShadow(stack, text, x + width - mc.textRenderer.getWidth(text) - 2, y + 4, ClickGUI.getInstance().textColor.getRGB());
		
		RenderUtil.postRender();
		stack.pop();
		
		if(hovered) {
			hoverAnimation += partialTicks / 5;
		} else {
			hoverAnimation -= partialTicks / 5;
		}
		hoverAnimation = Math.min(1, Math.max(0, hoverAnimation));
	}
	
	@Override
	public boolean click(double mx, double my, int button) {
		if(mx >= x && mx < x + width && my >= y && my < y + height) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			
			}
			return true;
		}
		return super.click(mx, my, button);
	}
	
	@Override
	public boolean release(double mx, double my, int button) {
		super.release(mx, my, button);
		if(listening) {
			setting.getKeybind().setKey(button, true);
			listening = false;
			return true;
		} else if(mx >= x && mx < x + width && my >= y && my < y + height) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				listening = true;
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				setting.reset();
			}
			return true;
		}
		return super.release(mx, my, button);
	}
	
	@Override
	public boolean type(int code, int scanCode, int modifiers) {
		if(listening) {
			if(code == GLFW.GLFW_KEY_ESCAPE) {
				setting.getKeybind().setKey(0);
			} else {
				setting.getKeybind().setKey(code, false);
			}
			listening = false;
			return true;
		}
		return super.type(code, scanCode, modifiers);
	}
}
