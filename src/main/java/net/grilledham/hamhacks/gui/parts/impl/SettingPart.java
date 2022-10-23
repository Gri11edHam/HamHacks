package net.grilledham.hamhacks.gui.parts.impl;

import net.grilledham.hamhacks.gui.parts.GuiPart;
import net.grilledham.hamhacks.util.RenderUtil;
import net.grilledham.hamhacks.util.animation.Animation;
import net.grilledham.hamhacks.util.animation.AnimationBuilder;
import net.grilledham.hamhacks.util.animation.AnimationType;
import net.grilledham.hamhacks.util.setting.SettingHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.lang.reflect.Field;

public abstract class SettingPart extends GuiPart {
	
	private final Animation tooltipAnimation = AnimationBuilder.create(AnimationType.LINEAR).build();
	
	private boolean hasClicked = false;
	
	protected final Field setting;
	protected final Object obj;
	
	public SettingPart(float x, float y, float width, Field setting, Object obj) {
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
	protected void renderTop(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		super.renderTop(stack, mx, my, scrollX, scrollY, partialTicks);
		boolean hovered = mx >= x && mx < x + width && my >= y && my < y + height;
		
		if(setting != null && obj != null) {
			if(SettingHelper.hasTooltip(setting)) {
				if(tooltipAnimation.get() >= 1 && !hasClicked) {
					RenderUtil.drawToolTip(stack, SettingHelper.getName(setting).getString(), SettingHelper.getTooltip(setting).getString(), mx, my);
				}
			}
		}
		
		tooltipAnimation.set(hovered);
		tooltipAnimation.update();
		if(tooltipAnimation.get() < 1) {
			hasClicked = false;
		}
	}
	
	@Override
	public boolean release(double mx, double my, float scrollX, float scrollY, int button) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		if(mx >= x && mx < x + width && my >= y && my < y + height) {
			hasClicked = true;
		}
		return super.release(mx, my, scrollX, scrollY, button);
	}
}
