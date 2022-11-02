package net.grilledham.hamhacks.gui.parts.impl;

import net.grilledham.hamhacks.animation.Animation;
import net.grilledham.hamhacks.animation.AnimationBuilder;
import net.grilledham.hamhacks.animation.AnimationType;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.render.ClickGUI;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.grilledham.hamhacks.setting.SettingHelper;
import net.grilledham.hamhacks.setting.StringSetting;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;

public class NumberSettingPart extends SettingPart {
	
	private final Animation hoverAnimation = AnimationBuilder.create(AnimationType.IN_OUT_QUAD, 0.25).build();
	private final Animation sliderAnimation = AnimationBuilder.create(AnimationType.IN_OUT_QUAD, 0.25).build();
	
	private boolean dragging = false;
	
	private final StringSettingPart editor;
	
	@StringSetting(name = "")
	public String strVal;
	
	public NumberSettingPart(float x, float y, Field setting, Object obj) {
		super(x, y, MinecraftClient.getInstance().textRenderer.getWidth(SettingHelper.getName(setting).getString()) + 314, setting, obj);
		sliderAnimation.setAbsolute(setting.getAnnotation(NumberSetting.class).min());
		try {
			NumberSetting numSetting = setting.getAnnotation(NumberSetting.class);
			if(numSetting.forceStep() && numSetting.step() == 1) {
				strVal = (int)setting.getFloat(obj) + "";
			} else {
				strVal = setting.getFloat(obj) + "";
			}
			Field strValField = getClass().getField("strVal");
			editor = new StringSettingPart(x + width - 207, y, strValField, this) {
				
				@Override
				public void updateValue(String value) {
					super.updateValue(value);
					try {
						if(!strVal.equals("")) {
							NumberSettingPart.this.updateValue(Float.parseFloat(strVal), false);
						}
					} catch(Exception ignored) {}
				}
				
				@Override
				public boolean typeChar(char c, int modifiers) {
					return switch(c) {
						case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '-' -> super.typeChar(c, modifiers);
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
	public void render(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		stack.push();
		RenderUtil.preRender();
		
		int bgC = ModuleManager.getModule(ClickGUI.class).bgColor.getRGB();
		RenderUtil.drawRect(stack, x, y, width, height, bgC);
		
		int outlineC = 0xffcccccc;
		RenderUtil.drawHRect(stack, x + width - 206, y + 2, 204, 12, outlineC);
		
		boolean hovered = mx >= x + width - 204 && mx < x + width - 4 && my >= y + 4 && my < y + 12;
		int boxC = RenderUtil.mix((ModuleManager.getModule(ClickGUI.class).accentColor.getRGB() & 0xff000000) + 0xffffff, ModuleManager.getModule(ClickGUI.class).accentColor.getRGB(), hoverAnimation.get() / 4);
		float sliderPercentage = ((float)sliderAnimation.get() - setting.getAnnotation(NumberSetting.class).min()) / (setting.getAnnotation(NumberSetting.class).max() - setting.getAnnotation(NumberSetting.class).min());
		RenderUtil.drawRect(stack, x + width - 204, y + 4, (200 * sliderPercentage), 8, boxC);
		
		mc.textRenderer.drawWithShadow(stack, SettingHelper.getName(setting), x + 2, y + 4, ModuleManager.getModule(ClickGUI.class).textColor.getRGB());
		
		RenderUtil.postRender();
		stack.pop();
		
		editor.draw(stack, mx, my, scrollX, scrollY, partialTicks);
		
		if(dragging) {
			float newPercentage = (mx - (x + width - 204)) / (float)200;
			float newVal = (newPercentage * (setting.getAnnotation(NumberSetting.class).max() - setting.getAnnotation(NumberSetting.class).min())) + setting.getAnnotation(NumberSetting.class).min();
			try {
				updateValue(newVal, true);
			} catch(IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		hoverAnimation.set(hovered);
		hoverAnimation.update();
		
		try {
			sliderAnimation.set(setting.getFloat(obj));
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		}
		sliderAnimation.update();
	}
	
	@Override
	public void moveTo(float x, float y) {
		super.moveTo(x, y);
		editor.moveTo(x + width - 207 - editor.getWidth(), y);
	}
	
	@Override
	public void moveBy(float x, float y) {
		super.moveBy(x, y);
		editor.moveBy(x, y);
	}
	
	@Override
	public void resize(float maxW, float maxH) {
		super.resize(maxW, maxH);
		editor.moveTo(x + width - 207 - editor.getWidth(), y);
	}
	
	@Override
	public boolean click(double mx, double my, float scrollX, float scrollY, int button) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		if(editor.click(mx, my, scrollX, scrollY, button)) {
			return true;
		}
		if(mx >= x && mx < x + width && my >= y && my < y + height) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				if(mx >= x + width - 204 && mx < x + width - 4) {
					dragging = true;
					return true;
				}
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			
			}
			return false;
		}
		return super.click(mx, my, scrollX, scrollY, button);
	}
	
	@Override
	public boolean release(double mx, double my, float scrollX, float scrollY, int button) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		super.release(mx, my, scrollX, scrollY, button);
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT && dragging) {
			dragging = false;
			return false;
		}
		if(editor.release(mx, my, scrollX, scrollY, button)) {
			return true;
		}
		if(mx >= x && mx < x + width && my >= y && my < y + height) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				try {
					SettingHelper.reset(setting, obj);
					updateValue(setting.getFloat(obj), true);
				} catch(IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return super.release(mx, my, scrollX, scrollY, button);
	}
	
	private void updateValue(float newVal, boolean fromSlider) throws IllegalAccessException {
		float roundedSetting = newVal;
		NumberSetting numSetting = setting.getAnnotation(NumberSetting.class);
		roundedSetting = MathHelper.clamp(roundedSetting, numSetting.min(), numSetting.max());
		if((numSetting.forceStep() || fromSlider) && numSetting.step() != -1) {
			float closest = numSetting.min();
			for(float f = numSetting.min(); f <= numSetting.max(); f += numSetting.step()) {
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
		setting.setFloat(obj, roundedSetting);
		if(fromSlider) {
			if(numSetting.forceStep() && numSetting.step() == 1) {
				strVal = (int)roundedSetting + "";
			} else {
				strVal = roundedSetting + "";
			}
		}
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
