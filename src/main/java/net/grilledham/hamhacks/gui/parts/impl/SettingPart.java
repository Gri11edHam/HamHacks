package net.grilledham.hamhacks.gui.parts.impl;

import net.grilledham.hamhacks.gui.parts.GuiPart;
import net.grilledham.hamhacks.util.RenderUtil;
import net.grilledham.hamhacks.util.setting.Setting;
import net.minecraft.client.util.math.MatrixStack;

public abstract class SettingPart extends GuiPart {
	
	private float tooltipAnimation;
	
	private boolean hasClicked = false;
	
	private Setting<?> setting;
	
	public SettingPart(int x, int y, int width, Setting<?> setting) {
		super(x, y, width, 16);
		this.setting = setting;
	}
	
	@Override
	protected void renderTop(MatrixStack stack, int mx, int my, float partialTicks) {
		super.renderTop(stack, mx, my, partialTicks);
		boolean hovered = mx >= x && mx < x + width && my >= y && my < y + height;
		
		if(setting.hasToolTip()) {
			if(tooltipAnimation >= 1 && !hasClicked) {
				RenderUtil.drawToolTip(stack, setting.getName(), setting.getToolTip(), mx, my);
			}
		}
		
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
	public boolean release(double mx, double my, int button) {
		if(mx >= x && mx < x + width && my >= y && my < y + height) {
			hasClicked = true;
		}
		return super.release(mx, my, button);
	}
}
