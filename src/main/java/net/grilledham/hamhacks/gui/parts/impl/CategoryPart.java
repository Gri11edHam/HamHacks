package net.grilledham.hamhacks.gui.parts.impl;

import net.grilledham.hamhacks.gui.parts.GuiPart;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.render.ClickGUI;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

public class CategoryPart extends GuiPart {
	
	private final Module.Category category;
	
	private float openCloseAnimation;
	private float hoverAnimation;
	
	private final ScrollablePart scrollArea;
	
	private boolean dragging = false;
	private int lastMouseX = 0;
	private int lastMouseY = 0;
	
	public CategoryPart(Screen parent, Module.Category category) {
		super(category.getX(), category.getY(), category.getWidth(), category.getHeight());
		this.category = category;
		openCloseAnimation = category.isExpanded() ? 1 : 0;
		int i = 0;
		scrollArea = new ScrollablePart(x, y + height, width, 16 * 8);
		for(Module m : ModuleManager.getModules(category)) {
			scrollArea.addPart(new ModulePart(parent, x, y + height + (16 * i), width, 16, m));
			i++;
		}
		scrollArea.moveTo(x, y + height);
	}
	
	@Override
	public void render(MatrixStack stack, int mx, int my, int scrollX, int scrollY, float partialTicks) {
		int x = this.x + scrollX;
		int y = this.y + scrollY;
		stack.push();
		
		float scissorHeight = height + scrollArea.getHeight() * (1 - openCloseAnimation);
		
		RenderUtil.pushScissor(x, y, width, scissorHeight, ClickGUI.getInstance().scale);
		RenderUtil.applyScissor();
		RenderUtil.preRender();
		
		int bgC = ClickGUI.getInstance().bgColor.getRGB();
		boolean hovered = mx >= x && mx < x + width && my >= y && my < y + height;
		bgC = RenderUtil.mix(ClickGUI.getInstance().bgColorHovered.getRGB(), bgC, hoverAnimation);
		RenderUtil.drawRect(stack, x + 1, y + 1, width - 1, height - 1, bgC);
		
		RenderUtil.drawRect(stack, x + 1, y, width - 1, 1, ClickGUI.getInstance().accentColor.getRGB());
		
		RenderUtil.drawRect(stack, x, y, 1, height, ClickGUI.getInstance().accentColor.getRGB());
		
		mc.textRenderer.drawWithShadow(stack, category.getText(), x + 3, y + 5, ClickGUI.getInstance().textColor.getRGB());
		
		scrollArea.draw(stack, mx, my, scrollX, scrollY, partialTicks);
		
		RenderUtil.popScissor();
		RenderUtil.postRender();
		
		stack.pop();
		
		if(dragging) {
			moveBy(mx - lastMouseX, my - lastMouseY);
			scrollArea.moveBy(mx - lastMouseX, my - lastMouseY);
			category.setPos(x, y);
			lastMouseX = mx;
			lastMouseY = my;
		}
		
		if(category.isExpanded()) {
			openCloseAnimation += partialTicks / 5;
		} else {
			openCloseAnimation -= partialTicks / 5;
		}
		openCloseAnimation = Math.min(1, Math.max(0, openCloseAnimation));
		
		if(hovered) {
			hoverAnimation += partialTicks / 5;
		} else {
			hoverAnimation -= partialTicks / 5;
		}
		hoverAnimation = Math.min(1, Math.max(0, hoverAnimation));
	}
	
	@Override
	protected void renderTop(MatrixStack stack, int mx, int my, int scrollX, int scrollY, float partialTicks) {
		scrollArea.drawTop(stack, mx, my, scrollX, scrollY, partialTicks);
	}
	
	@Override
	public boolean click(double mx, double my, int scrollX, int scrollY, int button) {
		int x = this.x + scrollX;
		int y = this.y + scrollY;
		if(mx >= x && mx < x + width && my >= y && my < y + height) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				dragging = true;
				lastMouseX = (int)mx;
				lastMouseY = (int)my;
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			
			}
			return true;
		}
		if(openCloseAnimation <= 0.5f) {
			if(scrollArea.click(mx, my, scrollX, scrollY, button)) {
				return true;
			}
		}
		return super.click(mx, my, scrollX, scrollY, button);
	}
	
	@Override
	public boolean release(double mx, double my, int scrollX, int scrollY, int button) {
		int x = this.x + scrollX;
		int y = this.y + scrollY;
		boolean wasDragging = dragging;
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			dragging = false;
		}
		if(mx >= x && mx < x + width && my >= y && my < y + height) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				category.expand(!category.isExpanded());
			}
			return true;
		}
		if(!wasDragging) {
			if(openCloseAnimation <= 0.5f) {
				if(scrollArea.release(mx, my, scrollX, scrollY, button)) {
					return true;
				}
			}
		}
		return super.release(mx, my, scrollX, scrollY, button);
	}
	
	@Override
	public boolean drag(double mx, double my, int scrollX, int scrollY, int button, double dx, double dy) {
		int x = this.x + scrollX;
		int y = this.y + scrollY;
		if(mx >= x && mx < x + width && my >= y && my < y + height) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			
			}
			return true;
		}
		if(openCloseAnimation <= 0.5f) {
			if(scrollArea.drag(mx, my, scrollX, scrollY, button, dx, dy)) {
				return true;
			}
		}
		return super.drag(mx, my, scrollX, scrollY, button, dx, dy);
	}
	
	@Override
	public boolean scroll(double mx, double my, int scrollX, int scrollY, double delta) {
		if(scrollArea.scroll(mx, my, scrollX, scrollY, delta)) {
			return true;
		}
		return super.scroll(mx, my, scrollX, scrollY, delta);
	}
}
