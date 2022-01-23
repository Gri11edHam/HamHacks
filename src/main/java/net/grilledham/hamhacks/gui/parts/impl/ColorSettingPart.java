package net.grilledham.hamhacks.gui.parts.impl;

import net.grilledham.hamhacks.gui.parts.GuiPart;
import net.grilledham.hamhacks.modules.render.ClickGUI;
import net.grilledham.hamhacks.util.RenderUtil;
import net.grilledham.hamhacks.util.setting.settings.BoolSetting;
import net.grilledham.hamhacks.util.setting.settings.ColorSetting;
import net.grilledham.hamhacks.util.setting.settings.StringSetting;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

import java.util.HexFormat;

public class ColorSettingPart extends GuiPart {
	
	private float hoverAnimation;
	private float selectionAnimation;
	
	private final ColorSetting setting;
	
	private final BoolSetting chroma;
	private final StringSetting hexVal;
	
	private final BoolSettingPart chromaPart;
	private final StringSettingPart hexValPart;
	
	private boolean selected = false;
	
	private boolean dragging = false;
	private double dragStartX = -1;
	private double dragStartY = -1;
	
	public ColorSettingPart(int x, int y, ColorSetting setting) {
		super(x, y, 16, 16);
		this.setting = setting;
		chroma = new BoolSetting("Chroma", setting.useChroma()) {
			@Override
			protected void valueChanged() {
				super.valueChanged();
				setting.setChroma(getValue());
			}
		};
		chromaPart = new BoolSettingPart(x, y, chroma);
		chromaPart.drawBackground = false;
		StringBuilder hex = new StringBuilder(Integer.toHexString(setting.getRGB()));
		while(hex.length() < 8) {
			hex.insert(0, "0");
		}
		hexVal = new StringSetting("", hex.toString());
		hexValPart = new StringSettingPart(x, y, hexVal) {
			@Override
			public boolean type(int code, int scanCode, int modifiers) {
				if(code == GLFW.GLFW_KEY_ENTER) {
					try {
						setting.setRGB(HexFormat.fromHexDigits(hexVal.getValue()));
						StringBuilder hex = new StringBuilder(Integer.toHexString(setting.getRGB()));
						while(hex.length() < 8) {
							hex.insert(0, "0");
						}
						hexVal.setValue(hex.toString());
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
		resize(mc.textRenderer.getWidth(setting.getName()) + 10 + 16, 16);
	}
	
	@Override
	protected void render(MatrixStack stack, int mx, int my, float partialTicks) {
		stack.push();
		RenderUtil.preRender();
		
		int bgC = ClickGUI.getInstance().bgColor.getRGB();
		RenderUtil.drawRect(stack, x, y, width, height, bgC);
		
		int outlineC = 0xffcccccc;
		RenderUtil.drawHRect(stack, x + width - 20, y + 2, 18, 12, outlineC);
		
		boolean hovered = mx >= x + width - 18 && mx < x + width - 2 && my >= y + 4 && my < y + 12;
		int boxC = RenderUtil.mix((setting.getRGB() & 0xff000000) + 0xffffff, setting.getRGB(), hoverAnimation / 4);
		RenderUtil.drawRect(stack, x + width - 18, y + 4, 14, 8, boxC);
		
		mc.textRenderer.drawWithShadow(stack, setting.getName(), x + 2, y + 4, ClickGUI.getInstance().textColor.getRGB());
		
		RenderUtil.postRender();
		stack.pop();
		
		if(hovered) {
			hoverAnimation += partialTicks / 5;
		} else {
			hoverAnimation -= partialTicks / 5;
		}
		hoverAnimation = Math.min(1, Math.max(0, hoverAnimation));
		if(selected) {
			selectionAnimation += partialTicks / 5;
		} else {
			selectionAnimation -= partialTicks / 5;
		}
		selectionAnimation = Math.min(1, Math.max(0, selectionAnimation));
	}
	
	@Override
	protected void renderTop(MatrixStack stack, int mx, int my, float partialTicks) {
		stack.push();
		float w = 220;
		float h = 122;
		float x = this.x + width - w;
		float y = this.y + height;
		
		RenderUtil.pushScissor(x - 1 + ((w + 2) * (1 - selectionAnimation)), y - 1, (w + 2) * selectionAnimation, (h + 2) * selectionAnimation, ClickGUI.getInstance().scale.getValue());
		RenderUtil.applyScissor();
		RenderUtil.preRender();
		
		RenderUtil.drawRect(stack, x, y, w, h, 0xff202020);
		RenderUtil.drawHRect(stack, x - 1, y - 1, w + 2, h + 2, 0xffa0a0a0);
		
		RenderUtil.drawHRect(stack, x + 1, y + 1, 103, 103, 0xffa0a0a0);
		RenderUtil.drawSBGradient(stack, x + 2, y + 2, 101, 101, setting.getHue());
		
		RenderUtil.drawHRect(stack, x + 105, y + 1, 22, 103, 0xffa0a0a0);
		RenderUtil.drawHueGradient(stack, x + 106, y + 2, 20, 101);
		
		RenderUtil.drawHRect(stack, x + 129, y + 1, 22, 103, 0xffa0a0a0);
		RenderUtil.drawAlphaGradient(stack, x + 130, y + 2, 20, 101);
		
		RenderUtil.drawHRect(stack, x + 1 + (100 * setting.getSaturation()), y + 1 + (100 * (1 - setting.getBrightness())), 3, 3, 0xffa0a0a0);
		RenderUtil.drawHRect(stack, x + 105, y + 1 + (100 * (setting.getHue())), 22, 3, 0xffa0a0a0);
		RenderUtil.drawHRect(stack, x + 129, y + 1 + (100 * (1 - setting.getAlpha())), 22, 3, 0xffa0a0a0);
		
		chromaPart.draw(stack, mx, my, partialTicks);
		if(selectionAnimation > 0.8f) {
			hexValPart.draw(stack, mx, my, partialTicks);
		}
		
		RenderUtil.popScissor();
		RenderUtil.postRender();
		stack.pop();
		
		if(selected) {
			if(dragging) {
				if(dragStartY >= y + 2 && dragStartY <= y + 102) {
					// SB
					if(dragStartX >= x + 2 && dragStartX <= x + 103) {
						float newS = (mx - (x + 1)) / 100f;
						float newB = 1 - ((my - (y + 1)) / 100f);
						newS = Math.min(Math.max(newS, 0), 1);
						newB = Math.min(Math.max(newB, 0), 1);
						setting.setSaturation(newS);
						setting.setBrightness(newB);
						StringBuilder hex = new StringBuilder(Integer.toHexString(setting.getRGB()));
						while(hex.length() < 8) {
							hex.insert(0, "0");
						}
						hexVal.setValue(hex.toString());
					}
					
					// Hue
					if(dragStartX >= x + 106 && dragStartX <= x + 126) {
						float newH = (my - (y + 1)) / 100f;
						newH = Math.min(Math.max(newH, 0), 1);
						setting.setHue(newH);
						StringBuilder hex = new StringBuilder(Integer.toHexString(setting.getRGB()));
						while(hex.length() < 8) {
							hex.insert(0, "0");
						}
						hexVal.setValue(hex.toString());
					}
					
					// Alpha
					if(dragStartX >= x + 130 && dragStartX <= x + 150) {
						float newA = 1 - ((my - (y + 1)) / 100f);
						newA = Math.min(Math.max(newA, 0), 1);
						setting.setAlpha(newA);
						StringBuilder hex = new StringBuilder(Integer.toHexString(setting.getRGB()));
						while(hex.length() < 8) {
							hex.insert(0, "0");
						}
						hexVal.setValue(hex.toString());
					}
				}
			}
		}
	}
	
	@Override
	public void moveTo(int x, int y) {
		super.moveTo(x, y);
		chromaPart.moveTo(x + width - 220 , y + height + 122 - chromaPart.getHeight() - 1);
		hexValPart.moveTo(x + width - hexValPart.getWidth() - 1, y + height + 122 - hexValPart.getHeight() - 1);
	}
	
	@Override
	public void moveBy(int x, int y) {
		super.moveBy(x, y);
		chromaPart.moveBy(x, y);
		hexValPart.moveBy(x, y);
	}
	
	@Override
	public void resize(int maxW, int maxH) {
		super.resize(maxW, maxH);
		chromaPart.moveTo(x + width - 220 , y + height + 122 - chromaPart.getHeight() - 1);
		hexValPart.moveTo(x + width - hexValPart.getWidth() - 1, y + height + 122 - hexValPart.getHeight() - 1);
	}
	
	@Override
	public boolean click(double mx, double my, int button) {
		dragging = true;
		dragStartX = mx;
		dragStartY = my;
		if(chromaPart.click(mx, my, button)) {
			return true;
		}
		if(hexValPart.click(mx, my, button)) {
			return true;
		}
		if(selected) {
			return true;
		}
		return super.click(mx, my, button);
	}
	
	@Override
	public boolean release(double mx, double my, int button) {
		boolean wasDragging = dragging;
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			dragging = false;
		}
		if(selected) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && (mx >= x + width - 18 && mx < x + width - 4 && my >= y + 4 && my < y + 12)) {
				setting.reset();
				chroma.setValue(setting.useChroma());
				StringBuilder hex = new StringBuilder(Integer.toHexString(setting.getRGB()));
				while(hex.length() < 8) {
					hex.insert(0, "0");
				}
				hexVal.setValue(hex.toString());
				return true;
			}
			float w = 220;
			float h = 122;
			float x = this.x + width - w;
			float y = this.y + height;
			if(wasDragging) {
				if(dragStartY >= y + 2 && dragStartY <= y + 102) {
					// SB
					if(dragStartX >= x + 2 && dragStartX <= x + 103) {
						return true;
					}
					// Hue
					if(dragStartX >= x + 106 && dragStartX <= x + 126) {
						return true;
					}
					// Alpha
					if(dragStartX >= x + 130 && dragStartX <= x + 150) {
						return true;
					}
				}
			}
			if(chromaPart.release(mx, my, button)) {
				return true;
			}
			if(hexValPart.release(mx, my, button)) {
				return true;
			}
			if(mx >= x && mx < x + w && my >= y && my < y + h) {
				return true;
			}
			selected = false;
			return true;
		} if(mx >= x + width - 18 && mx < x + width - 4 && my >= y + 4 && my < y + 12) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				selected = true;
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				setting.reset();
				chroma.setValue(setting.useChroma());
				StringBuilder hex = new StringBuilder(Integer.toHexString(setting.getRGB()));
				while(hex.length() < 8) {
					hex.insert(0, "0");
				}
				hexVal.setValue(hex.toString());
			}
			return true;
		}
		return super.release(mx, my, button);
	}
	
	@Override
	public boolean type(int code, int scanCode, int modifiers) {
		if(selected) {
			if(hexValPart.type(code, scanCode, modifiers)) {
				return true;
			}
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
