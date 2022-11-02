package net.grilledham.hamhacks.gui.element.impl;

import net.grilledham.hamhacks.animation.Animation;
import net.grilledham.hamhacks.animation.AnimationBuilder;
import net.grilledham.hamhacks.animation.AnimationType;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.grilledham.hamhacks.setting.SettingHelper;
import net.grilledham.hamhacks.setting.StringSetting;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;

public class StringSettingElement extends SettingElement {
	
	private final Animation cursorAnimation = AnimationBuilder.create(AnimationType.IN_OUT_QUAD, 0.5, true).build();
	private boolean cursorShown = false;
	private int cursorPos;
	
	private int stringScroll;
	
	private int selectionStart = -1;
	private int selectionEnd = -1;
	
	private boolean dragging = false;
	private int dragStartX = 0;
	
	private boolean selected = false;
	
	protected boolean drawBackground = true;
	
	@StringSetting(name = "")
	public String internalSetting;
	
	public StringSettingElement(float x, float y, float scale, Field setting, Object obj) {
		super(x, y, MinecraftClient.getInstance().textRenderer.getWidth(SettingHelper.getName(setting).getString()) + 106, scale, setting, obj);
		cursorPos = getValue().length();
		stringScroll = cursorPos;
	}
	
	public StringSettingElement(float x, float y, float scale, String setting) {
		super(x, y, 106, scale, null, null);
		this.internalSetting = setting;
		try {
			Field settingField = SettingElement.class.getDeclaredField("setting");
			settingField.setAccessible(true);
			settingField.set(this, getClass().getField("internalSetting"));
			settingField.setAccessible(false);
			
			Field objField = SettingElement.class.getDeclaredField("obj");
			objField.setAccessible(true);
			objField.set(this, this);
			objField.setAccessible(false);
		} catch(NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		cursorPos = setting.length();
		stringScroll = cursorPos;
	}
	
	@Override
	public void render(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		selectionStart = Math.min(Math.max(selectionStart, -1), getValue().length());
		selectionEnd = Math.min(Math.max(selectionEnd, -1), getValue().length());
		cursorPos = Math.min(Math.max(cursorPos, 0), getValue().length());
		if(!selected) {
			stringScroll = getValue().length();
		}
		stringScroll = Math.min(Math.max(stringScroll, 0), getValue().length());
		stack.push();
		RenderUtil.preRender();
		
		ClickGUI ui = PageManager.getPage(ClickGUI.class);
		if(drawBackground) {
			int bgC = ui.bgColor.getRGB();
			RenderUtil.drawRect(stack, x, y, width, height, bgC);
		}
		
		int outlineC = 0xffcccccc;
		RenderUtil.drawHRect(stack, x + width - 104, y, 104, height, outlineC);
		
		mc.textRenderer.drawWithShadow(stack, SettingHelper.getName(setting), x + 2, y + 4, ui.textColor.getRGB());
		
		RenderUtil.adjustScissor(x + width - 102, y, 100, height, scale);
		RenderUtil.applyScissor();
		
		if(getValue() == null || getValue().equals("")) {
			String value = setting.getAnnotation(StringSetting.class).placeholder();
			mc.textRenderer.drawWithShadow(stack, value, x + width - mc.textRenderer.getWidth(value) - 2, y + 4, RenderUtil.mix(ui.bgColor.getRGB(), ui.textColor.getRGB(), 0.75f));
		} else {
			mc.textRenderer.drawWithShadow(stack, getValue(), x + width - mc.textRenderer.getWidth(getValue()) - 2 + mc.textRenderer.getWidth(getValue().substring(stringScroll)), y + 4, ui.textColor.getRGB());
		}
		
		RenderUtil.preRender();
		
		if(selectionStart > -1) {
			int selectionColor = 0x804040c0;
			RenderUtil.drawRect(stack, x + width - mc.textRenderer.getWidth(getValue().substring(getSelectionStart())) - 3 + mc.textRenderer.getWidth(getValue().substring(stringScroll)), y + 3, mc.textRenderer.getWidth(getValue().substring(getSelectionStart(), getSelectionEnd())), mc.textRenderer.fontHeight + 1, selectionColor);
		}
		
		RenderUtil.popScissor();
		
		int cursorColor = RenderUtil.mix(ui.textColor.getRGB(), ui.textColor.getRGB() & 0xffffff, cursorAnimation.get());
		RenderUtil.drawRect(stack, x + width - mc.textRenderer.getWidth(getValue().substring(cursorPos)) - 3 + mc.textRenderer.getWidth(getValue().substring(stringScroll)), y + 3, 1, mc.textRenderer.fontHeight + 1, cursorColor);
		
		RenderUtil.postRender();
		stack.pop();
		
		if(dragging && selected) {
			if(dragStartX != mx) {
				String currentInput = getValue();
				float stringX = x + width - mc.textRenderer.getWidth(getValue()) - 2 + mc.textRenderer.getWidth(getValue().substring(stringScroll));
				cursorPos = Math.min(Math.max(Math.round((mx - stringX) / (MinecraftClient.getInstance().textRenderer.getWidth(currentInput) / (float)currentInput.length())), 0), currentInput.length());
				if(mx < x + width - 102) {
					stringScroll -= partialTicks;
				} else if(mx > x + width - 2) {
					stringScroll += partialTicks;
				}
				stringScroll = Math.min(Math.max(stringScroll, 0), currentInput.length());
				
				int start = Math.min(Math.max(Math.round((dragStartX - stringX) / (MinecraftClient.getInstance().textRenderer.getWidth(currentInput) / (float)currentInput.length())), 0), currentInput.length());
				if(cursorPos < start) {
					selectionEnd = start;
					selectionStart = cursorPos;
				} else {
					selectionEnd = cursorPos;
					selectionStart = start;
				}
			}
		}
		
		cursorAnimation.set(cursorShown);
		cursorAnimation.update();
		if(cursorAnimation.get() >= 1) {
			cursorShown = false;
		} else if(cursorAnimation.get() <= 0) {
			cursorShown = true;
		}
		cursorShown = cursorShown && selected;
	}
	
	@Override
	public boolean click(double mx, double my, float scrollX, float scrollY, int button) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
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
		return super.click(mx, my, scrollX, scrollY, button);
	}
	
	@Override
	public boolean release(double mx, double my, float scrollX, float scrollY, int button) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		super.release(mx, my, scrollX, scrollY, button);
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			dragging = false;
		}
		if(mx >= x + width - 104 && mx < x + width && my >= y && my < y + height) {
			String currentInput = getValue();
			float stringX = x + width - mc.textRenderer.getWidth(getValue()) - 2 + mc.textRenderer.getWidth(getValue().substring(stringScroll));
			cursorPos = (int)Math.min(Math.max(Math.round((mx - stringX) / (MinecraftClient.getInstance().textRenderer.getWidth(currentInput) / (float)currentInput.length())), 0), currentInput.length());
			if(mx < x + width - 102) {
				stringScroll--;
			} else if(mx > x + width - 2) {
				stringScroll++;
			}
			stringScroll = Math.min(Math.max(stringScroll, 0), currentInput.length());
			selected = true;
			PageManager.getPage(ClickGUI.class).typing = true;
			return true;
		} else if(selected && selectionStart <= -1) {
			selected = false;
			PageManager.getPage(ClickGUI.class).typing = false;
			return false;
		} else if(selected) {
			return true;
		}
		return super.release(mx, my, scrollX, scrollY, button);
	}
	
	@Override
	public boolean type(int code, int scanCode, int modifiers) {
		if(selected) {
			switch(code) {
				case GLFW.GLFW_KEY_ESCAPE, GLFW.GLFW_KEY_ENTER -> {
					selected = false;
					PageManager.getPage(ClickGUI.class).typing = false;
				}
				case GLFW.GLFW_KEY_BACKSPACE -> {
					if(selectionStart > -1) {
						String first = getValue().substring(0, getSelectionStart());
						String last = getValue().substring(getSelectionEnd());
						cursorPos = getSelectionStart();
						selectionStart = -1;
						updateValue(first + last);
						break;
					}
					if(cursorPos - 1 < 0) {
						break;
					}
					StringBuilder first = new StringBuilder(getValue().substring(0, cursorPos - 1));
					String last = getValue().substring(cursorPos);
					if(modifiers == 2) {
						String[] firstWords = getValue().substring(0, cursorPos).split(" ");
						first = new StringBuilder();
						for(int i = 0; i < firstWords.length - 1; i++) {
							first.append(firstWords[i]).append(" ");
						}
						cursorPos -= getValue().length() - (first.length() + last.length());
					}
					updateValue(first.toString().trim() + last);
					cursorPos--;
					stringScroll++;
				}
				case GLFW.GLFW_KEY_DELETE -> {
					if(selectionStart > -1) {
						String first = getValue().substring(0, getSelectionStart());
						String last = getValue().substring(getSelectionEnd());
						cursorPos = getSelectionStart();
						selectionStart = -1;
						updateValue(first + last);
						break;
					}
					if(cursorPos + 1 > getValue().length()) {
						break;
					}
					String first = getValue().substring(0, cursorPos);
					StringBuilder last = new StringBuilder(getValue().substring(cursorPos + 1));
					if(modifiers == 2) {
						String[] lastWords = getValue().substring(cursorPos).split(" ");
						last = new StringBuilder();
						for(int i = 1; i < lastWords.length; i++) {
							last.append(" ").append(lastWords[i]);
						}
					}
					updateValue(first + last.toString().trim());
				}
				case GLFW.GLFW_KEY_LEFT -> {
					if((modifiers & 1) == 1) {
						if(selectionStart <= -1) {
							selectionEnd = cursorPos;
							selectionStart = cursorPos;
						}
						if((modifiers & 2) == 2) {
							String first = getValue().substring(0, cursorPos).trim();
							cursorPos = first.lastIndexOf(" ");
						} else {
							cursorPos--;
						}
						if(cursorPos <= -1) {
							cursorPos = 0;
						}
						selectionEnd = cursorPos;
						break;
					}
					if(selectionStart > -1) {
						cursorPos = getSelectionStart();
						selectionStart = -1;
						break;
					}
					if(modifiers == 2) {
						String first = getValue().substring(0, cursorPos).trim();
						cursorPos = first.lastIndexOf(" ");
						break;
					}
					cursorPos--;
				}
				case GLFW.GLFW_KEY_RIGHT -> {
					if((modifiers & 1) == 1) {
						if(selectionStart <= -1) {
							selectionStart = cursorPos;
							selectionEnd = cursorPos;
						}
						if((modifiers & 2) == 2) {
							String first = getValue().substring(0, cursorPos);
							String last = getValue().substring(cursorPos).trim() + " ";
							cursorPos = first.length() + last.indexOf(" ") + 1;
						} else {
							cursorPos++;
						}
						selectionEnd = cursorPos;
						break;
					}
					if(selectionStart > -1) {
						cursorPos = getSelectionEnd();
						selectionStart = -1;
						break;
					}
					if(modifiers == 2) {
						String first = getValue().substring(0, cursorPos);
						String last = getValue().substring(cursorPos).trim() + " ";
						cursorPos = first.length() + last.indexOf(" ") + 1;
						break;
					}
					cursorPos++;
				}
				case GLFW.GLFW_KEY_HOME -> cursorPos = 0;
				case GLFW.GLFW_KEY_END -> cursorPos = getValue().length();
				case GLFW.GLFW_KEY_A -> {
					if(modifiers == 2) {
						selectionStart = 0;
						selectionEnd = getValue().length();
						cursorPos = getSelectionEnd();
					}
				}
				case GLFW.GLFW_KEY_C -> {
					if(modifiers == 2) {
						if(selectionStart > -1) {
							mc.keyboard.setClipboard(getValue().substring(getSelectionStart(), getSelectionEnd()));
						}
					}
				}
				case GLFW.GLFW_KEY_X -> {
					if(modifiers == 2) {
						if(selectionStart > -1) {
							mc.keyboard.setClipboard(getValue().substring(getSelectionStart(), getSelectionEnd()));
							
							cursorPos = getSelectionStart();
							String first = getValue().substring(0, getSelectionStart());
							String last = getValue().substring(getSelectionEnd());
							selectionStart = -1;
							updateValue(first + last);
						}
					}
				}
				case GLFW.GLFW_KEY_V -> {
					if(modifiers == 2) {
						if(selectionStart > -1) {
							cursorPos = getSelectionStart();
							String first = getValue().substring(0, getSelectionStart());
							String last = getValue().substring(getSelectionEnd());
							selectionStart = -1;
							updateValue(first + last);
						}
						String clipboard = String.valueOf(mc.keyboard.getClipboard());
						String first = getValue().substring(0, cursorPos);
						String last = getValue().substring(cursorPos);
						updateValue(first + clipboard + last);
						cursorPos += clipboard.length();
					}
				}
			}
			if(selectionStart == selectionEnd) {
				selectionStart = -1;
			}
			cursorPos = Math.min(Math.max(cursorPos, 0), getValue().length());
			selectionStart = Math.min(Math.max(selectionStart, -1), getValue().length());
			selectionEnd = Math.min(Math.max(selectionEnd, -1), getValue().length());
			stringScroll = Math.min(Math.max(stringScroll, 0), getValue().length());
			float cursorX = x + width - mc.textRenderer.getWidth(getValue().substring(cursorPos)) - 3 + mc.textRenderer.getWidth(getValue().substring(stringScroll));
			while(cursorX < x + width - 102) {
				stringScroll--;
				stringScroll = Math.min(Math.max(stringScroll, 0), getValue().length());
				cursorX = x + width - mc.textRenderer.getWidth(getValue().substring(cursorPos)) - 3 + mc.textRenderer.getWidth(getValue().substring(stringScroll));
			}
			while(cursorX > x + width - 2) {
				stringScroll++;
				stringScroll = Math.min(Math.max(stringScroll, 0), getValue().length());
				cursorX = x + width - mc.textRenderer.getWidth(getValue().substring(cursorPos)) - 3 + mc.textRenderer.getWidth(getValue().substring(stringScroll));
			}
			stringScroll = Math.min(Math.max(stringScroll, 0), getValue().length());
			cursorShown = true;
			cursorAnimation.setAbsolute(1);
			return selected;
		}
		return super.type(code, scanCode, modifiers);
	}
	
	@Override
	public boolean typeChar(char c, int modifiers) {
		if(selected) {
			if(selectionStart > -1) {
				String first = getValue().substring(0, getSelectionStart());
				String last = getValue().substring(getSelectionEnd());
				cursorPos = getSelectionStart();
				selectionStart = -1;
				updateValue(first + last);
			}
			String keyChar = String.valueOf(c);
			String first = getValue().substring(0, cursorPos);
			String last = getValue().substring(cursorPos);
			updateValue(first + keyChar + last);
			cursorPos++;
			stringScroll++;
			cursorShown = true;
			cursorAnimation.setAbsolute(1);
			return selected;
		}
		return super.typeChar(c, modifiers);
	}
	
	private int getSelectionStart() {
		return Math.min(selectionStart, selectionEnd);
	}
	
	private int getSelectionEnd() {
		return Math.max(selectionStart, selectionEnd);
	}
	
	public void updateValue(String value) {
		try {
			setting.set(obj, value);
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public String getValue() {
		try {
			return (String)setting.get(obj);
		} catch(IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
}
