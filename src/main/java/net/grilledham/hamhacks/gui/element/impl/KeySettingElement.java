package net.grilledham.hamhacks.gui.element.impl;

import net.grilledham.hamhacks.animation.Animation;
import net.grilledham.hamhacks.animation.AnimationType;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.grilledham.hamhacks.setting.KeySetting;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;

public class KeySettingElement extends SettingElement<Keybind> {
	
	private final Animation hoverAnimation = new Animation(AnimationType.EASE_IN_OUT, 0.25);
	
	private boolean listening = false;
	
	public KeySettingElement(float x, float y, double scale, KeySetting setting) {
		this(x, y, scale, setting::getName, setting.hasTooltip() ? setting::getTooltip : () -> "", setting::shouldShow, setting::get, (a) -> {}, setting::reset);
	}
	
	public KeySettingElement(float x, float y, double scale, Get<String> getName, Get<String> getTooltip, Get<Boolean> shouldShow, Get<Keybind> get, Set<Keybind> set, Runnable reset) {
		super(x, y, RenderUtil.getStringWidth(getName.get() + " [________________]") + 4, scale, getName, getTooltip, shouldShow, get, set, reset);
	}
	
	@Override
	public void draw(DrawContext ctx, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		MatrixStack stack = ctx.getMatrices();
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		stack.push();
		RenderUtil.preRender();
		
		ClickGUI ui = PageManager.getPage(ClickGUI.class);
		int bgC = ui.bgColor.get().getRGB();
		bgC = RenderUtil.mix(ui.bgColorHovered.get().getRGB(), bgC, hoverAnimation.get());
		
		boolean hovered;
		String text = "[" + (listening ? (get.get().getName().equals("None") ? "Listening..." : get.get().getName() + "...") : get.get().getName()) + "]";
		float textWidth = RenderUtil.getStringWidth(text);
		hovered = mx >= x + width - textWidth - 4 && mx < x + width && my >= y && my < y + height;
		RenderUtil.drawRect(stack, x, y, width - textWidth - 4, height, ui.bgColor.get().getRGB());
		RenderUtil.drawRect(stack, x + width - textWidth - 4, y, textWidth + 4, height, bgC);
		
		RenderUtil.drawString(ctx, getName.get(), x + 2, y + 4, ui.textColor.get().getRGB(), true);
		RenderUtil.drawString(ctx, text, x + width - textWidth - 2, y + 4, ui.textColor.get().getRGB(), true);
		
		RenderUtil.postRender();
		stack.pop();
		
		hoverAnimation.set(hovered);
		hoverAnimation.update();
	}
	
	@Override
	public boolean click(double mx, double my, float scrollX, float scrollY, int button) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		if(listening) {
			int code = button - Keybind.MOUSE_SHIFT;
			int[] codes = get.get().getKeyCombo();
			if(codes.length == 1 && codes[0] == 0) {
				get.get().setKey(code);
			} else {
				boolean containsKey = false;
				for(int i : codes) {
					if(i == code) {
						containsKey = true;
						break;
					}
				}
				if(!containsKey) {
					codes = Arrays.copyOf(codes, codes.length + 1);
					codes[codes.length - 1] = code;
				}
				get.get().setKey(codes);
			}
			return true;
		}
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
		String text;
		text = "[" + (listening ? (get.get().getName().equals("None") ? "Listening..." : get.get().getName() + "...") : get.get().getName()) + "]";
		float textWidth = RenderUtil.getStringWidth(text);
		if(listening) {
			return true;
		} else if(mx >= x + width - textWidth - 4 && mx < x + width && my >= y && my < y + height) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				get.get().setKey(0);
				listening = true;
				PageManager.getPage(ClickGUI.class).typing = true;
				return true;
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				reset.run();
			}
		}
		return super.release(mx, my, scrollX, scrollY, button);
	}
	
	@Override
	public boolean type(int code, int scanCode, int modifiers) {
		if(listening) {
			if(code == GLFW.GLFW_KEY_ESCAPE) {
				listening = false;
				PageManager.getPage(ClickGUI.class).typing = false;
			} else {
				int[] codes = get.get().getKeyCombo();
				if(codes.length == 1 && codes[0] == 0) {
					get.get().setKey(code);
				} else {
					boolean containsKey = false;
					for(int i : codes) {
						if(i == code) {
							containsKey = true;
							break;
						}
					}
					if(!containsKey) {
						codes = Arrays.copyOf(codes, codes.length + 1);
						codes[codes.length - 1] = code;
					}
					get.get().setKey(codes);
				}
				return true;
			}
			return false;
		}
		return super.type(code, scanCode, modifiers);
	}
}
