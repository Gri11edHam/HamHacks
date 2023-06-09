package net.grilledham.hamhacks.gui.element.impl;

import net.grilledham.hamhacks.animation.Animation;
import net.grilledham.hamhacks.animation.AnimationType;
import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.gui.screen.impl.ModuleSettingsScreen;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

public class ModuleElement extends GuiElement {
	
	private final Animation hoverAnimation = new Animation(AnimationType.EASE, 0.25, true);
	private final Animation enableAnimation = new Animation(AnimationType.EASE_IN_OUT, 0.25);
	private final Animation overflowAnimation = new Animation(AnimationType.EASE_IN_OUT, 0.25);
	
	private boolean hasClicked = false;
	
	private final Module module;
	private final Screen parent;
	
	public ModuleElement(Screen parent, float x, float y, float width, float height, double scale, Module module) {
		super(x, y, width, height, scale);
		this.module = module;
		this.parent = parent;
		enableAnimation.setAbsolute(module.isEnabled());
	}
	
	@Override
	public void draw(DrawContext ctx, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		MatrixStack stack = ctx.getMatrices();
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		stack.push();
		RenderUtil.preRender();
		RenderUtil.pushScissor(x, y, width, height + 1, (float)scale);
		
		ClickGUI ui = PageManager.getPage(ClickGUI.class);
		int bgC = ui.bgColor.get().getRGB();
		boolean hovered = mx >= x && mx < x + width && my >= y && my < y + height;
		bgC = RenderUtil.mix(ui.bgColorHovered.get().getRGB(), bgC, hoverAnimation.get());
		int bgCEnabled = ui.enabledColor.get().getRGB();
		bgCEnabled = RenderUtil.mix(ui.enabledColorHovered.get().getRGB(), bgCEnabled, hoverAnimation.get());
		float drawWidth = showTooltip ? Math.max(mc.textRenderer.getWidth(module.getName()) + 6, width) : width;
		RenderUtil.drawRect(stack, (float)(x + drawWidth * enableAnimation.get()), y, (float)(drawWidth * (1 - enableAnimation.get())), height, bgC);
		RenderUtil.drawRect(stack, x, y, (float)(drawWidth * enableAnimation.get()), height, bgCEnabled);
		
		RenderUtil.pushScissor(x + 2, y + 3, width - 4, 11, (float)scale);
		RenderUtil.drawString(ctx, module.getName(), x + 3, y + 4, ui.textColor.get().getRGB(), true);
		RenderUtil.popScissor();
		
		RenderUtil.popScissor();
		RenderUtil.postRender();
		stack.pop();
		
		hoverAnimation.set(hovered);
		hoverAnimation.update();
		
		enableAnimation.set(module.isEnabled());
		enableAnimation.update();
		
		if(module.hasToolTip()) {
			if(hasClicked) {
				setTooltip("", "");
			} else {
				setTooltip(module.getName(), module.getToolTip());
			}
		}
		
		if(!showTooltip) {
			hasClicked = false;
		}
	}
	
	@Override
	public void drawTop(DrawContext ctx, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		super.drawTop(ctx, mx, my, scrollX, scrollY, partialTicks);
		
		if(hoverAnimation.get() == 1) {
			MatrixStack stack = ctx.getMatrices();
			float x = this.x + scrollX;
			float y = this.y + scrollY;
			stack.push();
			RenderUtil.preRender();
			float drawWidth = Math.max(mc.textRenderer.getWidth(module.getName()) + 6, width);
			RenderUtil.pushScissor(x + width, y, drawWidth - width, height + 1, (float)scale);
			
			ClickGUI ui = PageManager.getPage(ClickGUI.class);
			int bgC = ui.bgColor.get().getRGB();
			bgC = RenderUtil.mix(ui.bgColorHovered.get().getRGB(), bgC, hoverAnimation.get());
			int bgCEnabled = ui.enabledColor.get().getRGB();
			bgCEnabled = RenderUtil.mix(ui.enabledColorHovered.get().getRGB(), bgCEnabled, hoverAnimation.get());
			int transparency = (int)(overflowAnimation.get() * 0xff) << 24;
			bgC = bgC & 0xffffff + transparency;
			bgCEnabled = bgCEnabled & 0xffffff + transparency;
			RenderUtil.drawRect(stack, (float)(x + drawWidth * enableAnimation.get()), y, (float)(drawWidth * (1 - enableAnimation.get())), height, bgC);
			RenderUtil.drawRect(stack, x, y, (float)(drawWidth * enableAnimation.get()), height, bgCEnabled);
			
			RenderUtil.pushScissor(x + width - 2, y + 3, width - 4, 11, (float)scale);
			RenderUtil.drawString(ctx, module.getName(), x + 3, y + 4, ui.textColor.get().getRGB(), true);
			RenderUtil.popScissor();
			
			RenderUtil.popScissor();
			RenderUtil.postRender();
			stack.pop();
		}
		
		overflowAnimation.set(hoverAnimation.get() == 1);
		overflowAnimation.update();
	}
	
	@Override
	public boolean release(double mx, double my, float scrollX, float scrollY, int button) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		if(mx >= x && mx < x + width && my >= y && my < y + height) {
			hasClicked = true;
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				module.toggle();
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				mc.setScreen(new ModuleSettingsScreen(parent, module, scale));
			}
		}
		return super.release(mx, my, scrollX, scrollY, button);
	}
}
