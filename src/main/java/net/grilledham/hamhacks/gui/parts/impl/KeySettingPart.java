package net.grilledham.hamhacks.gui.parts.impl;

import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.render.ClickGUI;
import net.grilledham.hamhacks.util.Animation;
import net.grilledham.hamhacks.util.RenderUtil;
import net.grilledham.hamhacks.util.setting.SettingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;

public class KeySettingPart extends SettingPart {
	
	private final Animation hoverAnimation = Animation.getInOutQuad(0.25);
	
	private boolean listening = false;
	
	public KeySettingPart(float x, float y, Field setting, Object obj) {
		super(x, y, MinecraftClient.getInstance().textRenderer.getWidth(SettingHelper.getName(setting).getString() + " [________________]") + 4, setting, obj);
	}
	
	@Override
	public void render(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		stack.push();
		RenderUtil.preRender();
		
		int bgC = ClickGUI.getInstance().bgColor.getRGB();
		boolean hovered = mx >= x && mx < x + width && my >= y && my < y + height;
		bgC = RenderUtil.mix(ClickGUI.getInstance().bgColorHovered.getRGB(), bgC, hoverAnimation.get());
		RenderUtil.drawRect(stack, x, y, width, height, bgC);
		
		mc.textRenderer.drawWithShadow(stack, SettingHelper.getName(setting), x + 2, y + 4, ClickGUI.getInstance().textColor.getRGB());
		try {
			String text = "[" + (listening ? "Listening..." : ((Keybind)setting.get(obj)).getName()) + "]";
			mc.textRenderer.drawWithShadow(stack, text, x + width - mc.textRenderer.getWidth(text) - 2, y + 4, ClickGUI.getInstance().textColor.getRGB());
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		}
		
		RenderUtil.postRender();
		stack.pop();
		
		hoverAnimation.set(hovered);
		hoverAnimation.update();
	}
	
	@Override
	public boolean click(double mx, double my, float scrollX, float scrollY, int button) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		if(mx >= x && mx < x + width && my >= y && my < y + height) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			
			}
			return true;
		}
		return super.click(mx, my, scrollX, scrollY, button);
	}
	
	@Override
	public boolean release(double mx, double my, float scrollX, float scrollY, int button) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		super.release(mx, my, scrollX, scrollY, button);
		try {
			if(listening) {
				((Keybind)setting.get(obj)).setKey(button, true);
				listening = false;
				return false;
			} else if(mx >= x && mx < x + width && my >= y && my < y + height) {
				if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
					listening = true;
					return true;
				} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
					SettingHelper.reset(setting, obj);
				}
			}
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		}
		return super.release(mx, my, scrollX, scrollY, button);
	}
	
	@Override
	public boolean type(int code, int scanCode, int modifiers) {
		if(listening) {
			try {
				if(code == GLFW.GLFW_KEY_ESCAPE) {
					((Keybind)setting.get(obj)).setKey(0);
				} else {
					((Keybind)setting.get(obj)).setKey(code, false);
				}
			} catch(IllegalAccessException e) {
				e.printStackTrace();
			}
			listening = false;
			return false;
		}
		return super.type(code, scanCode, modifiers);
	}
}
