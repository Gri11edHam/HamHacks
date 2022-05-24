package net.grilledham.hamhacks.gui.parts.impl;

import net.grilledham.hamhacks.modules.render.ClickGUI;
import net.grilledham.hamhacks.util.RenderUtil;
import net.grilledham.hamhacks.util.setting.settings.StringSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

public class StringSettingPart extends SettingPart {
	
	private float cursorAnimation = 0;
	private boolean cursorShown = false;
	private int cursorPos;
	
	private int stringScroll;
	
	private int selectionStart = -1;
	private int selectionEnd = -1;
	
	private boolean dragging = false;
	private int dragStartX = 0;
	
	private boolean selected = false;
	
	private final StringSetting setting;
	
	protected boolean drawBackground = true;
	
	public StringSettingPart(int x, int y, StringSetting setting) {
		super(x, y, MinecraftClient.getInstance().textRenderer.getWidth(setting.getName()) + 106, setting);
		this.setting = setting;
		cursorPos = setting.getValue().length();
		stringScroll = cursorPos;
	}
	
	@Override
	protected void render(MatrixStack stack, int mx, int my, float partialTicks) {
		selectionStart = Math.min(Math.max(selectionStart, -1), setting.getValue().length());
		selectionEnd = Math.min(Math.max(selectionEnd, selectionStart), setting.getValue().length());
		cursorPos = Math.min(Math.max(cursorPos, 0), setting.getValue().length());
		if(!selected) {
			stringScroll = setting.getValue().length();
		}
		stringScroll = Math.min(Math.max(stringScroll, 0), setting.getValue().length());
		stack.push();
		RenderUtil.preRender();
		
		if(drawBackground) {
			int bgC = ClickGUI.getInstance().bgColor.getRGB();
			RenderUtil.drawRect(stack, x, y, width, height, bgC);
		}
		
		int outlineC = 0xffcccccc;
		RenderUtil.drawHRect(stack, x + width - 104, y, 104, height, outlineC);
		
		mc.textRenderer.drawWithShadow(stack, setting.getName(), x + 2, y + 4, ClickGUI.getInstance().textColor.getRGB());
		
		RenderUtil.adjustScissor(x + width - 102, y, 100, height, ClickGUI.getInstance().scale.getValue());
		RenderUtil.applyScissor();
		
		mc.textRenderer.drawWithShadow(stack, setting.getValue(), x + width - mc.textRenderer.getWidth(setting.getValue()) - 2 + mc.textRenderer.getWidth(setting.getValue().substring(stringScroll)), y + 4, ClickGUI.getInstance().textColor.getRGB());
		
		RenderUtil.preRender();
		
		if(selectionStart > -1) {
			int selectionColor = 0x804040c0;
			RenderUtil.drawRect(stack, x + width - mc.textRenderer.getWidth(setting.getValue().substring(selectionStart)) - 3 + mc.textRenderer.getWidth(setting.getValue().substring(stringScroll)), y + 3, mc.textRenderer.getWidth(setting.getValue().substring(selectionStart, selectionEnd)), mc.textRenderer.fontHeight + 1, selectionColor);
		}
		
		RenderUtil.popScissor();
		
		int cursorColor = RenderUtil.mix(ClickGUI.getInstance().textColor.getRGB(), ClickGUI.getInstance().textColor.getRGB() & 0xffffff, cursorAnimation);
		RenderUtil.drawRect(stack, x + width - mc.textRenderer.getWidth(setting.getValue().substring(cursorPos)) - 3 + mc.textRenderer.getWidth(setting.getValue().substring(stringScroll)), y + 3, 1, mc.textRenderer.fontHeight + 1, cursorColor);
		
		RenderUtil.postRender();
		stack.pop();
		
		if(dragging && selected) {
			if(dragStartX != mx) {
				String currentInput = setting.getValue();
				float stringX = x + width - mc.textRenderer.getWidth(setting.getValue()) - 2 + mc.textRenderer.getWidth(setting.getValue().substring(stringScroll));
				cursorPos = Math.min(Math.max(Math.round((mx - stringX) / (MinecraftClient.getInstance().textRenderer.getWidth(currentInput) / (float)currentInput.length())), 0), currentInput.length());
				if(mx < x + width - 102) {
					stringScroll -= partialTicks;
				} else if(mx > x + width - 2) {
					stringScroll += partialTicks;
				}
				stringScroll = Math.min(Math.max(stringScroll, 0), currentInput.length());
				selectionEnd = cursorPos;
				
				selectionStart = Math.min(Math.max(Math.round((dragStartX - stringX) / (MinecraftClient.getInstance().textRenderer.getWidth(currentInput) / (float)currentInput.length())), 0), currentInput.length());
			}
		}
		
		if(cursorShown) {
			cursorAnimation += partialTicks / 10;
		} else {
			cursorAnimation -= partialTicks / 10;
		}
		cursorAnimation = Math.min(1, Math.max(0, cursorAnimation));
		if(cursorAnimation >= 1) {
			cursorShown = false;
		} else if(cursorAnimation <= 0) {
			cursorShown = true;
		}
		cursorShown = cursorShown && selected;
	}
	
	@Override
	public boolean click(double mx, double my, int button) {
		if(selected) {
			if(mx >= x + width - 104 && mx < x + width && my >= y && my < y + height) {
				if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
					selectionStart = -1;
					selectionEnd = -1;
					dragging = true;
					dragStartX = (int)mx;
				}
			} else {
				selectionStart = -1;
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
		if(mx >= x + width - 104 && mx < x + width && my >= y && my < y + height) {
			String currentInput = setting.getValue();
			float stringX = x + width - mc.textRenderer.getWidth(setting.getValue()) - 2 + mc.textRenderer.getWidth(setting.getValue().substring(stringScroll));
			cursorPos = (int)Math.min(Math.max(Math.round((mx - stringX) / (MinecraftClient.getInstance().textRenderer.getWidth(currentInput) / (float)currentInput.length())), 0), currentInput.length());
			if(mx < x + width - 102) {
				stringScroll--;
			} else if(mx > x + width - 2) {
				stringScroll++;
			}
			stringScroll = Math.min(Math.max(stringScroll, 0), currentInput.length());
			selected = true;
			return true;
		} else if(selected && selectionStart <= -1) {
			selected = false;
			return true;
		} else if(selected) {
			return true;
		}
		return super.release(mx, my, button);
	}
	
	@Override
	public boolean type(int code, int scanCode, int modifiers) {
		if(selected) {
			switch(code) {
				case GLFW.GLFW_KEY_ESCAPE -> selected = false;
				case GLFW.GLFW_KEY_BACKSPACE -> {
					if(selectionStart > -1) {
						String first = setting.getValue().substring(0, selectionStart);
						String last = setting.getValue().substring(selectionEnd);
						selectionStart = -1;
						cursorPos = selectionEnd;
						setting.setValue(first + last);
						break;
					}
					if(cursorPos - 1 < 0) {
						break;
					}
					StringBuilder first = new StringBuilder(setting.getValue().substring(0, cursorPos - 1));
					String last = setting.getValue().substring(cursorPos);
					if(modifiers == 2) {
						String[] firstWords = setting.getValue().substring(0, cursorPos).split(" ");
						first = new StringBuilder();
						for(int i = 0; i < firstWords.length - 1; i++) {
							first.append(firstWords[i]).append(" ");
						}
						cursorPos -= setting.getValue().length() - (first.length() + last.length());
					}
					setting.setValue(first.toString().trim() + last);
					cursorPos--;
					stringScroll++;
				}
				case GLFW.GLFW_KEY_DELETE -> {
					if(selectionStart > -1) {
						String first = setting.getValue().substring(0, selectionStart);
						String last = setting.getValue().substring(selectionEnd);
						selectionStart = -1;
						cursorPos = selectionEnd;
						setting.setValue(first + last);
						break;
					}
					if(cursorPos + 1 > setting.getValue().length()) {
						break;
					}
					String first = setting.getValue().substring(0, cursorPos);
					StringBuilder last = new StringBuilder(setting.getValue().substring(cursorPos + 1));
					if(modifiers == 2) {
						String[] lastWords = setting.getValue().substring(cursorPos).split(" ");
						last = new StringBuilder();
						for(int i = 1; i < lastWords.length; i++) {
							last.append(" ").append(lastWords[i]);
						}
					}
					setting.setValue(first + last.toString().trim());
				}
				case GLFW.GLFW_KEY_LEFT -> {
					if(modifiers == 1) {
						if(selectionStart <= -1) {
							selectionEnd = cursorPos;
						}
						cursorPos--;
						if(cursorPos > -1) {
							selectionStart = cursorPos;
						}
						break;
					}
					if(selectionStart > -1) {
						cursorPos = selectionStart;
						selectionStart = -1;
					}
					if(modifiers == 2) {
						String first = setting.getValue().substring(0, cursorPos).trim();
						cursorPos = first.lastIndexOf(" ");
						break;
					}
					cursorPos--;
				}
				case GLFW.GLFW_KEY_RIGHT -> {
					if(modifiers == 1) {
						if(selectionStart <= -1) {
							selectionStart = cursorPos;
						}
						cursorPos++;
						selectionEnd = cursorPos;
						break;
					}
					if(selectionStart > -1) {
						cursorPos = selectionEnd;
						selectionStart = -1;
					}
					if(modifiers == 2) {
						String first = setting.getValue().substring(0, cursorPos);
						String last = setting.getValue().substring(cursorPos).trim() + " ";
						cursorPos = first.length() + last.indexOf(" ") + 1;
						break;
					}
					cursorPos++;
				}
				case GLFW.GLFW_KEY_HOME -> cursorPos = 0;
				case GLFW.GLFW_KEY_END -> cursorPos = setting.getValue().length();
				case GLFW.GLFW_KEY_A -> {
					if(modifiers == 2) {
						selectionStart = 0;
						selectionEnd = setting.getValue().length();
						cursorPos = selectionEnd;
					}
				}
				case GLFW.GLFW_KEY_C -> {
					if(modifiers == 2) {
						if(selectionStart > -1) {
							mc.keyboard.setClipboard(setting.getValue().substring(selectionStart, selectionEnd));
						}
					}
				}
				case GLFW.GLFW_KEY_X -> {
					if(modifiers == 2) {
						if(selectionStart > -1) {
							mc.keyboard.setClipboard(setting.getValue().substring(selectionStart, selectionEnd));
							
							cursorPos = selectionStart;
							String first = setting.getValue().substring(0, selectionStart);
							String last = setting.getValue().substring(selectionEnd);
							selectionStart = -1;
							setting.setValue(first + last);
						}
					}
				}
				case GLFW.GLFW_KEY_V -> {
					if(modifiers == 2) {
						if(selectionStart > -1) {
							cursorPos = selectionStart;
							String first = setting.getValue().substring(0, selectionStart);
							String last = setting.getValue().substring(selectionEnd);
							selectionStart = -1;
							setting.setValue(first + last);
						}
						String clipboard = String.valueOf(mc.keyboard.getClipboard());
						String first = setting.getValue().substring(0, cursorPos);
						String last = setting.getValue().substring(cursorPos);
						setting.setValue(first + clipboard + last);
						cursorPos += clipboard.length();
					}
				}
			}
			cursorPos = Math.min(Math.max(cursorPos, 0), setting.getValue().length());
			selectionStart = Math.min(Math.max(selectionStart, -1), setting.getValue().length());
			selectionEnd = Math.min(Math.max(selectionEnd, selectionStart), setting.getValue().length());
			stringScroll = Math.min(Math.max(stringScroll, 0), setting.getValue().length());
			float cursorX = x + width - mc.textRenderer.getWidth(setting.getValue().substring(cursorPos)) - 3 + mc.textRenderer.getWidth(setting.getValue().substring(stringScroll));
			while(cursorX < x + width - 102) {
				stringScroll--;
				stringScroll = Math.min(Math.max(stringScroll, 0), setting.getValue().length());
				cursorX = x + width - mc.textRenderer.getWidth(setting.getValue().substring(cursorPos)) - 3 + mc.textRenderer.getWidth(setting.getValue().substring(stringScroll));
			}
			while(cursorX > x + width - 2) {
				stringScroll++;
				stringScroll = Math.min(Math.max(stringScroll, 0), setting.getValue().length());
				cursorX = x + width - mc.textRenderer.getWidth(setting.getValue().substring(cursorPos)) - 3 + mc.textRenderer.getWidth(setting.getValue().substring(stringScroll));
			}
			stringScroll = Math.min(Math.max(stringScroll, 0), setting.getValue().length());
			cursorShown = true;
			cursorAnimation = 1;
			return true;
		}
		return super.type(code, scanCode, modifiers);
	}
	
	@Override
	public boolean typeChar(char c, int modifiers) {
		if(selected) {
			if(selectionStart > -1) {
				String first = setting.getValue().substring(0, selectionStart);
				String last = setting.getValue().substring(selectionEnd);
				cursorPos = selectionStart;
				selectionStart = -1;
				setting.setValue(first + last);
			}
			String keyChar = String.valueOf(c);
			String first = setting.getValue().substring(0, cursorPos);
			String last = setting.getValue().substring(cursorPos);
			setting.setValue(first + keyChar + last);
			cursorPos++;
			stringScroll++;
			cursorShown = true;
			cursorAnimation = 1;
			return true;
		}
		return super.typeChar(c, modifiers);
	}
}
