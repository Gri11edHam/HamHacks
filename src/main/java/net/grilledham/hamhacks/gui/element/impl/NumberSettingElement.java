package net.grilledham.hamhacks.gui.element.impl;

import net.grilledham.hamhacks.HamHacksClient;
import net.grilledham.hamhacks.animation.Animation;
import net.grilledham.hamhacks.animation.AnimationType;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.grilledham.hamhacks.setting.StringSetting;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class NumberSettingElement extends SettingElement<Double> {
	
	private final Animation hoverAnimation = new Animation(AnimationType.EASE_IN_OUT, 0.25);
	private final Animation sliderAnimation = new Animation(AnimationType.EASE_IN_OUT, 0.25);
	
	private boolean dragging = false;
	
	private final StringSettingElement editor;
	
	public StringSetting strVal;
	
	protected final Get<Double> min;
	protected final Get<Double> max;
	protected final Get<Double> step;
	protected final Get<Boolean> forceStep;
	
	protected final boolean hasBounds;
	
	public boolean drawBackground = true;
	
	public NumberSettingElement(float x, float y, double scale, NumberSetting setting, boolean hasBounds) {
		this(x, y, scale, setting::getName, setting.hasTooltip() ? setting::getTooltip : () -> "", setting::shouldShow, setting::get, setting::set, setting::reset, setting::min, setting::max, setting::step, setting::forceStep, hasBounds);
	}
	
	public NumberSettingElement(float x, float y, double scale, Get<String> getName, Get<String> getTooltip, Get<Boolean> shouldShow, Get<Double> get, Set<Double> set, Runnable reset, Get<Double> min, Get<Double> max, Get<Double> step, Get<Boolean> forceStep, boolean hasBounds) {
		super(x, y, MinecraftClient.getInstance().textRenderer.getWidth(getName.get()) + 110 + (hasBounds ? 104 : 4), scale, getName, getTooltip, shouldShow, get, set, reset);
		this.min = min;
		this.max = max;
		this.step = step;
		this.forceStep = forceStep;
		this.hasBounds = hasBounds;
		sliderAnimation.setAbsolute(min.get());
		if(forceStep.get() && step.get() == 1) {
			strVal = new StringSetting("", String.valueOf(get.get().intValue()), () -> false);
		} else {
			strVal = new StringSetting("", String.valueOf(get.get().floatValue()), () -> false);
		}
		editor = new StringSettingElement(x + width - (hasBounds ? 207 : 103), y, scale, strVal) {
			
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
		resize(mc.textRenderer.getWidth(getName.get()) + (hasBounds ? 200 + 8 : 4) + editor.getWidth(), 16);
	}
	
	@Override
	public void draw(DrawContext ctx, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		MatrixStack stack = ctx.getMatrices();
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		stack.push();
		RenderUtil.preRender();
		
		boolean hovered = false;
		ClickGUI ui = PageManager.getPage(ClickGUI.class);
		if(drawBackground) {
			int bgC = ui.bgColor.get().getRGB();
			RenderUtil.drawRect(stack, x, y, width, height, bgC);
		}
		
		if(hasBounds) {
			int outlineC = 0xffcccccc;
			RenderUtil.drawHRect(stack, x + width - 206, y + 2, 204, 12, outlineC);
			
			hovered = mx >= x + width - 204 && mx < x + width - 4 && my >= y + 4 && my < y + 12;
			int boxC = RenderUtil.mix((ui.accentColor.get().getRGB() & 0xff000000) + 0xffffff, ui.accentColor.get().getRGB(), hoverAnimation.get() / 4);
			double sliderPercentage = (sliderAnimation.get() - min.get()) / (max.get() - min.get());
			RenderUtil.drawRect(stack, x + width - 204, y + 4, (float)(200 * sliderPercentage), 8, boxC);
		}
		
		RenderUtil.drawString(ctx, getName.get(), x + 2, y + 4, ui.textColor.get().getRGB(), true);
		
		RenderUtil.postRender();
		stack.pop();
		
		editor.render(ctx, mx, my, scrollX, scrollY, partialTicks);
		
		if(dragging && hasBounds) {
			float newPercentage = (mx - (x + width - 204)) / (float)200;
			double newVal = (newPercentage * (max.get() - min.get())) + min.get();
			try {
				updateValue(newVal, true);
			} catch(IllegalAccessException e) {
				HamHacksClient.LOGGER.error("", e);
			}
		}
		
		hoverAnimation.set(hovered);
		hoverAnimation.update();
		
		sliderAnimation.set(get.get());
		sliderAnimation.update();
	}
	
	@Override
	public void moveTo(float x, float y) {
		super.moveTo(x, y);
		editor.moveTo(x + width - (hasBounds ? 207 : 3) - editor.getWidth(), y);
	}
	
	@Override
	public void moveBy(float x, float y) {
		super.moveBy(x, y);
		editor.moveBy(x, y);
	}
	
	@Override
	public void resize(float maxW, float maxH) {
		super.resize(maxW, maxH);
		editor.moveTo(x + width - (hasBounds ? 207 : 3) - editor.getWidth(), y);
	}
	
	@Override
	public boolean click(double mx, double my, float scrollX, float scrollY, int button) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		if(editor.click(mx, my, scrollX, scrollY, button)) {
			return true;
		}
		if(!hasBounds) {
			return super.click(mx, my, scrollX, scrollY, button);
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
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT && dragging && hasBounds) {
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
					reset.run();
					updateValue(get.get(), true);
				} catch(IllegalAccessException e) {
					HamHacksClient.LOGGER.error("", e);
				}
			}
		}
		return super.release(mx, my, scrollX, scrollY, button);
	}
	
	protected void updateValue(double newVal, boolean fromSlider) throws IllegalAccessException {
		double roundedSetting = newVal;
		if(hasBounds) {
			roundedSetting = MathHelper.clamp(roundedSetting, min.get(), max.get());
			if((forceStep.get() || fromSlider) && step.get() != -1) {
				double closest = min.get();
				for(double f = min.get(); (float)f <= max.get(); f += step.get()) {
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
			set.set(roundedSetting);
			if(fromSlider) {
				if(forceStep.get() && step.get() == 1) {
					strVal.set(String.valueOf((int)roundedSetting));
				} else {
					strVal.set(String.valueOf((float)roundedSetting));
				}
			}
		} else {
			if(forceStep.get() && step.get() == 1) {
				roundedSetting = Math.round(roundedSetting);
			}
			set.set(roundedSetting);
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
