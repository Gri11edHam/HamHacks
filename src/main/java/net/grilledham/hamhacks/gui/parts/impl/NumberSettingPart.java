package net.grilledham.hamhacks.gui.parts.impl;

import net.grilledham.hamhacks.modules.render.ClickGUI;
import net.grilledham.hamhacks.util.RenderUtil;
import net.grilledham.hamhacks.util.setting.NumberSetting;
import net.grilledham.hamhacks.util.setting.SettingHelper;
import net.grilledham.hamhacks.util.setting.StringSetting;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;

public class NumberSettingPart extends SettingPart {
	
	private float hoverAnimation;
	private float sliderAnimation;
	
	private boolean dragging = false;
	
	private final StringSettingPart editor;
	
	@StringSetting(name = "")
	public String strVal;
	
	public NumberSettingPart(int x, int y, Field setting, Object obj) {
		super(x, y, 16, setting, obj);
		try {
			float roundedSetting = setting.getFloat(obj);
			NumberSetting numSetting = setting.getAnnotation(NumberSetting.class);
			if(numSetting.forceStep() && numSetting.step() != -1) {
				float closest = numSetting.min();
				for(float f = numSetting.min(); f < numSetting.max(); f += numSetting.step()) {
					float newDist = Math.abs(f - roundedSetting);
					float oldDist = Math.abs(closest - roundedSetting);
					if(newDist <= oldDist) {
						closest = f;
					} else {
						break;
					}
				}
				roundedSetting = closest;
			}
			strVal = String.format("%f", roundedSetting);
			Field strValField = getClass().getField("strVal");
			final Field finalSetting = setting;
			editor = new StringSettingPart(x + width - 207, y, strValField, this) {
				@Override
				public boolean type(int code, int scanCode, int modifiers) {
					if(code == GLFW.GLFW_KEY_ENTER) {
						try {
							finalSetting.setFloat(obj, Float.parseFloat(strVal));
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
		} catch(IllegalAccessException | NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
		resize(mc.textRenderer.getWidth(SettingHelper.getName(setting)) + 200 + 8 + editor.getWidth(), 16);
	}
	
	@Override
	public void render(MatrixStack stack, int mx, int my, int scrollX, int scrollY, float partialTicks) {
		int x = this.x + scrollX;
		int y = this.y + scrollY;
		stack.push();
		RenderUtil.preRender();
		
		int bgC = ClickGUI.getInstance().bgColor.getRGB();
		RenderUtil.drawRect(stack, x, y, width, height, bgC);
		
		int outlineC = 0xffcccccc;
		RenderUtil.drawHRect(stack, x + width - 206, y + 2, 204, 12, outlineC);
		
		boolean hovered = mx >= x + width - 204 && mx < x + width - 4 && my >= y + 4 && my < y + 12;
		int boxC = RenderUtil.mix((ClickGUI.getInstance().accentColor.getRGB() & 0xff000000) + 0xffffff, ClickGUI.getInstance().accentColor.getRGB(), hoverAnimation / 4);
		float sliderPercentage = (sliderAnimation - setting.getAnnotation(NumberSetting.class).min()) / (setting.getAnnotation(NumberSetting.class).max() - setting.getAnnotation(NumberSetting.class).min());
		RenderUtil.drawRect(stack, x + width - 204, y + 4, (200 * sliderPercentage), 8, boxC);
		
		mc.textRenderer.drawWithShadow(stack, SettingHelper.getName(setting), x + 2, y + 4, ClickGUI.getInstance().textColor.getRGB());
		
		RenderUtil.postRender();
		stack.pop();
		
		editor.draw(stack, mx, my, scrollX, scrollY, partialTicks);
		
		if(dragging) {
			float newPercentage = (mx - (x + width - 204)) / (float)200;
			float newVal = (newPercentage * (setting.getAnnotation(NumberSetting.class).max() - setting.getAnnotation(NumberSetting.class).min())) + setting.getAnnotation(NumberSetting.class).min();
			try {
				setting.setFloat(obj, newVal);
				float roundedSetting = setting.getFloat(obj);
				NumberSetting numSetting = setting.getAnnotation(NumberSetting.class);
				if(numSetting.forceStep()) {
					float closest = numSetting.min();
					for(float f = numSetting.min(); f < numSetting.max(); f += numSetting.step()) {
						float newDist = Math.abs(f - roundedSetting);
						float oldDist = Math.abs(closest - roundedSetting);
						if(newDist <= oldDist) {
							closest = f;
						} else {
							break;
						}
					}
					roundedSetting = closest;
				}
				strVal = String.format("%f", roundedSetting);
			} catch(IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		if(hovered) {
			hoverAnimation += partialTicks / 5;
		} else {
			hoverAnimation -= partialTicks / 5;
		}
		hoverAnimation = Math.min(1, Math.max(0, hoverAnimation));
		
		try {
			sliderAnimation += (setting.getFloat(obj) - sliderAnimation) * (partialTicks / 5);
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		}
		sliderAnimation = Math.min(setting.getAnnotation(NumberSetting.class).max(), Math.max(setting.getAnnotation(NumberSetting.class).min(), sliderAnimation));
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
	public boolean click(double mx, double my, int scrollX, int scrollY, int button) {
		int x = this.x + scrollX;
		int y = this.y + scrollY;
		if(editor.click(mx, my, scrollX, scrollY, button)) {
			return true;
		}
		if(mx >= x && mx < x + width && my >= y && my < y + height) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				dragging = true;
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			
			}
			return true;
		}
		return super.click(mx, my, scrollX, scrollY, button);
	}
	
	@Override
	public boolean release(double mx, double my, int scrollX, int scrollY, int button) {
		int x = this.x + scrollX;
		int y = this.y + scrollY;
		super.release(mx, my, scrollX, scrollY, button);
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			dragging = false;
		}
		if(editor.release(mx, my, scrollX, scrollY, button)) {
			return true;
		}
		if(mx >= x && mx < x + width && my >= y && my < y + height) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				try {
					SettingHelper.reset(setting, obj);
					float roundedSetting = setting.getFloat(obj);
					NumberSetting numSetting = setting.getAnnotation(NumberSetting.class);
					if(numSetting.forceStep()) {
						float closest = numSetting.min();
						for(float f = numSetting.min(); f < numSetting.max(); f += numSetting.step()) {
							float newDist = Math.abs(f - roundedSetting);
							float oldDist = Math.abs(closest - roundedSetting);
							if(newDist <= oldDist) {
								closest = f;
							} else {
								break;
							}
						}
						roundedSetting = closest;
					}
					strVal = String.format("%f", roundedSetting);
				} catch(IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			return true;
		}
		return super.release(mx, my, scrollX, scrollY, button);
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
