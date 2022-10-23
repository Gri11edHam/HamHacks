package net.grilledham.hamhacks.gui.parts.impl;

import net.grilledham.hamhacks.gui.parts.GuiPart;
import net.grilledham.hamhacks.gui.screens.ModuleSettingsScreen;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.render.ClickGUI;
import net.grilledham.hamhacks.util.RenderUtil;
import net.grilledham.hamhacks.util.animation.Animation;
import net.grilledham.hamhacks.util.animation.AnimationBuilder;
import net.grilledham.hamhacks.util.animation.AnimationType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

public class ModulePart extends GuiPart {
	
	private final Animation hoverAnimation = AnimationBuilder.create(AnimationType.IN_OUT_QUAD, 0.25).build();
	private final Animation enableAnimation = AnimationBuilder.create(AnimationType.IN_OUT_QUAD, 0.25).build();
	
	private final Animation tooltipAnimation = AnimationBuilder.create(AnimationType.LINEAR).build();
	
	private boolean hasClicked = false;
	
	private final Module module;
	private final Screen parent;
	
	public ModulePart(Screen parent, float x, float y, float width, float height, Module module) {
		super(x, y, width, height);
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
		
		int bgC = ModuleManager.getModule(ClickGUI.class).bgColor.getRGB();
		boolean hovered = mx >= x && mx < x + width && my >= y && my < y + height;
		bgC = RenderUtil.mix(ModuleManager.getModule(ClickGUI.class).bgColorHovered.getRGB(), bgC, hoverAnimation.get());
		int bgCEnabled = ModuleManager.getModule(ClickGUI.class).enabledColor.getRGB();
		bgCEnabled = RenderUtil.mix(ModuleManager.getModule(ClickGUI.class).enabledColorHovered.getRGB(), bgCEnabled, hoverAnimation.get());
		RenderUtil.drawRect(stack, (float)(x + width * enableAnimation.get()), y, (float)(width * (1 - enableAnimation.get())), height, bgC);
		RenderUtil.drawRect(stack, x, y, (float)(width * enableAnimation.get()), height, bgCEnabled);
		
		mc.textRenderer.drawWithShadow(stack, module.getName(), x + 3, y + 4, ModuleManager.getModule(ClickGUI.class).textColor.getRGB());
		
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
	protected void renderTop(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		super.renderTop(stack, mx, my, scrollX, scrollY, partialTicks);
		if(module.hasToolTip()) {
			if(tooltipAnimation.get() >= 1 && !hasClicked) {
				RenderUtil.drawToolTip(stack, module.getName(), module.getToolTip(), mx, my);
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
				mc.setScreen(new ModuleSettingsScreen(parent, module));
			}
		}
		return super.release(mx, my, scrollX, scrollY, button);
	}
}
