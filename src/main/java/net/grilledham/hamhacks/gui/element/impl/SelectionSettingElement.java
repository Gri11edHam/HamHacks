package net.grilledham.hamhacks.gui.element.impl;

import net.grilledham.hamhacks.animation.Animation;
import net.grilledham.hamhacks.animation.AnimationType;
import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.grilledham.hamhacks.setting.SelectionSetting;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class SelectionSettingElement extends SettingElement<Integer> {
	
	private static final int PREFERED_SCROLL_HEIGHT = 200;
	private final Animation hoverAnimation = new Animation(AnimationType.EASE, 0.25, true);
	private final Animation selectionAnimation = new Animation(AnimationType.EASE_IN_OUT, 0.25);
	
	private boolean selected = false;
	
	private final ScrollableElement scrollArea;
	
	private float maxWidth;
	
	protected final Get<String[]> options;
	
	public SelectionSettingElement(float x, float y, double scale, SelectionSetting setting) {
		this(x, y, scale, setting::getName, setting.hasTooltip() ? setting::getTooltip : () -> "", setting::shouldShow, setting::get, setting::set, setting::reset, setting::options);
	}
	
	public SelectionSettingElement(float x, float y, double scale, Get<String> getName, Get<String> getTooltip, Get<Boolean> shouldShow, Get<Integer> get, Set<Integer> set, Runnable reset, Get<String[]> options) {
		super(x, y, RenderUtil.getStringWidth(getName.get()), scale, getName, getTooltip, shouldShow, get, set, reset);
		this.options = options;
		maxWidth = 40;
		GuiElement element;
		int i = 0;
		scrollArea = new ScrollableElement(0, 0, 40, PREFERED_SCROLL_HEIGHT, scale);
		for(String string : (options.get())) {
			String s = Text.translatable(string).getString();
			int finalI = i;
			scrollArea.addElement(element = new ButtonElement(s, 0, 0, RenderUtil.getStringWidth(s) + 4, 16, scale, () -> {
				set.set(finalI);
			}));
			if(maxWidth < element.getWidth()) {
				maxWidth = element.getWidth();
			}
			i++;
		}
		resize(RenderUtil.getStringWidth(getName.get()) + maxWidth + 6, 16);
		scrollArea.resize(maxWidth, PREFERED_SCROLL_HEIGHT);
		scrollArea.moveTo(x + width - maxWidth, y);
	}
	
	@Override
	public void moveTo(float x, float y) {
		super.moveTo(x, y);
		scrollArea.moveTo(x + width - maxWidth, y);
	}
	
	@Override
	public void moveBy(float x, float y) {
		super.moveBy(x, y);
		scrollArea.moveBy(x, y);
	}
	
	@Override
	public void resize(float maxW, float maxH) {
		super.resize(maxW, maxH);
		scrollArea.moveTo(x + width - maxWidth, y);
	}
	
	@Override
	public void draw(DrawContext ctx, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		MatrixStack stack = ctx.getMatrices();
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		stack.push();
		RenderUtil.preRender();
		
		ClickGUI ui = PageManager.getPage(ClickGUI.class);
		int bgC = ui.bgColor.get().getRGB();
		RenderUtil.drawRect(stack, x, y, width - maxWidth, height, bgC);
		
		boolean hovered = mx >= x + width - maxWidth && mx < x + width && my >= y && my < y + height;
		bgC = RenderUtil.mix(ui.bgColorHovered.get().getRGB(), bgC, hoverAnimation.get());
		RenderUtil.drawRect(stack, x + width - maxWidth, y, maxWidth, height, bgC);
		
		int outlineC = 0xffcccccc;
		RenderUtil.drawHRect(stack, x + width - maxWidth, y, maxWidth, height, outlineC);
		
		RenderUtil.drawString(ctx, getName.get(), x + 2, y + 4, ui.textColor.get().getRGB(), true);
		String text = Text.translatable(options.get()[get.get()]).getString();
		RenderUtil.drawString(ctx, text, x + width - RenderUtil.getStringWidth(text) - 2, y + 4, ui.textColor.get().getRGB(), true);
		
		RenderUtil.postRender();
		stack.pop();
		
		scrollArea.setMaxHeight((float)Math.min(PREFERED_SCROLL_HEIGHT, (mc.getWindow().getHeight() / scale) - scrollArea.getY() - scrollY));
		
		hoverAnimation.set(hovered);
		hoverAnimation.update();
		
		selectionAnimation.set(selected);
		selectionAnimation.update();
	}
	
	@Override
	public void drawTop(DrawContext ctx, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		MatrixStack stack = ctx.getMatrices();
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		stack.push();
		stack.translate(0, 0, 1);
		RenderUtil.preRender();
		RenderUtil.pushScissor(x, y, width, scrollArea.getHeight() * (float)selectionAnimation.get(), (float)scale);
		
		scrollArea.render(ctx, mx, my, scrollX, scrollY, partialTicks);
		
		RenderUtil.postRender();
		RenderUtil.popScissor();
		stack.pop();
		super.drawTop(ctx, mx, my, scrollX, scrollY, partialTicks);
	}
	
	@Override
	public boolean click(double mx, double my, float scrollX, float scrollY, int button) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		if(selected) {
			return true;
		}
		if(mx >= x + width - maxWidth && mx < x + width && my >= y && my < y + height) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			
			}
		}
		return super.click(mx, my, scrollX, scrollY, button);
	}
	
	@Override
	public boolean release(double mx, double my, float scrollX, float scrollY, int button) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		super.release(mx, my, scrollX, scrollY, button);
		if(selected) {
			selected = false;
			scrollArea.release(mx, my, scrollX, scrollY, button);
			return false;
		} else if(mx >= x + width - maxWidth && mx < x + width && my >= y && my < y + height) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				selected = true;
				return true;
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				reset.run();
				return false;
			}
		}
		return super.release(mx, my, scrollX, scrollY, button);
	}
	
	@Override
	public boolean scroll(double mx, double my, float scrollX, float scrollY, double delta) {
		if(scrollArea.scroll(mx, my, scrollX, scrollY, delta)) {
			return true;
		}
		return super.scroll(mx, my, scrollX, scrollY, delta);
	}
}
