package net.grilledham.hamhacks.gui.parts.impl;

import net.grilledham.hamhacks.gui.parts.GuiPart;
import net.grilledham.hamhacks.modules.render.ClickGUI;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScrollablePart extends GuiPart {
	
	private float nextScroll;
	
	private float scroll = 0;
	
	private int maxHeight;
	
	private final List<GuiPart> subParts = new ArrayList<>();
	private final HashMap<GuiPart, Boolean> isPartEnabled = new HashMap<>();
	
	public ScrollablePart(int x, int y, int width, int maxHeight) {
		super(x, y, width, 0);
		this.maxHeight = maxHeight;
	}
	
	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}
	
	public void addPart(GuiPart part) {
		subParts.add(part);
		isPartEnabled.put(part, true);
	}
	
	public void addPart(GuiPart part, int i) {
		subParts.add(i, part);
		isPartEnabled.put(part, true);
	}
	
	public void removePart(GuiPart part) {
		subParts.remove(part);
		isPartEnabled.remove(part);
	}
	
	public void setPartEnabled(GuiPart part, boolean enabled) {
		isPartEnabled.put(part, enabled);
	}
	
	public List<GuiPart> getParts() {
		return subParts;
	}
	
	public void clearParts() {
		subParts.clear();
	}
	
	@Override
	protected void render(MatrixStack stack, int mx, int my, int scrollX, int scrollY, float partialTicks) {
		int x = this.x + scrollX;
		int y = this.y + scrollY;
		stack.push();
		
		RenderUtil.adjustScissor(x, y, width, height, ClickGUI.getInstance().scale);
		RenderUtil.applyScissor();
		
		float trueHeight = 0;
		for(GuiPart part : subParts) {
			if(isPartEnabled.get(part)) {
				part.draw(stack, mx, my, scrollX, (int)(scrollY - scroll + trueHeight), partialTicks);
				trueHeight += part.getHeight();
			}
		}
		
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
	protected void renderTop(MatrixStack stack, int mx, int my, int scrollX, int scrollY, float partialTicks) {
		int x = this.x + scrollX;
		int y = this.y + scrollY;
		stack.push();
		
		RenderUtil.pushScissor(x, y, width, height, ClickGUI.getInstance().scale);
		RenderUtil.applyScissor();
		
		float trueHeight = 0;
		for(GuiPart part : subParts) {
			if(isPartEnabled.get(part)) {
				part.drawTop(stack, mx, my, scrollX, (int)(scrollY - scroll + trueHeight), partialTicks);
				trueHeight += part.getHeight();
			}
		}
		
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
	public boolean click(double mx, double my, int scrollX, int scrollY, int button) {
		float trueHeight = 0;
		for(GuiPart part : subParts) {
			if(isPartEnabled.get(part)) {
				if(part.click(mx, my, scrollX, (int)(scrollY - scroll + trueHeight), button)) {
					return true;
				}
				trueHeight += part.getHeight();
			}
		}
		return super.click(mx, my, scrollX, (int)(scrollY - scroll), button);
	}
	
	@Override
	public boolean release(double mx, double my, int scrollX, int scrollY, int button) {
		float trueHeight = 0;
		for(GuiPart part : subParts) {
			if(isPartEnabled.get(part)) {
				if(part.release(mx, my, scrollX, (int)(scrollY - scroll + trueHeight), button)) {
					return true;
				}
				trueHeight += part.getHeight();
			}
		}
		return super.release(mx, my, scrollX, (int)(scrollY - scroll), button);
	}
	
	@Override
	public boolean drag(double mx, double my, int scrollX, int scrollY, int button, double dx, double dy) {
		float trueHeight = 0;
		for(GuiPart part : subParts) {
			if(isPartEnabled.get(part)) {
				if(part.drag(mx, my, scrollX, (int)(scrollY - scroll + trueHeight), button, dx, dy)) {
					return true;
				}
				trueHeight += part.getHeight();
			}
		}
		return super.drag(mx, my, scrollX, (int)(scrollY - scroll), button, dx, dy);
	}
	
	@Override
	public boolean type(int code, int scanCode, int modifiers) {
		for(GuiPart part : subParts) {
			if(isPartEnabled.get(part)) {
				if(part.type(code, scanCode, modifiers)) {
					return true;
				}
			}
		}
		return super.type(code, scanCode, modifiers);
	}
	
	@Override
	public boolean typeChar(char c, int modifiers) {
		for(GuiPart part : subParts) {
			if(isPartEnabled.get(part)) {
				if(part.typeChar(c, modifiers)) {
					return true;
				}
			}
		}
		return super.typeChar(c, modifiers);
	}
	
	@Override
	public boolean scroll(double mx, double my, int scrollX, int scrollY, double delta) {
		int x = this.x + scrollX;
		int y = this.y + scrollY;
		if(mx >= x && mx < x + width && my >= y && my < y + height) {
			float trueHeight = 0;
			for(GuiPart part : subParts) {
				if(isPartEnabled.get(part)) {
					if(part.scroll(mx, my, scrollX, (int)(scrollY - scroll + trueHeight), delta)) {
						return true;
					}
					trueHeight += part.getHeight();
				}
			}
			if(height >= maxHeight) {
				nextScroll -= delta * 10;
				return true;
			}
		}
		return super.scroll(mx, my, scrollX, scrollY, delta);
	}
}
