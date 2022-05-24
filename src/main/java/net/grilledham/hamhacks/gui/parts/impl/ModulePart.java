package net.grilledham.hamhacks.gui.parts.impl;

import net.grilledham.hamhacks.gui.parts.GuiPart;
import net.grilledham.hamhacks.gui.screens.ModuleSettingsScreen;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.render.ClickGUI;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

public class ModulePart extends GuiPart {
	
	private float hoverAnimation;
	private float enableAnimation;
	
	private float tooltipAnimation;
	
	private boolean hasClicked = false;
	
	private final Module module;
	private final Screen parent;
	
	public ModulePart(Screen parent, int x, int y, int width, int height, Module module) {
		super(x, y, width, height);
		this.module = module;
		this.parent = parent;
	}
	
	@Override
	public void render(MatrixStack stack, int mx, int my, float partialTicks) {
		stack.push();
		RenderUtil.preRender();
		
		int bgC = ClickGUI.getInstance().bgColor.getRGB();
		boolean hovered = mx >= x && mx < x + width && my >= y && my < y + height;
		bgC = RenderUtil.mix(ClickGUI.getInstance().bgColorHovered.getRGB(), bgC, hoverAnimation);
		RenderUtil.drawRect(stack, x + 1, y, width - 1, height, bgC);
		
		int barC = (ClickGUI.getInstance().accentColor.getRGB() & 0xff000000) + RenderUtil.mix(0x00a400, 0xa40000, enableAnimation);
		RenderUtil.drawRect(stack, x, y, 1, height, barC);
		
		mc.textRenderer.drawWithShadow(stack, module.getName(), x + 3, y + 4, ClickGUI.getInstance().textColor.getRGB());
		
		RenderUtil.postRender();
		stack.pop();
		
		if(hovered) {
			hoverAnimation += partialTicks / 5;
		} else {
			hoverAnimation -= partialTicks / 5;
		}
		hoverAnimation = Math.min(1, Math.max(0, hoverAnimation));
		
		if(module.isEnabled()) {
			enableAnimation += partialTicks / 5;
		} else {
			enableAnimation -= partialTicks / 5;
		}
		enableAnimation = Math.min(1, Math.max(0, enableAnimation));
		
		if(hovered) {
			tooltipAnimation += partialTicks / 20;
		} else {
			tooltipAnimation -= partialTicks / 20;
		}
		tooltipAnimation = Math.min(1, Math.max(0, tooltipAnimation));
		if(tooltipAnimation < 1) {
			hasClicked = false;
		}
	}
	
	@Override
	protected void renderTop(MatrixStack stack, int mx, int my, float partialTicks) {
		super.renderTop(stack, mx, my, partialTicks);
		if(module.hasToolTip()) {
			if(tooltipAnimation >= 1 && !hasClicked) {
				RenderUtil.drawToolTip(stack, module.getName(), module.getToolTip(), mx, my);
			}
		}
	}
	
	@Override
	public boolean release(double mx, double my, int button) {
		if(mx >= x && mx < x + width && my >= y && my < y + height) {
			hasClicked = true;
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				module.toggle();
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				mc.setScreen(new ModuleSettingsScreen(parent, module));
			}
			return true;
		}
		return super.release(mx, my, button);
	}
}
