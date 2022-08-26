package net.grilledham.hamhacks.modules;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.EventManager;
import net.grilledham.hamhacks.event.events.EventKey;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Keybind {
	
	/**
	 * The amount a code is shifted by if it is a mouse button
	 */
	public static final int MOUSE_SHIFT = 100;
	
	private int code;
	private final int defaultCode;
	
	private boolean isPressed = false;
	private int wasPressed = 0;
	
	public Keybind(int defaultCode) {
		this.code = defaultCode;
		this.defaultCode = defaultCode;
		EventManager.register(this);
	}
	
	@EventListener
	public void keyPressed(EventKey e) {
		checkKeyState(e.handle, e.key, e.scancode, e.action, e.modifiers);
	}
	
	/**
	 * Resets the keybind to its default key code
	 */
	public void resetKey() {
		code = defaultCode;
	}
	
	/**
	 * Changes the key code for this keybind
	 * @param code The key code
	 */
	public void setKey(int code) {
		if(code < 0) {
			setKey(code + MOUSE_SHIFT, true);
		} else {
			setKey(code, false);
		}
	}
	
	/**
	 * Changes the key code for this keybind
	 * @param code The key code
	 * @param isMouseButton Should this key be treated as a mouse button
	 */
	public void setKey(int code, boolean isMouseButton) {
		if(isMouseButton) {
			this.code = code - MOUSE_SHIFT;
		} else {
			this.code = code;
		}
	}
	
	public void checkKeyState(long handle, int key, int scancode, int action, int modifiers) {
		if(key == code && MinecraftClient.getInstance().currentScreen == null) {
			if(GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_F3) == GLFW.GLFW_PRESS) {
				isPressed = false;
			} else {
				if(code < 0) {
					isPressed = GLFW.glfwGetMouseButton(handle, code + MOUSE_SHIFT) == GLFW.GLFW_PRESS;
				} else if(code != 0) {
					isPressed = action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT;
				} else {
					isPressed = false;
				}
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
	
	public int getKey() {
		return code;
	}
	
	public String getName() {
		if(code < 0) {
			return InputUtil.fromTranslationKey("key.mouse." + (code + MOUSE_SHIFT)).getLocalizedText().getString();
		} else if(code != 0) {
			return InputUtil.fromKeyCode(code, GLFW.glfwGetKeyScancode(code)).getLocalizedText().getString();
		} else {
			return "None";
		}
	}
}
