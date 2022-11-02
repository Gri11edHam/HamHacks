package net.grilledham.hamhacks.gui.element.impl;

import net.grilledham.hamhacks.animation.Animation;
import net.grilledham.hamhacks.animation.AnimationBuilder;
import net.grilledham.hamhacks.animation.AnimationType;
import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.setting.SettingHelper;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.util.math.MatrixStack;

import java.lang.reflect.Field;

public abstract class SettingElement extends GuiElement {
	
	private final Animation tooltipAnimation = AnimationBuilder.create(AnimationType.LINEAR).build();
	
	private boolean hasClicked = false;
	
	protected final Field setting;
	protected final Object obj;
	
	public SettingElement(float x, float y, float width, float scale, Field setting, Object obj) {
		super(x, y, width, 16, scale);
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
	public void renderTop(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		super.renderTop(stack, mx, my, scrollX, scrollY, partialTicks);
		boolean hovered = mx >= x && mx < x + width && my >= y && my < y + height;
		
		if(setting != null && obj != null) {
			if(SettingHelper.hasTooltip(setting)) {
				if(tooltipAnimation.get() >= 1 && !hasClicked) {
					RenderUtil.drawToolTip(stack, SettingHelper.getName(setting).getString(), SettingHelper.getTooltip(setting).getString(), mx, my, scale);
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
