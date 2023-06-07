package net.grilledham.hamhacks.gui.element.impl;

import net.grilledham.hamhacks.animation.Animation;
import net.grilledham.hamhacks.animation.AnimationType;
import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

public abstract class SettingElement<T> extends GuiElement {
	
	private final Animation tooltipAnimation = new Animation(AnimationType.LINEAR);
	
	private boolean hasClicked = false;
	
	protected final Get<String> getName;
	protected final Get<String> getTooltip;
	
	protected final Get<Boolean> shouldShow;
	
	protected final Get<T> get;
	protected final Set<T> set;
	
	protected final Runnable reset;
	
	public SettingElement(float x, float y, float width, double scale, Get<String> getName, Get<String> getTooltip, Get<Boolean> shouldShow, Get<T> get, Set<T> set, Runnable reset) {
		super(x, y, width, 16, scale);
		this.getName = getName;
		this.getTooltip = getTooltip;
		this.shouldShow = shouldShow;
		this.get = get;
		this.set = set;
		this.reset = reset;
	}
	
	@Override
	public void renderTop(DrawContext ctx, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		super.renderTop(ctx, mx, my, scrollX, scrollY, partialTicks);
		boolean hovered = mx >= x && mx < x + width && my >= y && my < y + height;
		
		if(getTooltip.get() != null && !getTooltip.get().equals("")) {
			if(tooltipAnimation.get() >= 1 && !hasClicked) {
				RenderUtil.drawToolTip(ctx, getName.get(), getTooltip.get(), mx, my, scale);
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
	
	public String getName() {
		return getName.get();
	}
	
	public boolean shouldShow() {
		return shouldShow.get();
	}
	
	@FunctionalInterface
	public interface Get<T> {
		T get();
	}
	
	@FunctionalInterface
	public interface Set<T> {
		void set(T value);
	}
}
