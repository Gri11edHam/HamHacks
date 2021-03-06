package net.grilledham.hamhacks.gui.parts.impl;

import net.grilledham.hamhacks.gui.parts.GuiPart;
import net.grilledham.hamhacks.modules.render.ClickGUI;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

public class ButtonPart extends GuiPart {
	
	private float hoverAnimation;
	
	private String text;
	private final Runnable onClick;
	
	public ButtonPart(String text, int x, int y, int width, int height, Runnable onClick) {
		super(x, y, width, height);
		this.text = text;
		this.onClick = onClick;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	public void render(MatrixStack stack, int mx, int my, float partialTicks) {
		stack.push();
		RenderUtil.preRender();
		
		int bgC = 0xff202020;
		boolean hovered = mx >= x && mx < x + width && my >= y && my < y + height;
		bgC = RenderUtil.mix(0xffa0a0a0, bgC, hoverAnimation);
		RenderUtil.drawRect(stack, x, y, width, height, bgC);
		
		mc.textRenderer.drawWithShadow(stack, text, x + width / 2f - mc.textRenderer.getWidth(text) / 2f, y + height / 2f - mc.textRenderer.fontHeight / 2f, ClickGUI.getInstance().textColor.getRGB());
		
		RenderUtil.postRender();
		stack.pop();
		
		if(hovered) {
			hoverAnimation += partialTicks / 5;
		} else {
			hoverAnimation -= partialTicks / 5;
		}
		hoverAnimation = Math.min(1, Math.max(0, hoverAnimation));
	}
	
	@Override
	public boolean release(double mx, double my, int button) {
		if(mx >= x && mx < x + width && my >= y && my < y + height) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				onClick.run();
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			
			}
			return true;
		}
		return super.release(mx, my, button);
	}
}
