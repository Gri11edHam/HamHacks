package net.grilledham.hamhacks.gui.element.impl;

import net.grilledham.hamhacks.animation.Animation;
import net.grilledham.hamhacks.animation.AnimationBuilder;
import net.grilledham.hamhacks.animation.AnimationType;
import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.grilledham.hamhacks.setting.SettingHelper;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SettingCategoryElement extends GuiElement {
	
	private final Object obj;
	private final Text category;
	
	private final Animation openCloseAnimation = AnimationBuilder.create(AnimationType.IN_OUT_QUAD, 0.25).build();
	private final Animation hoverAnimation = AnimationBuilder.create(AnimationType.IN_OUT_QUAD, 0.25).build();
	
	private final List<GuiElement> subElements = new ArrayList<>();
	private final HashMap<GuiElement, Boolean> isElementEnabled = new HashMap<>();
	private GuiElement selected = null;
	
	private float elementsHeight = 0;
	private final float collapsedHeight = 19;
	
	public SettingCategoryElement(Object obj, Text category, float x, float y, float scale) {
		super(x, y, MinecraftClient.getInstance().textRenderer.getWidth(category) + 12 + 8, 0, scale);
		this.obj = obj;
		this.category = category;
	}
	
	public void addElement(GuiElement element) {
		subElements.add(element);
		isElementEnabled.put(element, true);
		updateSize();
	}
	
	public void addElement(GuiElement element, int i) {
		subElements.add(i, element);
		isElementEnabled.put(element, true);
		updateSize();
	}
	
	public void removeElement(GuiElement element) {
		subElements.remove(element);
		isElementEnabled.remove(element);
		updateSize();
	}
	
	public void setEnabled(GuiElement element, boolean enabled) {
		isElementEnabled.put(element, enabled);
		updateSize();
	}
	
	public List<GuiElement> getElements() {
		return subElements;
	}
	
	private void updateSize() {
		float newHeight = 0;
		float maxWidth = 0;
		for(GuiElement element : subElements) {
			if(isElementEnabled.get(element)) {
				newHeight += element.getHeight();
				if(element.getWidth() > maxWidth) {
					maxWidth = element.getWidth();
				}
			}
		}
		width = Math.max(maxWidth, preferredWidth);
		elementsHeight = newHeight;
		height = elementsHeight + collapsedHeight;
	}
	
	@Override
	public void render(MatrixStack stack, int mx, int my, float offX, float offY, float tickDelta) {
		float x = this.x + offX;
		float y = this.y + offY;
		stack.push();
		
		RenderUtil.adjustScissor(x, y, width, (float)(collapsedHeight + elementsHeight * openCloseAnimation.get()), scale);
		RenderUtil.applyScissor();
		RenderUtil.preRender();
		
		ClickGUI ui = PageManager.getPage(ClickGUI.class);
		int bgC = ui.bgColor.getRGB();
		boolean hovered = mx >= x && mx < x + width && my >= y && my < y + collapsedHeight;
		RenderUtil.drawRect(stack, x, y, width, collapsedHeight, bgC);
		float lineX = x + 6 + mc.textRenderer.getWidth(category);
		float arrowWidth = 8;
		float lineW = width - 6 - mc.textRenderer.getWidth(category) - (arrowWidth + 8);
		RenderUtil.drawRect(stack, lineX - (category.getString().equals("") ? 3 : 0), y + 9, lineW + (category.getString().equals("") ? 3 : 0), 2, ui.textColor.getRGB());
		
		stack.push();
		stack.translate(lineX + lineW + arrowWidth / 2 + 4, y + 11, 0);
		stack.multiply(Quaternion.fromEulerXyzDegrees(new Vec3f(0, 0, (float)(-90 * openCloseAnimation.get()))));
		stack.translate(-lineX - lineW - arrowWidth / 2 - 4, -y - 11, 0);
		mc.textRenderer.drawWithShadow(stack, "<", lineX + lineW + 6, y + 7, ui.textColor.getRGB());
		stack.pop();
		
		mc.textRenderer.drawWithShadow(stack, category, x + 3, y + 7, ui.textColor.getRGB());
		
		float trueHeight = 0;
		for(GuiElement element : subElements) {
			if(isElementEnabled.get(element)) {
				element.render(stack, mx, my, offX, offY + collapsedHeight + trueHeight, tickDelta);
				trueHeight += element.getHeight();
			}
		}
		
		RenderUtil.popScissor();
		RenderUtil.postRender();
		
		stack.pop();
		
		height = (float)(collapsedHeight + (trueHeight * openCloseAnimation.get()));
		
		openCloseAnimation.set(SettingHelper.isExpanded(obj, category));
		openCloseAnimation.update();
		
		hoverAnimation.set(hovered);
		hoverAnimation.update();
	}
	
	@Override
	public void renderTop(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		
		stack.push();
		
		RenderUtil.pushScissor(x, y, width, height, scale);
		RenderUtil.applyScissor();
		
		float trueHeight = 0;
		for(GuiElement element : subElements) {
			if(isElementEnabled.get(element)) {
				element.renderTop(stack, mx, my, scrollX, scrollY + collapsedHeight + trueHeight, partialTicks);
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
	public boolean click(double mx, double my, float offX, float offY, int button) {
		if(openCloseAnimation.get() < 0.5) {
			return false;
		}
		float trueHeight = 0;
		for(GuiElement element : subElements) {
			if(isElementEnabled.get(element)) {
				if(selected != null) {
					if(element == selected) {
						selected.click(mx, my, offX, offY + trueHeight + collapsedHeight, button);
						return true;
					}
				} else {
					if(element.click(mx, my, offX, offY + trueHeight + collapsedHeight, button)) {
						selected = element;
						return true;
					}
				}
				trueHeight += element.getHeight();
			}
		}
		return super.click(mx, my, offX, offY, button);
	}
	
	@Override
	public boolean release(double mx, double my, float offX, float offY, int button) {
		float x = this.x + offX;
		float y = this.y + offY;
		if(openCloseAnimation.get() >= 0.5) {
			float trueHeight = 0;
			for(GuiElement element : subElements) {
				if(isElementEnabled.get(element)) {
					if(selected != null) {
						if(element == selected) {
							if(!selected.release(mx, my, offX, offY + trueHeight + collapsedHeight, button)) {
								selected = null;
								return false;
							}
							return true;
						}
					} else {
						if(element.release(mx, my, offX, offY + trueHeight + collapsedHeight, button)) {
							selected = element;
							return true;
						}
					}
					trueHeight += element.getHeight();
				}
			}
		}
		if(mx >= x && mx <= x + width && my >= y && my <= y + collapsedHeight) {
			SettingHelper.setExpanded(obj, category, !SettingHelper.isExpanded(obj, category));
			return false;
		}
		return super.release(mx, my, offX, offY, button);
	}
	
	@Override
	public boolean drag(double mx, double my, float scrollX, float scrollY, int button, double dx, double dy) {
		if(openCloseAnimation.get() < 0.5) {
			return false;
		}
		float trueHeight = 0;
		for(GuiElement element : subElements) {
			if(isElementEnabled.get(element)) {
				if(selected != null) {
					if(element == selected) {
						selected.drag(mx, my, scrollX, scrollY + trueHeight + collapsedHeight, button, dx, dy);
						return true;
					}
				} else {
					if(element.drag(mx, my, scrollX, scrollY + trueHeight + collapsedHeight, button, dx, dy)) {
						return true;
					}
				}
				trueHeight += element.getHeight();
			}
		}
		return super.drag(mx, my, scrollX, scrollY, button, dx, dy);
	}
	
	@Override
	public boolean type(int code, int scanCode, int modifiers) {
		if(openCloseAnimation.get() < 0.5) {
			return false;
		}
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
		if(openCloseAnimation.get() < 0.5) {
			return false;
		}
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
		if(openCloseAnimation.get() < 0.5) {
			return false;
		}
		float trueHeight = 0;
		for(GuiElement element : subElements) {
			if(isElementEnabled.get(element)) {
				if(element.scroll(mx, my, scrollX, scrollY + trueHeight + collapsedHeight, delta)) {
					return true;
				}
				trueHeight += element.getHeight();
			}
		}
		return super.scroll(mx, my, scrollX, scrollY, delta);
	}
}
