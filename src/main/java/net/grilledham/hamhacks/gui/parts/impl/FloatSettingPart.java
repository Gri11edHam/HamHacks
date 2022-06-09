package net.grilledham.hamhacks.gui.parts.impl;

import net.grilledham.hamhacks.modules.render.ClickGUI;
import net.grilledham.hamhacks.util.RenderUtil;
import net.grilledham.hamhacks.util.setting.settings.FloatSetting;
import net.grilledham.hamhacks.util.setting.settings.StringSetting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class FloatSettingPart extends SettingPart {
	
	private float hoverAnimation;
	private float sliderAnimation;
	
	private final FloatSetting setting;
	
	private boolean dragging = false;
	
	private final StringSettingPart editor;
	private final StringSetting strVal;
	
	public FloatSettingPart(int x, int y, FloatSetting setting) {
		super(x, y, 16, setting);
		strVal = new StringSetting(Text.translatable(""), setting.getValue().toString());
		editor = new StringSettingPart(x + width - 207, y, strVal) {
			@Override
			public boolean type(int code, int scanCode, int modifiers) {
				if(code == GLFW.GLFW_KEY_ENTER) {
					try {
						setting.setValue(Float.valueOf(strVal.getValue()));
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
				return super.type(code, scanCode, modifiers);
			}
			
			@Override
			public boolean typeChar(char c, int modifiers) {
				return switch(c) {
					case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.' -> super.typeChar(c, modifiers);
					default -> false;
				};
			}
		};
		editor.drawBackground = false;
		resize(mc.textRenderer.getWidth(setting.getName()) + 200 + 8 + editor.getWidth(), 16);
		this.setting = setting;
	}
	
	@Override
	public void render(MatrixStack stack, int mx, int my, float partialTicks) {
		stack.push();
		RenderUtil.preRender();
		
		int bgC = ClickGUI.getInstance().bgColor.getRGB();
		RenderUtil.drawRect(stack, x, y, width, height, bgC);
		
		int outlineC = 0xffcccccc;
		RenderUtil.drawHRect(stack, x + width - 206, y + 2, 204, 12, outlineC);
		
		boolean hovered = mx >= x + width - 204 && mx < x + width - 4 && my >= y + 4 && my < y + 12;
		int boxC = RenderUtil.mix((ClickGUI.getInstance().accentColor.getRGB() & 0xff000000) + 0xffffff, ClickGUI.getInstance().accentColor.getRGB(), hoverAnimation / 4);
		float sliderPercentage = (sliderAnimation - setting.getMin()) / (setting.getMax() - setting.getMin());
		RenderUtil.drawRect(stack, x + width - 204, y + 4, (200 * sliderPercentage), 8, boxC);
		
		mc.textRenderer.drawWithShadow(stack, setting.getName(), x + 2, y + 4, ClickGUI.getInstance().textColor.getRGB());
		
		RenderUtil.postRender();
		stack.pop();
		
		editor.draw(stack, mx, my, partialTicks);
		
		if(dragging) {
			float newPercentage = (mx - (x + width - 204)) / (float)200;
			float newVal = (newPercentage * (setting.getMax() - setting.getMin())) + setting.getMin();
			setting.setValue(newVal);
			strVal.setValue(setting.getValue().toString());
		}
		
		if(hovered) {
			hoverAnimation += partialTicks / 5;
		} else {
			hoverAnimation -= partialTicks / 5;
		}
		hoverAnimation = Math.min(1, Math.max(0, hoverAnimation));
		
		sliderAnimation += (setting.getValue() - sliderAnimation) * (partialTicks / 5);
		sliderAnimation = Math.min(setting.getMax(), Math.max(setting.getMin(), sliderAnimation));
	}
	
	@Override
	public void moveTo(int x, int y) {
		super.moveTo(x, y);
		editor.moveTo(x + width - 207 - editor.getWidth(), y);
	}
	
	@Override
	public void moveBy(int x, int y) {
		super.moveBy(x, y);
		editor.moveBy(x, y);
	}
	
	@Override
	public void resize(int maxW, int maxH) {
		super.resize(maxW, maxH);
		editor.moveTo(x + width - 207 - editor.getWidth(), y);
	}
	
	@Override
	public boolean click(double mx, double my, int button) {
		if(editor.click(mx, my, button)) {
			return true;
		}
		if(mx >= x && mx < x + width && my >= y && my < y + height) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				dragging = true;
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			
			}
			return true;
		}
		return super.click(mx, my, button);
	}
	
	@Override
	public boolean release(double mx, double my, int button) {
		super.release(mx, my, button);
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			dragging = false;
		}
		if(editor.release(mx, my, button)) {
			return true;
		}
		if(mx >= x && mx < x + width && my >= y && my < y + height) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				setting.reset();
				strVal.setValue(setting.getValue().toString());
			}
			return true;
		}
		return super.release(mx, my, button);
	}
	
	@Override
	public boolean type(int code, int scanCode, int modifiers) {
		if(editor.type(code, scanCode, modifiers)) {
			return true;
		}
		return super.type(code, scanCode, modifiers);
	}
	
	@Override
	public boolean typeChar(char c, int modifiers) {
		if(editor.typeChar(c, modifiers)) {
			return true;
		}
		return super.typeChar(c, modifiers);
	}
}
