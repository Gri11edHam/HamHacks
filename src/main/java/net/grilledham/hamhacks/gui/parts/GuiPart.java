package net.grilledham.hamhacks.gui.parts;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public abstract class GuiPart {
	
	protected final MinecraftClient mc = MinecraftClient.getInstance();
	
	protected float x;
	protected float y;
	protected float width;
	protected float height;
	
	protected final float preferredWidth;
	
	protected float lastX;
	protected float lastY;
	
	public GuiPart(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.preferredWidth = width;
	}
	
	public void moveTo(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void moveBy(float x, float y) {
		this.x += x;
		this.y += y;
	}
	
	public void resize(float maxW, float maxH) {
		width = maxW;
		height = maxH;
	}
	
	public void draw(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		render(stack, mx, my, scrollX, scrollY, partialTicks);
		lastX = x;
		lastY = y;
	}
	
	public void drawTop(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		renderTop(stack, mx, my, scrollX, scrollY, partialTicks);
		lastX = x;
		lastY = y;
	}
	
	protected abstract void render(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks);
	
	protected void renderTop(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
	}
	
	public boolean click(double mx, double my, float scrollX, float scrollY, int button) {
		return false;
	}
	
	public boolean release(double mx, double my, float scrollX, float scrollY, int button) {
		return false;
	}
	
	public boolean drag(double mx, double my, float scrollX, float scrollY, int button, double dx, double dy) {
		return false;
	}
	
	public boolean type(int code, int scanCode, int modifiers) {
		return false;
	}
	
	public boolean typeChar(char c, int modifiers) {
		return false;
	}
	
	public boolean scroll(double mx, double my, float scrollX, float scrollY, double delta) {
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
}
