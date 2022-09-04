package net.grilledham.hamhacks.gui.parts.impl;

import net.grilledham.hamhacks.gui.parts.GuiPart;
import net.grilledham.hamhacks.util.RenderUtil;
import net.grilledham.hamhacks.util.setting.SettingHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.lang.reflect.Field;

public abstract class SettingPart extends GuiPart {
	
	private float tooltipAnimation;
	
	private boolean hasClicked = false;
	
	protected final Field setting;
	protected final Object obj;
	
	public SettingPart(int x, int y, int width, Field setting, Object obj) {
		super(x, y, width, 16);
		this.setting = setting;
		this.obj = obj;
	}
	
	public Field getSetting() {
		return setting;
	}
	
	public Object getObject() {
		return obj;
	}
	
	@Override
	protected void renderTop(MatrixStack stack, int mx, int my, int scrollX, int scrollY, float partialTicks) {
		int x = this.x + scrollX;
		int y = this.y + scrollY;
		super.renderTop(stack, mx, my, scrollX, scrollY, partialTicks);
		boolean hovered = mx >= x && mx < x + width && my >= y && my < y + height;
		
		if(setting != null && obj != null) {
			if(SettingHelper.hasTooltip(setting)) {
				if(tooltipAnimation >= 1 && !hasClicked) {
					RenderUtil.drawToolTip(stack, SettingHelper.getName(setting).getString(), SettingHelper.getTooltip(setting).getString(), mx, my);
				}
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
	public boolean release(double mx, double my, int scrollX, int scrollY, int button) {
		int x = this.x + scrollX;
		int y = this.y + scrollY;
		if(mx >= x && mx < x + width && my >= y && my < y + height) {
			hasClicked = true;
		}
		return super.release(mx, my, scrollX, scrollY, button);
	}
}
