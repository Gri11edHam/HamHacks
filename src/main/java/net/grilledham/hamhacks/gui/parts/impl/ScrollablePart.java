package net.grilledham.hamhacks.gui.parts.impl;

import net.grilledham.hamhacks.gui.parts.GuiPart;
import net.grilledham.hamhacks.modules.render.ClickGUI;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

public class ScrollablePart extends GuiPart {
	
	private float nextScroll;
	
	private float scroll = 0;
	
	private final int maxHeight;
	
	private final List<GuiPart> subParts = new ArrayList<>();
	
	public ScrollablePart(int x, int y, int width, int maxHeight) {
		super(x, y, width, 0);
		this.maxHeight = maxHeight;
	}
	
	public void addPart(GuiPart part) {
		subParts.add(part);
	}
	
	public void addPart(GuiPart part, int i) {
		subParts.add(i, part);
	}
	
	public void removePart(GuiPart part) {
		subParts.remove(part);
	}
	
	public void clearParts() {
		subParts.clear();
	}
	
	@Override
	protected void render(MatrixStack stack, int mx, int my, float partialTicks) {
		stack.push();
		
		RenderUtil.adjustScissor(x, y, width, height, ClickGUI.getInstance().scale.getValue());
		RenderUtil.applyScissor();
		
		RenderUtil.translateScissor(0, -scroll);
		
		float trueHeight = 0;
		for(GuiPart part : subParts) {
			stack.translate(0, -scroll, 0);
			part.draw(stack, mx, (int)(my + scroll), partialTicks);
			stack.translate(0, scroll, 0);
			trueHeight += part.getHeight();
		}
		
		RenderUtil.translateScissor(0, scroll);
		
		RenderUtil.popScissor();
		
		stack.pop();
		
		height = (int)Math.min(trueHeight, maxHeight);
		
		if(nextScroll > trueHeight - height) {
			nextScroll = trueHeight - height;
		} else if(nextScroll < 0) {
			nextScroll = 0;
		}
		
		scroll = scroll + (nextScroll - scroll) * partialTicks;
	}
	
	@Override
	protected void renderTop(MatrixStack stack, int mx, int my, float partialTicks) {
		stack.push();
		
		RenderUtil.pushScissor(x, y, width, height, ClickGUI.getInstance().scale.getValue());
		RenderUtil.applyScissor();
		
		RenderUtil.translateScissor(0, -scroll);
		
		for(GuiPart part : subParts) {
			stack.translate(0, -scroll, 0);
			part.drawTop(stack, mx, (int)(my + scroll), partialTicks);
			stack.translate(0, +scroll, 0);
		}
		
		RenderUtil.translateScissor(0, scroll);
		
		RenderUtil.popScissor();
		
		stack.pop();
	}
	
	@Override
	public void moveBy(int x, int y) {
		super.moveBy(x, y);
		for(GuiPart part : subParts) {
			part.moveBy(x, y);
		}
	}
	
	@Override
	public void moveTo(int x, int y) {
		super.moveTo(x, y);
		for(GuiPart part : subParts) {
			part.moveTo(x, y);
		}
	}
	
	@Override
	public boolean click(double mx, double my, int button) {
		for(GuiPart part : subParts) {
			if(part.click(mx, my + scroll, button)) {
				return true;
			}
		}
		return super.click(mx, my + scroll, button);
	}
	
	@Override
	public boolean release(double mx, double my, int button) {
		for(GuiPart part : subParts) {
			if(part.release(mx, my + scroll, button)) {
				return true;
			}
		}
		return super.release(mx, my + scroll, button);
	}
	
	@Override
	public boolean drag(double mx, double my, int button, double dx, double dy) {
		for(GuiPart part : subParts) {
			if(part.drag(mx, my + scroll, button, dx, dy)) {
				return true;
			}
		}
		return super.drag(mx, my + scroll, button, dx, dy);
	}
	
	@Override
	public boolean type(int code, int scanCode, int modifiers) {
		for(GuiPart part : subParts) {
			if(part.type(code, scanCode, modifiers)) {
				return true;
			}
		}
		return super.type(code, scanCode, modifiers);
	}
	
	@Override
	public boolean typeChar(char c, int modifiers) {
		for(GuiPart part : subParts) {
			if(part.typeChar(c, modifiers)) {
				return true;
			}
		}
		return super.typeChar(c, modifiers);
	}
	
	@Override
	public boolean scroll(double mx, double my, double delta) {
		if(mx >= x && mx < x + width && my >= y && my < y + height) {
			for(GuiPart part : subParts) {
				if(part.scroll(mx, my + scroll, delta)) {
					return true;
				}
			}
			if(height >= maxHeight) {
				nextScroll -= delta * 10;
				return true;
			}
		}
		return super.scroll(mx, my, delta);
	}
}
