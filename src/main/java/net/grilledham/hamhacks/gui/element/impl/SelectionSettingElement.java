package net.grilledham.hamhacks.gui.element.impl;

import net.grilledham.hamhacks.animation.Animation;
import net.grilledham.hamhacks.animation.AnimationBuilder;
import net.grilledham.hamhacks.animation.AnimationType;
import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.grilledham.hamhacks.setting.SelectionSetting;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class SelectionSettingElement extends SettingElement<Integer> {
	
	private final Animation hoverAnimation = AnimationBuilder.create(AnimationType.IN_OUT_QUAD, 0.25).build();
	private final Animation selectionAnimation = AnimationBuilder.create(AnimationType.IN_OUT_QUAD, 0.25).build();
	
	private boolean selected = false;
	
	private final List<GuiElement> elements = new ArrayList<>();
	
	private float maxWidth;
	
	protected final Get<String[]> options;
	
	public SelectionSettingElement(float x, float y, double scale, SelectionSetting setting) {
		this(x, y, scale, setting::getName, setting.hasTooltip() ? setting::getTooltip : () -> "", setting::shouldShow, setting::get, setting::set, setting::reset, setting::options);
	}
	
	public SelectionSettingElement(float x, float y, double scale, Get<String> getName, Get<String> getTooltip, Get<Boolean> shouldShow, Get<Integer> get, Set<Integer> set, Runnable reset, Get<String[]> options) {
		super(x, y, MinecraftClient.getInstance().textRenderer.getWidth(getName.get()), scale, getName, getTooltip, shouldShow, get, set, reset);
		this.options = options;
		maxWidth = 0;
		GuiElement element;
		int i = 0;
		for(String string : (options.get())) {
			String s = Text.translatable(string).getString();
			int finalI = i;
			elements.add(element = new ButtonElement(s, 0, 0, mc.textRenderer.getWidth(s) + 4, 16, scale, () -> {
				set.set(finalI);
			}));
			if(maxWidth < element.getWidth()) {
				maxWidth = element.getWidth();
			}
			i++;
		}
		resize(MinecraftClient.getInstance().textRenderer.getWidth(getName.get()) + maxWidth + 6, 16);
		int yAdd = 0;
		for(GuiElement guiElement : elements) {
			guiElement.moveTo(x + width - maxWidth, y + yAdd);
			guiElement.resize(maxWidth, guiElement.getHeight());
			yAdd += guiElement.getHeight();
		}
	}
	
	@Override
	public void moveTo(float x, float y) {
		super.moveTo(x, y);
		int yAdd = 0;
		for(GuiElement element : elements) {
			element.moveTo(x + width - maxWidth, y + yAdd);
			yAdd += element.getHeight();
		}
	}
	
	@Override
	public void moveBy(float x, float y) {
		super.moveBy(x, y);
		for(GuiElement element : elements) {
			element.moveBy(x, y);
		}
	}
	
	@Override
	public void resize(float maxW, float maxH) {
		super.resize(maxW, maxH);
		int yAdd = 0;
		for(GuiElement element : elements) {
			element.moveTo(x + width - maxWidth, y + yAdd);
			yAdd += element.getHeight();
		}
	}
	
	@Override
	public void render(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
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
		
		mc.textRenderer.drawWithShadow(stack, getName.get(), x + 2, y + 4, ui.textColor.get().getRGB());
		String text = Text.translatable(options.get()[get.get()]).getString();
		mc.textRenderer.drawWithShadow(stack, text, x + width - mc.textRenderer.getWidth(text) - 2, y + 4, ui.textColor.get().getRGB());
		
		RenderUtil.postRender();
		stack.pop();
		
		hoverAnimation.set(hovered);
		hoverAnimation.update();
		
		selectionAnimation.set(selected);
		selectionAnimation.update();
	}
	
	@Override
	public void renderTop(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		stack.push();
		RenderUtil.preRender();
		RenderUtil.pushScissor(x, y, width, (height * elements.size()) * (float)selectionAnimation.get(), (float)scale);
		RenderUtil.applyScissor();
		
		for(GuiElement element : elements) {
			element.render(stack, mx, my, scrollX, scrollY, partialTicks);
		}
		
		RenderUtil.postRender();
		RenderUtil.popScissor();
		stack.pop();
		super.renderTop(stack, mx, my, scrollX, scrollY, partialTicks);
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
			for(GuiElement element : elements) {
				element.release(mx, my, scrollX, scrollY, button);
			}
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
}
