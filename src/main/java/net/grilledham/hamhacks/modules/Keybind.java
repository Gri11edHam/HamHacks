package net.grilledham.hamhacks.modules;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
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
	private boolean wasPressed = false;
	
	public Keybind(int defaultCode) {
		this.code = defaultCode;
		this.defaultCode = defaultCode;
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if(MinecraftClient.getInstance().currentScreen == null) {
				checkKeyState();
			}
		});
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
	
	public void checkKeyState() {
		boolean pressed;
		long handle = MinecraftClient.getInstance().getWindow().getHandle();
		if(GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_F3) == GLFW.GLFW_PRESS) {
			pressed = false;
		} else {
			if(code < 0) {
				pressed = GLFW.glfwGetMouseButton(handle, code + MOUSE_SHIFT) == GLFW.GLFW_PRESS;
			} else if(code != 0) {
				pressed = GLFW.glfwGetKey(handle, code) == GLFW.GLFW_PRESS;
			} else {
				pressed = false;
			}
		}
		if(isPressed && pressed) {
			wasPressed = false;
		} else {
			wasPressed = pressed;
		}
		isPressed = pressed;
	}
	
	/**
	 * Returns <code>true</code> if the key was pressed and <code>false</code> if it wasn't or if the key is still down
	 * @return <code>true</code> or <code>false</code> depending on the key state
	 */
	public boolean wasPressed() {
		boolean toReturn = wasPressed;
		wasPressed = false;
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
