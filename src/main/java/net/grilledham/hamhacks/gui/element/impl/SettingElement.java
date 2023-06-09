package net.grilledham.hamhacks.gui.element.impl;

import net.grilledham.hamhacks.gui.element.GuiElement;
import net.minecraft.client.gui.DrawContext;

public abstract class SettingElement<T> extends GuiElement {
	
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
		super.renderTop(ctx, mx, my, scrollX, scrollY, partialTicks);
		
		if(hasClicked) {
			setTooltip("", "");
		} else {
			setTooltip(getName.get(), getTooltip.get());
		}
		
		if(!showTooltip) {
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
