package net.grilledham.hamhacks.gui.element;

import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public abstract class GuiElement {
	
	protected final MinecraftClient mc = MinecraftClient.getInstance();
	
	protected float x;
	protected float y;
	protected float width;
	protected float height;
	
	protected final float preferredWidth;
	protected final float preferredHeight;
	
	protected final double scale;
	
	private float tooltipTicks = 0;
	protected boolean showTooltip = false;
	
	private String tooltipTitle = "";
	private String tooltip = "";
	
	public GuiElement(float x, float y, float width, float height, double scale) {
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
	
	public void render(DrawContext ctx, int mx, int my, float offX, float offY, float tickDelta) {
		draw(ctx, mx, my, offX, offY, tickDelta);
		if(mx >= x + offX && my >= y + offY && mx < x + offX + width && my < y + offY + height) {
			tooltipTicks += tickDelta;
		} else {
			tooltipTicks -= tickDelta * 4;
			if(tooltipTicks < 0) {
				tooltipTicks = 0;
			}
		}
		showTooltip = tooltipTicks >= 20;
		if(showTooltip) {
			tooltipTicks = 20;
		}
	}
	
	protected abstract void draw(DrawContext ctx, int mx, int my, float offX, float offY, float tickDelta);
	
	public void renderTop(DrawContext ctx, int mx, int my, float offX, float offY, float tickDelta) {
		MatrixStack stack = ctx.getMatrices();
		stack.push();
		stack.translate(0, 0, 1);
		drawTop(ctx, mx, my, offX, offY, tickDelta);
		if(showTooltip && !tooltipTitle.equals("") && !tooltip.equals("")) {
			stack.translate(0, 0, 1);
			RenderUtil.drawToolTip(ctx, tooltipTitle, tooltip, mx, my, scale);
		}
		stack.pop();
	}
	
	protected void drawTop(DrawContext ctx, int mx, int my, float offX, float offY, float tickDelta) {}
	
	public void setTooltip(String title, String tooltip) {
		this.tooltipTitle = title;
		this.tooltip = tooltip;
	}
	
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
