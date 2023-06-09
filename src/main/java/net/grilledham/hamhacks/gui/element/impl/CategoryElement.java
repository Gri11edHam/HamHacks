package net.grilledham.hamhacks.gui.element.impl;

import net.grilledham.hamhacks.animation.Animation;
import net.grilledham.hamhacks.animation.AnimationType;
import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

public class CategoryElement extends GuiElement {
	
	public static final int PREFERED_SCROLL_HEIGHT = 16 * 16;
	private final Category category;
	
	private final Animation openCloseAnimation = new Animation(AnimationType.EASE_IN_OUT, 0.25);
	private final Animation hoverAnimation = new Animation(AnimationType.EASE_IN_OUT, 0.25);
	private final Animation overflowAnimation = new Animation(AnimationType.EASE_IN_OUT, 0.25);
	
	private final ScrollableElement scrollArea;
	
	private boolean dragging = false;
	private double lastMouseX = 0;
	private double lastMouseY = 0;
	
	public CategoryElement(Screen parent, Category category, double scale) {
		super(category.getX(), category.getY(), PageManager.getPage(ClickGUI.class).categoriesWidth.get().floatValue(), category.getHeight(), scale);
		this.category = category;
		openCloseAnimation.setAbsolute(1);
		int i = 0;
		scrollArea = new ScrollableElement(x + 1, y + height, width - 2, PREFERED_SCROLL_HEIGHT, scale);
		for(Module m : ModuleManager.getModules(category)) {
			scrollArea.addElement(new ModuleElement(parent, x + 1, y + height + (16 * i), width - 2, 16, scale, m));
			i++;
		}
		scrollArea.moveTo(x + 1, y + height);
	}
	
	@Override
	public void draw(DrawContext ctx, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		MatrixStack stack = ctx.getMatrices();
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		stack.push();
		
		float scissorHeight = height + scrollArea.getHeight() * (float)(1 - openCloseAnimation.get()) + 1;
		
		RenderUtil.pushScissor(x, y, width, scissorHeight, (float)scale);
		RenderUtil.preRender();
		
		ClickGUI ui = PageManager.getPage(ClickGUI.class);
		int bgC = ui.accentColor.get().getRGB();
		boolean hovered = mx >= x && mx < x + width && my >= y && my < y + height;
		RenderUtil.drawRect(stack, x, y, width, height, bgC);
		
		RenderUtil.pushScissor(x + 2, y + 4, width - 4, 11, (float)scale);
		RenderUtil.drawString(ctx, category.getName(), x + 3, y + 5, ui.textColor.get().getRGB(), true);
		RenderUtil.popScissor();
		
		scrollArea.render(ctx, mx, my, scrollX, scrollY, partialTicks);
		
		RenderUtil.popScissor();
		RenderUtil.postRender();
		
		stack.pop();
		
		if(dragging) {
			moveBy((float)(mx - lastMouseX), (float)(my - lastMouseY));
			scrollArea.moveBy((float)(mx - lastMouseX), (float)(my - lastMouseY));
			category.setPos(this.x, this.y);
			lastMouseX = mx;
			lastMouseY = my;
		}
		
		scrollArea.setMaxHeight((float)Math.min(PREFERED_SCROLL_HEIGHT, (mc.getWindow().getHeight() / scale) - scrollArea.getY()));
		
		openCloseAnimation.set(category.isExpanded());
		openCloseAnimation.update();
		
		hoverAnimation.set(hovered);
		hoverAnimation.update();
	}
	
	@Override
	public void drawTop(DrawContext ctx, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		if(hoverAnimation.get() == 1) {
			MatrixStack stack = ctx.getMatrices();
			float x = this.x + scrollX;
			float y = this.y + scrollY;
			stack.push();
			
			RenderUtil.pushScissor(x + width, y, Math.max(mc.textRenderer.getWidth(category.getName()) - width + 6, 0), height + 1, (float)scale);
			RenderUtil.preRender();
			
			ClickGUI ui = PageManager.getPage(ClickGUI.class);
			int bgC = ui.accentColor.get().getRGB() & 0xffffff;
			int transparency = (int)(overflowAnimation.get() * 0xff) << 24;
			bgC += transparency;
			RenderUtil.drawRect(stack, x, y, mc.textRenderer.getWidth(category.getName()) + 6, height, bgC);
			
			RenderUtil.pushScissor(x + width - 2, y + 4, Math.max(mc.textRenderer.getWidth(category.getName()) - width + 6, 0), 11, (float)scale);
			RenderUtil.drawString(ctx, category.getName(), x + 3, y + 5, ui.textColor.get().getRGB(), true);
			RenderUtil.popScissor();
			
			RenderUtil.popScissor();
			RenderUtil.postRender();
			
			stack.pop();
		}
		
		overflowAnimation.set(hoverAnimation.get() == 1);
		overflowAnimation.update();
		
		scrollArea.renderTop(ctx, mx, my, scrollX, scrollY, partialTicks);
	}
	
	@Override
	public boolean click(double mx, double my, float scrollX, float scrollY, int button) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		if(mx >= x && mx < x + width && my >= y && my < y + height) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				dragging = true;
				lastMouseX = mx;
				lastMouseY = my;
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			
			}
		}
		if(openCloseAnimation.get() <= 0.5) {
			scrollArea.click(mx, my, scrollX, scrollY, button);
		}
		return super.click(mx, my, scrollX, scrollY, button);
	}
	
	@Override
	public boolean release(double mx, double my, float scrollX, float scrollY, int button) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		boolean wasDragging = dragging;
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			dragging = false;
		}
		if(mx >= x && mx < x + width && my >= y && my < y + height) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				category.expand(!category.isExpanded());
			}
		}
		if(!wasDragging) {
			if(openCloseAnimation.get() <= 0.5) {
				scrollArea.release(mx, my, scrollX, scrollY, button);
			}
		}
		return super.release(mx, my, scrollX, scrollY, button);
	}
	
	@Override
	public boolean drag(double mx, double my, float scrollX, float scrollY, int button, double dx, double dy) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		if(mx >= x && mx < x + width && my >= y && my < y + height) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			
			}
			return true;
		}
		if(openCloseAnimation.get() <= 0.5) {
			if(scrollArea.drag(mx, my, scrollX, scrollY, button, dx, dy)) {
				return true;
			}
		}
		return super.drag(mx, my, scrollX, scrollY, button, dx, dy);
	}
	
	@Override
	public boolean scroll(double mx, double my, float scrollX, float scrollY, double delta) {
		if(scrollArea.scroll(mx, my, scrollX, scrollY, delta)) {
			return true;
		}
		return super.scroll(mx, my, scrollX, scrollY, delta);
	}
}
