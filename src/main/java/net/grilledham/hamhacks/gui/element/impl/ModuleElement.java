package net.grilledham.hamhacks.gui.element.impl;

import net.grilledham.hamhacks.animation.Animation;
import net.grilledham.hamhacks.animation.AnimationBuilder;
import net.grilledham.hamhacks.animation.AnimationType;
import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.gui.screen.impl.ModuleSettingsScreen;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

public class ModuleElement extends GuiElement {
	
	private final Animation hoverAnimation = AnimationBuilder.create(AnimationType.IN_OUT_QUAD, 0.25).build();
	private final Animation enableAnimation = AnimationBuilder.create(AnimationType.IN_OUT_QUAD, 0.25).build();
	
	private final Animation tooltipAnimation = AnimationBuilder.create(AnimationType.LINEAR).build();
	
	private boolean hasClicked = false;
	
	private final Module module;
	private final Screen parent;
	
	public ModuleElement(Screen parent, float x, float y, float width, float height, float scale, Module module) {
		super(x, y, width, height, scale);
		this.module = module;
		this.parent = parent;
		enableAnimation.setAbsolute(module.isEnabled());
	}
	
	@Override
	public void render(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		stack.push();
		RenderUtil.preRender();
		
		ClickGUI ui = PageManager.getPage(ClickGUI.class);
		int bgC = ui.bgColor.getRGB();
		boolean hovered = mx >= x && mx < x + width && my >= y && my < y + height;
		bgC = RenderUtil.mix(ui.bgColorHovered.getRGB(), bgC, hoverAnimation.get());
		int bgCEnabled = ui.enabledColor.getRGB();
		bgCEnabled = RenderUtil.mix(ui.enabledColorHovered.getRGB(), bgCEnabled, hoverAnimation.get());
		RenderUtil.drawRect(stack, (float)(x + width * enableAnimation.get()), y, (float)(width * (1 - enableAnimation.get())), height, bgC);
		RenderUtil.drawRect(stack, x, y, (float)(width * enableAnimation.get()), height, bgCEnabled);
		
		mc.textRenderer.drawWithShadow(stack, module.getName(), x + 3, y + 4, ui.textColor.getRGB());
		
		RenderUtil.postRender();
		stack.pop();
		
		hoverAnimation.set(hovered);
		hoverAnimation.update();
		
		enableAnimation.set(module.isEnabled());
		enableAnimation.update();
		
		tooltipAnimation.set(hovered);
		tooltipAnimation.update();
		if(tooltipAnimation.get() < 1) {
			hasClicked = false;
		}
	}
	
	@Override
	public void renderTop(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		super.renderTop(stack, mx, my, scrollX, scrollY, partialTicks);
		if(module.hasToolTip()) {
			if(tooltipAnimation.get() >= 1 && !hasClicked) {
				RenderUtil.drawToolTip(stack, module.getName(), module.getToolTip(), mx, my, scale);
			}
		}
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
