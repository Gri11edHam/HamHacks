package net.grilledham.hamhacks.gui.element.impl;

import net.grilledham.hamhacks.animation.Animation;
import net.grilledham.hamhacks.animation.AnimationType;
import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.gui.screen.impl.ClickGUIScreen;
import net.grilledham.hamhacks.page.Page;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class PageElement extends GuiElement {
	
	private final Animation hoverAnimation = new Animation(AnimationType.EASE, 0.25, true);
	
	private final Page page;
	
	private final boolean left;
	private final boolean right;
	
	private final ClickGUIScreen parent;
	
	public PageElement(ClickGUIScreen parent, float x, float y, double scale, Page page, boolean left, boolean right) {
		super(x, y, MinecraftClient.getInstance().textRenderer.getWidth(page == null ? Text.translatable("hamhacks.page.null").getString() : page.getName()) + 8 + (left ? 2 : 0) + (right ? 2 : 0), 20, scale);
		this.parent = parent;
		this.page = page;
		this.left = left;
		this.right = right;
	}
	
	@Override
	public void render(DrawContext ctx, int mx, int my, float offX, float offY, float tickDelta) {
		MatrixStack stack = ctx.getMatrices();
		float x = this.x + offX;
		float y = this.y + offY;
		stack.push();
		RenderUtil.preRender();
		
		ClickGUI ui = PageManager.getPage(ClickGUI.class);
		boolean hovered = mx >= x && mx < x + width && my >= y && my < y + height;
		int bgC = ui.bgColor.get().getRGB();
		bgC = RenderUtil.mix(ui.bgColorHovered.get().getRGB(), bgC, hoverAnimation.get());
		float width = this.width;
		float height = this.height - 2;
		if(left) {
			x += 2;
			width -= 2;
		}
		if(right) {
			width -= 2;
		}
		RenderUtil.drawRect(stack, x, y, width, height, bgC);
		RenderUtil.drawRect(stack, x, y + height, width, 2, ui.accentColor.get().getRGB());
		if(left) {
			RenderUtil.drawRect(stack, this.x + offX, y, 2, this.height, ui.accentColor.get().getRGB());
		}
		if(right) {
			RenderUtil.drawRect(stack, x + width, y, 2, this.height, ui.accentColor.get().getRGB());
		}
		
		String name = page == null ? Text.translatable("hamhacks.page.null").getString() : page.getName();
		RenderUtil.drawString(ctx, name, x + width / 2f - mc.textRenderer.getWidth(name) / 2f, y + height / 2f - mc.textRenderer.fontHeight / 2f, ui.textColor.get().getRGB(), true);
		
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
				parent.setPage(page);
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			
			}
		}
		return super.release(mx, my, scrollX, scrollY, button);
	}
}
