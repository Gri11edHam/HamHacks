package net.grilledham.hamhacks.gui.parts;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public abstract class GuiPart {
	
	protected final MinecraftClient mc = MinecraftClient.getInstance();
	
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	
	protected int lastX;
	protected int lastY;
	
	public GuiPart(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public void moveTo(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void moveBy(int x, int y) {
		this.x += x;
		this.y += y;
	}
	
	public void resize(int maxW, int maxH) {
		width = maxW;
		height = maxH;
	}
	
	public void draw(MatrixStack stack, int mx, int my, float partialTicks) {
		render(stack, mx, my, partialTicks);
		lastX = x;
		lastY = y;
	}
	
	public void drawTop(MatrixStack stack, int mx, int my, float partialTicks) {
		renderTop(stack, mx, my, partialTicks);
		lastX = x;
		lastY = y;
	}
	
	protected abstract void render(MatrixStack stack, int mx, int my, float partialTicks);
	
	protected void renderTop(MatrixStack stack, int mx, int my, float partialTicks) {
	}
	
	public boolean click(double mx, double my, int button) {
		return false;
	}
	
	public boolean release(double mx, double my, int button) {
		return false;
	}
	
	public boolean drag(double mx, double my, int button, double dx, double dy) {
		return false;
	}
	
	public boolean type(int code, int scanCode, int modifiers) {
		return false;
	}
	
	public boolean typeChar(char c, int modifiers) {
		return false;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
