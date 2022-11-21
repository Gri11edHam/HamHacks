package net.grilledham.hamhacks.gui.element.impl;

import net.grilledham.hamhacks.animation.Animation;
import net.grilledham.hamhacks.animation.AnimationBuilder;
import net.grilledham.hamhacks.animation.AnimationType;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.grilledham.hamhacks.setting.StringSetting;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class NumberSettingElement extends SettingElement<NumberSetting> {
	
	private final Animation hoverAnimation = AnimationBuilder.create(AnimationType.IN_OUT_QUAD, 0.25).build();
	private final Animation sliderAnimation = AnimationBuilder.create(AnimationType.IN_OUT_QUAD, 0.25).build();
	
	private boolean dragging = false;
	
	private final StringSettingElement editor;
	
	public StringSetting strVal;
	
	public NumberSettingElement(float x, float y, double scale, NumberSetting setting) {
		super(x, y, MinecraftClient.getInstance().textRenderer.getWidth(setting.getName()) + 314, scale, setting);
		sliderAnimation.setAbsolute(setting.min());
		if(setting.forceStep() && setting.step() == 1) {
			strVal = new StringSetting("", (int)(double)setting.get() + "", () -> false);
		} else {
			strVal = new StringSetting("", setting.get() + "", () -> false);
		}
		editor = new StringSettingElement(x + width - 207, y, scale, strVal) {
			
			@Override
			public void updateValue(String value) {
				super.updateValue(value);
				try {
					if(!strVal.get().equals("")) {
						NumberSettingElement.this.updateValue(Double.parseDouble(strVal.get()), false);
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
		resize(mc.textRenderer.getWidth(setting.getName()) + 200 + 8 + editor.getWidth(), 16);
	}
	
	@Override
	public void render(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		stack.push();
		RenderUtil.preRender();
		
		ClickGUI ui = PageManager.getPage(ClickGUI.class);
		int bgC = ui.bgColor.get().getRGB();
		RenderUtil.drawRect(stack, x, y, width, height, bgC);
		
		int outlineC = 0xffcccccc;
		RenderUtil.drawHRect(stack, x + width - 206, y + 2, 204, 12, outlineC);
		
		boolean hovered = mx >= x + width - 204 && mx < x + width - 4 && my >= y + 4 && my < y + 12;
		int boxC = RenderUtil.mix((ui.accentColor.get().getRGB() & 0xff000000) + 0xffffff, ui.accentColor.get().getRGB(), hoverAnimation.get() / 4);
		double sliderPercentage = (sliderAnimation.get() - setting.min()) / (setting.max() - setting.min());
		RenderUtil.drawRect(stack, x + width - 204, y + 4, (float)(200 * sliderPercentage), 8, boxC);
		
		mc.textRenderer.drawWithShadow(stack, setting.getName(), x + 2, y + 4, ui.textColor.get().getRGB());
		
		RenderUtil.postRender();
		stack.pop();
		
		editor.render(stack, mx, my, scrollX, scrollY, partialTicks);
		
		if(dragging) {
			float newPercentage = (mx - (x + width - 204)) / (float)200;
			double newVal = (newPercentage * (setting.max() - setting.min())) + setting.min();
			try {
				updateValue(newVal, true);
			} catch(IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		hoverAnimation.set(hovered);
		hoverAnimation.update();
		
		sliderAnimation.set(setting.get());
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
					setting.reset();
					updateValue(setting.get(), true);
				} catch(IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return super.release(mx, my, scrollX, scrollY, button);
	}
	
	private void updateValue(double newVal, boolean fromSlider) throws IllegalAccessException {
		double roundedSetting = newVal;
		NumberSetting numSetting = setting;
		roundedSetting = MathHelper.clamp(roundedSetting, numSetting.min(), numSetting.max());
		if((numSetting.forceStep() || fromSlider) && numSetting.step() != -1) {
			double closest = numSetting.min();
			for(double f = numSetting.min(); f <= numSetting.max(); f += numSetting.step()) {
				double newDist = Math.abs(f - roundedSetting);
				double oldDist = Math.abs(closest - roundedSetting);
				if(newDist <= oldDist) {
					closest = f;
				} else {
					break;
				}
			}
			roundedSetting = closest;
		}
		setting.set(roundedSetting);
		if(fromSlider) {
			if(numSetting.forceStep() && numSetting.step() == 1) {
				strVal.set((int)roundedSetting + "");
			} else {
				strVal.set(roundedSetting + "");
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
