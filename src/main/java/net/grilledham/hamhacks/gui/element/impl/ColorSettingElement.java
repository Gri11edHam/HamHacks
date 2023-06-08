package net.grilledham.hamhacks.gui.element.impl;

import net.grilledham.hamhacks.animation.Animation;
import net.grilledham.hamhacks.animation.AnimationType;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.grilledham.hamhacks.setting.BoolSetting;
import net.grilledham.hamhacks.setting.ColorSetting;
import net.grilledham.hamhacks.setting.StringSetting;
import net.grilledham.hamhacks.util.Color;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

import java.util.HexFormat;

public class ColorSettingElement extends SettingElement<Color> {
	
	private final Animation hoverAnimation = new Animation(AnimationType.EASE_IN_OUT, 0.25);
	private final Animation selectionAnimation = new Animation(AnimationType.EASE_IN_OUT, 0.25, true);
	
	public BoolSetting chroma;
	
	public StringSetting hexVal;
	
	private final BoolSettingElement chromaPart;
	private final StringSettingElement hexValPart;
	
	private boolean selected = false;
	
	private boolean dragging = false;
	private double dragStartX = -1;
	private double dragStartY = -1;
	
	public ColorSettingElement(float x, float y, double scale, ColorSetting setting) {
		this(x, y, scale, setting::getName, setting.hasTooltip() ? setting::getTooltip : () -> "", setting::shouldShow, setting::get, setting::set, setting::reset);
	}
	
	public ColorSettingElement(float x, float y, double scale, Get<String> getName, Get<String> getTooltip, Get<Boolean> shouldShow, Get<Color> get, Set<Color> set, Runnable reset) {
		super(x, y, 16, scale, getName, getTooltip, shouldShow, get, set, reset);
		chroma = new BoolSetting("hamhacks.setting.colorSettingElement.chroma", false, () -> false);
		chroma.set(get.get().getChroma());
		chromaPart = new BoolSettingElement(x, y, scale, chroma) {
			@Override
			public boolean release(double mx, double my, float scrollX, float scrollY, int button) {
				boolean superReturn = super.release(mx, my, scrollX, scrollY, button);
				ColorSettingElement.this.get.get().setChroma(get.get());
				return superReturn;
			}
		};
		chromaPart.drawBackground = false;
		StringBuilder hex = new StringBuilder(Integer.toHexString(get.get().getRGB()));
		while(hex.length() < 8) {
			hex.insert(0, "0");
		}
		hexVal = new StringSetting("", hex.toString(), () -> false, "ffffffff");
		hexValPart = new StringSettingElement(x, y, scale, hexVal) {
			@Override
			public boolean type(int code, int scanCode, int modifiers) {
				if(code == GLFW.GLFW_KEY_ENTER) {
					try {
						if(hexVal.get().length() > 8) {
							hexVal.set(hexVal.get().substring(hexVal.get().length() - 8));
						}
						ColorSettingElement.this.get.get().set(HexFormat.fromHexDigits(hexVal.get()));
						StringBuilder hex = new StringBuilder(Integer.toHexString(ColorSettingElement.this.get.get().getRGB()));
						while(hex.length() < 8) {
							hex.insert(0, "0");
						}
						hexVal.set(hex.toString());
						type(GLFW.GLFW_KEY_END, GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_END), 0);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
				return super.type(code, scanCode, modifiers);
			}
			
			@Override
			public boolean typeChar(char c, int modifiers) {
				return switch(c) {
					case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' -> super.typeChar(c, modifiers);
					default -> false;
				};
			}
		};
		hexValPart.drawBackground = false;
		resize(mc.textRenderer.getWidth(getName.get()) + 10 + 16, 16);
	}
	
	@Override
	public void render(DrawContext ctx, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		MatrixStack stack = ctx.getMatrices();
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		stack.push();
		RenderUtil.preRender();
		
		ClickGUI ui = PageManager.getPage(ClickGUI.class);
		int bgC = ui.bgColor.get().getRGB();
		RenderUtil.drawRect(stack, x, y, width, height, bgC);
		
		int outlineC = 0xffcccccc;
		RenderUtil.drawHRect(stack, x + width - 20, y + 2, 18, 12, outlineC);
		
		boolean hovered = mx >= x + width - 18 && mx < x + width - 2 && my >= y + 4 && my < y + 12;
		int boxC = RenderUtil.mix((get.get().getRGB() & 0xff000000) + 0xffffff, get.get().getRGB(), hoverAnimation.get() / 4);
		RenderUtil.drawRect(stack, x + width - 18, y + 4, 14, 8, boxC);
		
		RenderUtil.drawString(ctx, getName.get(), x + 2, y + 4, ui.textColor.get().getRGB(), true);
		
		RenderUtil.postRender();
		stack.pop();
		
		hoverAnimation.set(hovered);
		
		hoverAnimation.update();
		selectionAnimation.set(selected);
		selectionAnimation.update();
	}
	
	@Override
	public void renderTop(DrawContext ctx, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		MatrixStack stack = ctx.getMatrices();
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		stack.push();
		stack.translate(0, 0, 200);
		float w = 220;
		float h = 122;
		float newX = x + width - w;
		float newY = y + height;
		float subPartScroll = 0;
		if(newY + h > mc.currentScreen.height) {
			newY = y - h;
			subPartScroll = -height - h;
		}
		
		RenderUtil.pushScissor(newX - 1 + ((w + 2) * (float)(1 - selectionAnimation.get())), newY - 1, (w + 2) * (float)selectionAnimation.get(), (h + 2) * (float)selectionAnimation.get(), (float)scale);
		RenderUtil.preRender();
		
		ClickGUI ui = PageManager.getPage(ClickGUI.class);
		RenderUtil.drawRect(stack, newX, newY, w, h, ui.bgColor.get().getRGB());
		RenderUtil.drawHRect(stack, newX - 1, newY - 1, w + 2, h + 2, 0xffcccccc);
		
		RenderUtil.drawHRect(stack, newX + 1, newY + 1, 103, 103, 0xffcccccc);
		RenderUtil.drawSBGradient(stack, newX + 2, newY + 2, 101, 101, get.get().getHue());
		
		RenderUtil.drawHRect(stack, newX + 105, newY + 1, 22, 103, 0xffcccccc);
		RenderUtil.drawHueGradient(stack, newX + 106, newY + 2, 20, 101);
		
		RenderUtil.drawHRect(stack, newX + 129, newY + 1, 22, 103, 0xffcccccc);
		RenderUtil.drawAlphaGradient(stack, newX + 130, newY + 2, 20, 101);
		
		RenderUtil.drawHRect(stack, newX + 1 + (100 * get.get().getSaturation()), newY + 1 + (100 * (1 - get.get().getBrightness())), 3, 3, 0xffcccccc);
		RenderUtil.drawHRect(stack, newX + 105, newY + 1 + (100 * (get.get().getHue())), 22, 3, 0xffcccccc);
		RenderUtil.drawHRect(stack, newX + 129, newY + 1 + (100 * (1 - get.get().getAlpha())), 22, 3, 0xffcccccc);
		
		chromaPart.render(ctx, mx, my, scrollX, scrollY + subPartScroll, partialTicks);
		if(selectionAnimation.get() > 0.8) {
			hexValPart.render(ctx, mx, my, scrollX, scrollY + subPartScroll, partialTicks);
		}
		
		RenderUtil.popScissor();
		RenderUtil.postRender();
		stack.pop();
		
		if(selected) {
			if(dragging) {
				if(dragStartY >= newY + 2 && dragStartY <= newY + 102) {
					// SB
					if(dragStartX >= newX + 2 && dragStartX <= newX + 103) {
						float newS = (mx - (newX + 1)) / 100f;
						float newB = 1 - ((my - (newY + 1)) / 100f);
						newS = Math.min(Math.max(newS, 0), 1);
						newB = Math.min(Math.max(newB, 0), 1);
						get.get().setSaturation(newS);
						get.get().setBrightness(newB);
						StringBuilder hex = new StringBuilder(Integer.toHexString(get.get().getRGB()));
						while(hex.length() < 8) {
							hex.insert(0, "0");
						}
						hexVal.set(hex.toString());
					}
					
					// Hue
					if(dragStartX >= newX + 106 && dragStartX <= newX + 126) {
						float newH = (my - (newY + 1)) / 100f;
						newH = Math.min(Math.max(newH, 0), 1);
						get.get().setHue(newH);
						StringBuilder hex = new StringBuilder(Integer.toHexString(get.get().getRGB()));
						while(hex.length() < 8) {
							hex.insert(0, "0");
						}
						hexVal.set(hex.toString());
					}
					
					// Alpha
					if(dragStartX >= newX + 130 && dragStartX <= newX + 150) {
						float newA = 1 - ((my - (newY + 1)) / 100f);
						newA = Math.min(Math.max(newA, 0), 1);
						get.get().setAlpha(newA);
						StringBuilder hex = new StringBuilder(Integer.toHexString(get.get().getRGB()));
						while(hex.length() < 8) {
							hex.insert(0, "0");
						}
						hexVal.set(hex.toString());
					}
				}
			}
		}
		super.renderTop(ctx, mx, my, scrollX, scrollY, partialTicks);
	}
	
	@Override
	public void moveTo(float x, float y) {
		super.moveTo(x, y);
		chromaPart.moveTo(x + width - 220 , y + height + 122 - chromaPart.getHeight() - 1);
		hexValPart.moveTo(x + width - hexValPart.getWidth() - 1, y + height + 122 - hexValPart.getHeight() - 1);
	}
	
	@Override
	public void moveBy(float x, float y) {
		super.moveBy(x, y);
		chromaPart.moveBy(x, y);
		hexValPart.moveBy(x, y);
	}
	
	@Override
	public void resize(float maxW, float maxH) {
		super.resize(maxW, maxH);
		chromaPart.moveTo(x + width - 220 , y + height + 122 - chromaPart.getHeight() - 1);
		hexValPart.moveTo(x + width - hexValPart.getWidth() - 1, y + height + 122 - hexValPart.getHeight() - 1);
	}
	
	@Override
	public boolean click(double mx, double my, float scrollX, float scrollY, int button) {
		dragging = true;
		dragStartX = mx;
		dragStartY = my;
		float h = 122;
		float newY = y + height;
		float subPartScroll = 0;
		if(newY + h > mc.currentScreen.height) {
			subPartScroll = -height - h;
		}
		if(chromaPart.click(mx, my, scrollX, scrollY + subPartScroll, button)) {
			return true;
		}
		if(hexValPart.click(mx, my, scrollX, scrollY + subPartScroll, button)) {
			return true;
		}
		if(selected) {
			return true;
		}
		return super.click(mx, my, scrollX, scrollY, button);
	}
	
	@Override
	public boolean release(double mx, double my, float scrollX, float scrollY, int button) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		super.release(mx, my, scrollX, scrollY, button);
		boolean wasDragging = dragging;
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			dragging = false;
		}
		if(selected) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && (mx >= x + width - 18 && mx < x + width - 4 && my >= y + 4 && my < y + 12)) {
				reset.run();
				chroma.set(get.get().getChroma());
				StringBuilder hex = new StringBuilder(Integer.toHexString(get.get().getRGB()));
				while(hex.length() < 8) {
					hex.insert(0, "0");
				}
				hexVal.set(hex.toString());
				return true;
			}
			float w = 220;
			float h = 122;
			float newX = x + width - w;
			float newY = y + height;
			float subPartScroll = 0;
			if(newY + h > mc.currentScreen.height) {
				newY = y - h;
				subPartScroll = -height - h;
			}
			if(wasDragging) {
				if(dragStartY >= newY + 2 && dragStartY <= newY + 102) {
					// SB
					if(dragStartX >= newX + 2 && dragStartX <= newX + 103) {
						return true;
					}
					// Hue
					if(dragStartX >= newX + 106 && dragStartX <= newX + 126) {
						return true;
					}
					// Alpha
					if(dragStartX >= newX + 130 && dragStartX <= newX + 150) {
						return true;
					}
				}
			}
			if(chromaPart.release(mx, my, scrollX, scrollY + subPartScroll, button)) {
				return true;
			}
			if(hexValPart.release(mx, my, scrollX, scrollY + subPartScroll, button)) {
				return true;
			}
			if(mx >= newX && mx < newX + w && my >= newY && my < newY + h) {
				return true;
			}
			selected = false;
			return false;
		} if(mx >= x + width - 18 && mx < x + width - 4 && my >= y + 4 && my < y + 12) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				selected = true;
				return true;
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				reset.run();
				chroma.set(get.get().getChroma());
				StringBuilder hex = new StringBuilder(Integer.toHexString(get.get().getRGB()));
				while(hex.length() < 8) {
					hex.insert(0, "0");
				}
				hexVal.set(hex.toString());
				return false;
			}
		}
		return super.release(mx, my, scrollX, scrollY, button);
	}
	
	@Override
	public boolean type(int code, int scanCode, int modifiers) {
		if(selected) {
			boolean stringPartSelected = hexValPart.type(0, 0, 0);
			if(hexValPart.type(code, scanCode, modifiers)) {
				return true;
			}
			if(code == GLFW.GLFW_KEY_ESCAPE && !stringPartSelected) {
				selected = false;
				return false;
			}
			return true;
		}
		return super.type(code, scanCode, modifiers);
	}
	
	@Override
	public boolean typeChar(char c, int modifiers) {
		if(selected) {
			if(hexValPart.typeChar(c, modifiers)) {
				return true;
			}
		}
		return super.typeChar(c, modifiers);
	}
}
