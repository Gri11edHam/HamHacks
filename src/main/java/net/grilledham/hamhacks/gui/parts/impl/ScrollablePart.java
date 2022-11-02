package net.grilledham.hamhacks.gui.parts.impl;

import net.grilledham.hamhacks.animation.Animation;
import net.grilledham.hamhacks.animation.AnimationBuilder;
import net.grilledham.hamhacks.animation.AnimationType;
import net.grilledham.hamhacks.gui.parts.GuiPart;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.render.ClickGUI;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScrollablePart extends GuiPart {
	
	private float nextScroll;
	
	private final Animation scroll = AnimationBuilder.create(AnimationType.LINEAR, 0.05).build();
	
	private float maxHeight;
	
	private final List<GuiPart> subParts = new ArrayList<>();
	private final HashMap<GuiPart, Boolean> isPartEnabled = new HashMap<>();
	
	private GuiPart selected = null;
	
	public ScrollablePart(float x, float y, float width, float maxHeight) {
		super(x, y, width, 0);
		this.maxHeight = maxHeight;
	}
	
	public void setMaxHeight(float maxHeight) {
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
	protected void render(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		
		boolean hovered = mx >= x && my >= y && mx <= x + width && my <= y + height;
		
		stack.push();
		
		RenderUtil.adjustScissor(x, y, width, height, ModuleManager.getModule(ClickGUI.class).scale);
		RenderUtil.applyScissor();
		
		float trueHeight = 0;
		for(GuiPart part : subParts) {
			if(isPartEnabled.get(part)) {
				part.draw(stack, hovered || part == selected ? mx : -1, hovered || part == selected ? my : -1, scrollX, scrollY - (float)scroll.get() + trueHeight, partialTicks);
				trueHeight += part.getHeight();
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
	protected void renderTop(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		
		boolean hovered = mx >= x && my >= y && mx <= x + width && my <= y + height;
		
		stack.push();
		
		RenderUtil.pushScissor(x, y, width, height, ModuleManager.getModule(ClickGUI.class).scale);
		RenderUtil.applyScissor();
		
		float trueHeight = 0;
		for(GuiPart part : subParts) {
			if(isPartEnabled.get(part)) {
				part.drawTop(stack, hovered || part == selected ? mx : -1, hovered || part == selected ? my : -1, scrollX, scrollY - (float)scroll.get() + trueHeight, partialTicks);
				trueHeight += part.getHeight();
			}
		}
		
		RenderUtil.popScissor();
		
		stack.pop();
	}
	
	@Override
	public void moveBy(float x, float y) {
		super.moveBy(x, y);
		for(GuiPart part : subParts) {
			part.moveBy(x, y);
		}
	}
	
	@Override
	public void moveTo(float x, float y) {
		super.moveTo(x, y);
		for(GuiPart part : subParts) {
			part.moveTo(x, y);
		}
	}
	
	@Override
	public void resize(float maxW, float maxH) {
		super.resize(maxW, height);
		for(GuiPart part : subParts) {
			part.resize(maxW, part.getHeight());
		}
	}
	
	@Override
	public boolean click(double mx, double my, float scrollX, float scrollY, int button) {
		boolean hovered = mx >= x && my >= y && mx <= x + width && my <= y + height;
		float trueHeight = 0;
		for(GuiPart part : subParts) {
			if(isPartEnabled.get(part)) {
				if(selected != null) {
					if(part == selected) {
						selected.click(mx, my, scrollX, scrollY - (float)scroll.get() + trueHeight, button);
						return true;
					}
				} else {
					if(hovered) {
						if(part.click(mx, my, scrollX, scrollY - (float)scroll.get() + trueHeight, button)) {
							selected = part;
							return true;
						}
					}
				}
				trueHeight += part.getHeight();
			}
		}
		return super.click(mx, my, scrollX, scrollY - (float)scroll.get(), button);
	}
	
	@Override
	public boolean release(double mx, double my, float scrollX, float scrollY, int button) {
		boolean hovered = mx >= x && my >= y && mx <= x + width && my <= y + height;
		float trueHeight = 0;
		for(GuiPart part : subParts) {
			if(isPartEnabled.get(part)) {
				if(selected != null) {
					if(part == selected) {
						if(!selected.release(mx, my, scrollX, scrollY - (float)scroll.get() + trueHeight, button)) {
							selected = null;
						}
						return true;
					}
				} else {
					if(hovered) {
						if(part.release(mx, my, scrollX, scrollY - (float)scroll.get() + trueHeight, button)) {
							selected = part;
							return true;
						}
					}
				}
				trueHeight += part.getHeight();
			}
		}
		return super.release(mx, my, scrollX, scrollY - (float)scroll.get(), button);
	}
	
	@Override
	public boolean drag(double mx, double my, float scrollX, float scrollY, int button, double dx, double dy) {
		boolean hovered = mx >= x && my >= y && mx <= x + width && my <= y + height;
		float trueHeight = 0;
		for(GuiPart part : subParts) {
			if(isPartEnabled.get(part)) {
				if(selected != null) {
					if(part == selected) {
						selected.drag(mx, my, scrollX, scrollY - (float)scroll.get() + trueHeight, button, dx, dy);
						return true;
					}
				} else {
					if(hovered) {
						if(part.drag(mx, my, scrollX, scrollY - (float)scroll.get() + trueHeight, button, dx, dy)) {
							return true;
						}
					}
				}
				trueHeight += part.getHeight();
			}
		}
		return super.drag(mx, my, scrollX, scrollY - (float)scroll.get(), button, dx, dy);
	}
	
	@Override
	public boolean type(int code, int scanCode, int modifiers) {
		if(selected != null) {
			if(!selected.type(code, scanCode, modifiers)) {
				selected = null;
			}
			return true;
		}
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
		if(selected != null) {
			selected.typeChar(c, modifiers);
			return true;
		}
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
	public boolean scroll(double mx, double my, float scrollX, float scrollY, double delta) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		if(mx >= x && mx < x + width && my >= y && my < y + height) {
			float trueHeight = 0;
			for(GuiPart part : subParts) {
				if(isPartEnabled.get(part)) {
					if(part.scroll(mx, my, scrollX, scrollY - (float)scroll.get() + trueHeight, delta)) {
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
