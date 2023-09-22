package net.grilledham.hamhacks.gui.element.impl;

import net.grilledham.hamhacks.animation.Animation;
import net.grilledham.hamhacks.animation.AnimationType;
import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScrollableElement extends GuiElement {
	
	private float nextScroll;
	
	private final Animation scroll = new Animation(AnimationType.LINEAR, 0.05);
	
	private float maxHeight;
	
	private final List<GuiElement> subElements = new ArrayList<>();
	private final HashMap<GuiElement, Boolean> isElementEnabled = new HashMap<>();
	
	private GuiElement selected = null;
	
	public ScrollableElement(float x, float y, float width, float maxHeight, double scale) {
		super(x, y, width, 0, scale);
		this.maxHeight = maxHeight;
	}
	
	public void setMaxHeight(float maxHeight) {
		this.maxHeight = maxHeight;
	}
	
	public void addElement(GuiElement element) {
		subElements.add(element);
		isElementEnabled.put(element, true);
	}
	
	public void addElement(GuiElement element, int i) {
		subElements.add(i, element);
		isElementEnabled.put(element, true);
	}
	
	public void removeElement(GuiElement element) {
		subElements.remove(element);
		isElementEnabled.remove(element);
	}
	
	public void setEnabled(GuiElement element, boolean enabled) {
		isElementEnabled.put(element, enabled);
	}
	
	public List<GuiElement> getElements() {
		return subElements;
	}
	
	public void clearElements() {
		subElements.clear();
	}
	
	@Override
	public void draw(DrawContext ctx, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		MatrixStack stack = ctx.getMatrices();
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		
		boolean hovered = mx >= x && my >= y && mx <= x + width && my <= y + height;
		
		stack.push();
		
		RenderUtil.adjustScissor(x, y, width, height, (float)scale);
		
		float trueHeight = 0;
		for(GuiElement element : subElements) {
			if(isElementEnabled.get(element)) {
				if(element.getX() + element.getWidth() >= x - 2 - scrollX && element.getY() + element.getHeight() + trueHeight >= y - 2 - scrollY + scroll.get() && element.getX() - element.getWidth() <= x + width + 2 - scrollX && element.getY() - element.getHeight() + trueHeight <= y - scrollY + height + 2 + scroll.get()) {
					element.render(ctx, hovered || element == selected ? mx : -1, hovered || element == selected ? my : -1, scrollX, scrollY - (float)scroll.get() + trueHeight, partialTicks);
				}
				trueHeight += element.getHeight();
			}
		}
		
		RenderUtil.popScissor();
		
		stack.pop();
		
		height = Math.min(trueHeight, maxHeight);
		
		if(nextScroll > trueHeight - height) {
			nextScroll = trueHeight - height;
		} else if(nextScroll < 0) {
			nextScroll = 0;
		}
		
		scroll.set(nextScroll);
		scroll.update();
	}
	
	@Override
	public void drawTop(DrawContext ctx, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		MatrixStack stack = ctx.getMatrices();
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		
		boolean hovered = mx >= x && my >= y && mx <= x + width && my <= y + height;
		
		stack.push();
		stack.translate(0, 0, 1);
		
		RenderUtil.pushScissor(x, y, width, height, (float)scale);
		
		float trueHeight = 0;
		for(GuiElement element : subElements) {
			if(isElementEnabled.get(element)) {
				element.renderTop(ctx, hovered || element == selected ? mx : -1, hovered || element == selected ? my : -1, scrollX, scrollY - (float)scroll.get() + trueHeight, partialTicks);
				trueHeight += element.getHeight();
			}
		}
		
		RenderUtil.popScissor();
		
		stack.pop();
	}
	
	@Override
	public void moveBy(float x, float y) {
		super.moveBy(x, y);
		for(GuiElement element : subElements) {
			element.moveBy(x, y);
		}
	}
	
	@Override
	public void moveTo(float x, float y) {
		super.moveTo(x, y);
		for(GuiElement element : subElements) {
			element.moveTo(x, y);
		}
	}
	
	@Override
	public void resize(float maxW, float maxH) {
		super.resize(maxW, height);
		for(GuiElement element : subElements) {
			element.resize(maxW, element.getHeight());
		}
	}
	
	@Override
	public boolean click(double mx, double my, float scrollX, float scrollY, int button) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		boolean hovered = mx >= x && my >= y && mx <= x + width && my <= y + height;
		float trueHeight = 0;
		for(GuiElement element : subElements) {
			if(isElementEnabled.get(element)) {
				if(selected != null) {
					if(element == selected) {
						selected.click(mx, my, scrollX, scrollY - (float)scroll.get() + trueHeight, button);
						return true;
					}
				} else {
					if(hovered) {
						if(element.click(mx, my, scrollX, scrollY - (float)scroll.get() + trueHeight, button)) {
							selected = element;
							return true;
						}
					}
				}
				trueHeight += element.getHeight();
			}
		}
		return super.click(mx, my, scrollX, scrollY - (float)scroll.get(), button);
	}
	
	@Override
	public boolean release(double mx, double my, float scrollX, float scrollY, int button) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		boolean hovered = mx >= x && my >= y && mx <= x + width && my <= y + height;
		float trueHeight = 0;
		for(GuiElement element : subElements) {
			if(isElementEnabled.get(element)) {
				if(selected != null) {
					if(element == selected) {
						if(!selected.release(mx, my, scrollX, scrollY - (float)scroll.get() + trueHeight, button)) {
							selected = null;
							return false;
						}
						return true;
					}
				} else {
					if(hovered) {
						if(element.release(mx, my, scrollX, scrollY - (float)scroll.get() + trueHeight, button)) {
							selected = element;
							return true;
						}
					}
				}
				trueHeight += element.getHeight();
			}
		}
		return super.release(mx, my, scrollX, scrollY - (float)scroll.get(), button);
	}
	
	@Override
	public boolean drag(double mx, double my, float scrollX, float scrollY, int button, double dx, double dy) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		boolean hovered = mx >= x && my >= y && mx <= x + width && my <= y + height;
		float trueHeight = 0;
		for(GuiElement element : subElements) {
			if(isElementEnabled.get(element)) {
				if(selected != null) {
					if(element == selected) {
						selected.drag(mx, my, scrollX, scrollY - (float)scroll.get() + trueHeight, button, dx, dy);
						return true;
					}
				} else {
					if(hovered) {
						if(element.drag(mx, my, scrollX, scrollY - (float)scroll.get() + trueHeight, button, dx, dy)) {
							return true;
						}
					}
				}
				trueHeight += element.getHeight();
			}
		}
		return super.drag(mx, my, scrollX, scrollY - (float)scroll.get(), button, dx, dy);
	}
	
	@Override
	public boolean type(int code, int scanCode, int modifiers) {
		if(selected != null) {
			if(!selected.type(code, scanCode, modifiers)) {
				selected = null;
				return false;
			}
			return true;
		}
		for(GuiElement element : subElements) {
			if(isElementEnabled.get(element)) {
				if(element.type(code, scanCode, modifiers)) {
					return true;
				}
			}
		}
		return super.type(code, scanCode, modifiers);
	}
	
	@Override
	public boolean typeChar(char c, int modifiers) {
		if(selected != null) {
			selected.typeChar(c, modifiers);
			return true;
		}
		for(GuiElement element : subElements) {
			if(isElementEnabled.get(element)) {
				if(element.typeChar(c, modifiers)) {
					return true;
				}
			}
		}
		return super.typeChar(c, modifiers);
	}
	
	@Override
	public boolean scroll(double mx, double my, float scrollX, float scrollY, double delta) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		boolean hovered = mx >= x && mx < x + width && my >= y && my < y + height;
		float trueHeight = 0;
		for(GuiElement element : subElements) {
			if(isElementEnabled.get(element)) {
				if(selected != null && element == selected) {
					if(element.scroll(mx, my, scrollX, scrollY - (float)scroll.get() + trueHeight, delta)) {
						return true;
					}
				}
				if(hovered) {
					if(element.scroll(mx, my, scrollX, scrollY - (float)scroll.get() + trueHeight, delta)) {
						return true;
					}
				}
				trueHeight += element.getHeight();
			}
		}
		if(height >= maxHeight && hovered) {
			nextScroll -= delta * 10;
			return true;
		}
		return super.scroll(mx, my, scrollX, scrollY, delta);
	}
}
