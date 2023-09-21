package net.grilledham.hamhacks.gui.element.impl;

import net.grilledham.hamhacks.animation.Animation;
import net.grilledham.hamhacks.animation.AnimationType;
import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

public class ButtonElement extends GuiElement {
	
	private final Animation hoverAnimation = new Animation(AnimationType.EASE_IN_OUT, 0.25);
	
	private String text;
	private final Runnable onClick;
	
	protected boolean drawBackground = true;
	
	public ButtonElement(String text, float x, float y, float width, float height, double scale, Runnable onClick) {
		super(x, y, width, height, scale);
		this.text = text;
		this.onClick = onClick;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	public void draw(DrawContext ctx, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		MatrixStack stack = ctx.getMatrices();
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		stack.push();
		RenderUtil.preRender();
		
		ClickGUI ui = PageManager.getPage(ClickGUI.class);
		boolean hovered = mx >= x && mx < x + width && my >= y && my < y + height;
		if(drawBackground) {
			int bgC = ui.bgColor.get().getRGB();
			bgC = RenderUtil.mix(ui.bgColorHovered.get().getRGB(), bgC, hoverAnimation.get());
			RenderUtil.drawRect(stack, x, y, width, height, bgC);
		}
		RenderUtil.drawHRect(stack, x, y, width, height, 0xffcccccc);
		
		RenderUtil.drawString(ctx, text, x + width / 2f - RenderUtil.getStringWidth(text) / 2f, y + height / 2f - RenderUtil.getFontHeight() / 2f, ui.textColor.get().getRGB(), true);
		
		RenderUtil.postRender();
		stack.pop();
		
		hoverAnimation.set(hovered);
		hoverAnimation.update();
	}
	
	@Override
	public boolean release(double mx, double my, float scrollX, float scrollY, int button) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		if(mx >= x && mx < x + width && my >= y && my < y + height) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				onClick.run();
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			
			}
		}
		return super.release(mx, my, scrollX, scrollY, button);
	}
}
