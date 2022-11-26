package net.grilledham.hamhacks.modules;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.EventManager;
import net.grilledham.hamhacks.event.events.EventClick;
import net.grilledham.hamhacks.event.events.EventKey;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Keybind {
	
	/**
	 * The amount a code is shifted by if it is a mouse button
	 */
	public static final int MOUSE_SHIFT = 100;
	
	private int[] codes;
	private final int[] defaultCodes;
	
	private boolean isPressed = false;
	private int wasPressed = 0;
	
	public Keybind(int... defaultCode) {
		if(defaultCode.length == 0) {
			defaultCode = new int[] {0};
		}
		this.codes = defaultCode;
		this.defaultCodes = defaultCode;
		EventManager.register(this);
	}
	
	@EventListener
	public void keyPressed(EventKey e) {
		checkKeyState(e.handle, e.key, e.scancode, e.action, e.modifiers, false);
	}
	
	@EventListener
	public void mouseClicked(EventClick e) {
		checkKeyState(MinecraftClient.getInstance().getWindow().getHandle(), e.button, 0, GLFW.GLFW_PRESS, 0, true);
	}
	
	/**
	 * Resets the keybind to its default key code
	 */
	public void resetKey() {
		codes = defaultCodes;
	}
	
	/**
	 * Changes the key code for this keybind
	 * @param codes The key combination
	 */
	public void setKey(int... codes) {
		if(codes.length == 0) {
			this.codes = new int[] {0};
		} else {
			this.codes = codes;
		}
	}
	
	public void checkKeyState(long handle, int key, int scancode, int action, int modifiers, boolean mouseEvent) {
		int activator = codes[codes.length - 1];
		boolean activated;
		if(mouseEvent) {
			if(activator < 0) {
				activator += MOUSE_SHIFT;
				activated = key == activator;
			} else {
				activated = false;
			}
		} else {
			activated = key == activator;
		}
		if(activated && MinecraftClient.getInstance().currentScreen == null) {
			if(GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_F3) == GLFW.GLFW_PRESS) {
				isPressed = false;
			} else {
				boolean pressed = true;
				for(int code : codes) {
					if(code < 0) {
						if(GLFW.glfwGetMouseButton(handle, code + MOUSE_SHIFT) != GLFW.GLFW_PRESS && GLFW.glfwGetMouseButton(handle, code + MOUSE_SHIFT) != GLFW.GLFW_REPEAT) {
							pressed = false;
						}
					} else if(code != 0) {
						if(GLFW.glfwGetKey(handle, code) != GLFW.GLFW_PRESS && GLFW.glfwGetKey(handle, code) != GLFW.GLFW_REPEAT) {
							pressed = false;
						}
					} else {
						pressed = false;
					}
				}
				isPressed = pressed;
			}
			if(isPressed) {
				wasPressed++;
			}
		}
	}
	
	/**
	 * Returns <code>true</code> if the key was pressed and <code>false</code> if it wasn't or if the key is still down
	 * @return <code>true</code> or <code>false</code> depending on the key state
	 */
	public boolean wasPressed() {
		boolean toReturn = wasPressed > 0;
		if(wasPressed > 0) {
			wasPressed--;
		}
		return toReturn;
	}
	
	/**
	 * Returns <code>true</code> if the key is down and <code>false</code> if it isn't
	 * @return <code>true</code> or <code>false</code> depending on the key state
	 */
	public boolean isPressed() {
		return isPressed;
	}
	
	public int[] getKeyCombo() {
		return codes;
	}
	
	public String getName() {
		StringBuilder name = new StringBuilder();
		int i = 0;
		for(int code : codes) {
			if(code < 0) {
				name.append(InputUtil.fromTranslationKey("key.mouse." + (code + MOUSE_SHIFT + 1)).getLocalizedText().getString());
			} else if(code != 0) {
				name.append(InputUtil.fromKeyCode(code, GLFW.glfwGetKeyScancode(code)).getLocalizedText().getString());
			} else {
				name.append("None");
			}
			if(i < codes.length - 1) {
				name.append("+");
			}
			i++;
		}
		return name.toString();
	}
	
	public String getCombinedString() {
		StringBuilder name = new StringBuilder();
		for(int code : codes) {
			if(code < 0) {
				name.append(InputUtil.fromTranslationKey("key.mouse." + (code + MOUSE_SHIFT + 1)).getLocalizedText().getString());
			} else if(code != 0) {
				name.append(InputUtil.fromKeyCode(code, GLFW.glfwGetKeyScancode(code)).getLocalizedText().getString());
			} else {
				name.append("None");
			}
		}
		return name.toString();
	}
}
