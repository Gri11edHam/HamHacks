package net.grilledham.hamhacks.gui.element;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public abstract class GuiElement {
	
	protected final MinecraftClient mc = MinecraftClient.getInstance();
	
	protected float x;
	protected float y;
	protected float width;
	protected float height;
	
	protected final float preferredWidth;
	protected final float preferredHeight;
	
	protected final float scale;
	
	public GuiElement(float x, float y, float width, float height, float scale) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.preferredWidth = width;
		this.preferredHeight = height;
		this.scale = scale;
	}
	
	public void moveTo(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void moveBy(float x, float y) {
		this.x += x;
		this.y += y;
	}
	
	public void resize(float width, float height) {
		this.width = width;
		this.height = height;
	}
	
	public abstract void render(MatrixStack stack, int mx, int my, float offX, float offY, float tickDelta);
	
	public void renderTop(MatrixStack stack, int mx, int my, float offX, float offY, float tickDelta) {}
	
	public boolean click(double mx, double my, float offX, float offY, int button) {
		return false;
	}
	
	public boolean release(double mx, double my, float offX, float offY, int button) {
		return false;
	}
	
	public boolean drag(double mx, double my, float offX, float offY, int button, double dx, double dy) {
		return false;
	}
	
	public boolean scroll(double mx, double my, float offX, float offY, double delta) {
		return false;
	}
	
	public boolean type(int code, int scanCode, int modifiers) {
		return false;
	}
	
	public boolean typeChar(char c, int modifiers) {
		return false;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
	public float getPreferredWidth() {
		return preferredWidth;
	}
	
	public float getPreferredHeight() {
		return preferredHeight;
	}
}
