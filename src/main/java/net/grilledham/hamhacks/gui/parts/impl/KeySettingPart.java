package net.grilledham.hamhacks.gui.parts.impl;

import net.grilledham.hamhacks.animation.Animation;
import net.grilledham.hamhacks.animation.AnimationBuilder;
import net.grilledham.hamhacks.animation.AnimationType;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.render.ClickGUI;
import net.grilledham.hamhacks.setting.SettingHelper;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.util.Arrays;

public class KeySettingPart extends SettingPart {
	
	private final Animation hoverAnimation = AnimationBuilder.create(AnimationType.IN_OUT_QUAD, 0.25).build();
	
	private boolean listening = false;
	
	public KeySettingPart(float x, float y, Field setting, Object obj) {
		super(x, y, MinecraftClient.getInstance().textRenderer.getWidth(SettingHelper.getName(setting).getString() + " [________________]") + 4, setting, obj);
	}
	
	@Override
	public void render(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		stack.push();
		RenderUtil.preRender();
		
		int bgC = ModuleManager.getModule(ClickGUI.class).bgColor.getRGB();
		boolean hovered = mx >= x && mx < x + width && my >= y && my < y + height;
		bgC = RenderUtil.mix(ModuleManager.getModule(ClickGUI.class).bgColorHovered.getRGB(), bgC, hoverAnimation.get());
		RenderUtil.drawRect(stack, x, y, width, height, bgC);
		
		mc.textRenderer.drawWithShadow(stack, SettingHelper.getName(setting), x + 2, y + 4, ModuleManager.getModule(ClickGUI.class).textColor.getRGB());
		try {
			String text = "[" + (listening ? (((Keybind)setting.get(obj)).getName().equals("None") ? "Listening..." : ((Keybind)setting.get(obj)).getName() + "...") : ((Keybind)setting.get(obj)).getName()) + "]";
			mc.textRenderer.drawWithShadow(stack, text, x + width - mc.textRenderer.getWidth(text) - 2, y + 4, ModuleManager.getModule(ClickGUI.class).textColor.getRGB());
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		}
		
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
			try {
				int code = button - Keybind.MOUSE_SHIFT;
				int[] codes = ((Keybind)setting.get(obj)).getKeyCombo();
				if(codes.length == 1 && codes[0] == 0) {
					((Keybind)setting.get(obj)).setKey(code);
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
					((Keybind)setting.get(obj)).setKey(codes);
				}
			} catch(IllegalAccessException e) {
				throw new RuntimeException(e);
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
		try {
			if(listening) {
				return true;
			} else if(mx >= x && mx < x + width && my >= y && my < y + height) {
				if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
					((Keybind)setting.get(obj)).setKey(0);
					listening = true;
					ModuleManager.getModule(ClickGUI.class).typing = true;
					return true;
				} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
					SettingHelper.reset(setting, obj);
				}
			}
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		}
		return super.release(mx, my, scrollX, scrollY, button);
	}
	
	@Override
	public boolean type(int code, int scanCode, int modifiers) {
		if(listening) {
			try {
				if(code == GLFW.GLFW_KEY_ESCAPE) {
					listening = false;
					ModuleManager.getModule(ClickGUI.class).typing = false;
				} else {
					int[] codes = ((Keybind)setting.get(obj)).getKeyCombo();
					if(codes.length == 1 && codes[0] == 0) {
						((Keybind)setting.get(obj)).setKey(code);
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
						((Keybind)setting.get(obj)).setKey(codes);
					}
					return true;
				}
			} catch(IllegalAccessException e) {
				e.printStackTrace();
			}
			return false;
		}
		return super.type(code, scanCode, modifiers);
	}
}
