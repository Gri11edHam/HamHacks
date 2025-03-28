package net.grilledham.hamhacks.gui.screen;

import net.grilledham.hamhacks.gui.element.GuiElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class GuiScreen extends Screen {
	
	protected final Screen last;
	
	protected final double scale;
	
	protected final List<GuiElement> elements = new ArrayList<>();
	
	protected GuiElement selected = null;
	
	private boolean dirty = false;
	
	private boolean initialized = false;
	
	private boolean wasMouseDown = false;
	
	protected boolean alwaysDrawBackground = true;
	
	protected GuiScreen(Text title, Screen last, double scale) {
		super(title);
		this.last = last;
		this.scale = scale;
	}
	
	@Override
	protected void clearAndInit() {
		width = (int)(client.getWindow().getFramebufferWidth() / scale);
		height = (int)(client.getWindow().getFramebufferHeight() / scale);
		elements.clear();
		dirty = false;
		initialized = true;
		super.clearAndInit();
	}
	
	@Override
	public void render(DrawContext ctx, int mx, int my, float tickDelta) {
		if(dirty || !initialized) {
			clearAndInit();
		}
		
		super.render(ctx, mx, my, tickDelta);
		
		MatrixStack stack = ctx.getMatrices();
		mx = (int)((mx * client.getWindow().getScaleFactor()) / scale);
		my = (int)((my * client.getWindow().getScaleFactor()) / scale);
		stack.push();
		float scaleFactor = (float)(scale / client.getWindow().getScaleFactor());
		stack.scale(scaleFactor, scaleFactor, scaleFactor);
		
		for(GuiElement element : elements) {
			element.render(ctx, mx, my, 0, 0, tickDelta);
		}
		for(GuiElement element : elements) {
			element.renderTop(ctx, mx, my, 0, 0, tickDelta);
		}
		stack.pop();
	}
	
	@Override
	public boolean mouseClicked(double mx, double my, int button) {
		wasMouseDown = true;
		mx = (mx * client.getWindow().getScaleFactor()) / scale;
		my = (my * client.getWindow().getScaleFactor()) / scale;
		for(GuiElement element : elements) {
			if(selected != null) {
				if(element == selected) {
					selected.click(mx, my, 0, 0, button);
					return true;
				}
			} else {
				if(element.click(mx, my, 0, 0, button)) {
					selected = element;
					return true;
				}
			}
		}
		return super.mouseClicked(mx, my, button);
	}
	
	@Override
	public boolean mouseReleased(double mx, double my, int button) {
		mx = (mx * client.getWindow().getScaleFactor()) / scale;
		my = (my * client.getWindow().getScaleFactor()) / scale;
		if(!wasMouseDown) return super.mouseReleased(mx, my, button);
		wasMouseDown = false;
		for(GuiElement element : elements.stream().toList()) {
			if(selected != null) {
				if(element == selected) {
					if(!selected.release(mx, my, 0, 0, button)) {
						selected = null;
					}
					return true;
				}
			} else {
				if(element.release(mx, my, 0, 0, button)) {
					selected = element;
					return true;
				}
			}
		}
		return super.mouseReleased(mx, my, button);
	}
	
	@Override
	public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
		mx = (mx * client.getWindow().getScaleFactor()) / scale;
		my = (my * client.getWindow().getScaleFactor()) / scale;
		for(GuiElement element : elements) {
			if(selected != null) {
				if(element == selected) {
					selected.drag(mx, my, 0, 0, button, dx, dy);
					return true;
				}
			} else {
				if(element.drag(mx, my, 0, 0, button, dx, dy)) {
					return true;
				}
			}
		}
		return super.mouseDragged(mx, my, button, dx, dy);
	}
	
	@Override
	public boolean mouseScrolled(double mx, double my, double hAmount, double vAmount) {
		mx = (mx * client.getWindow().getScaleFactor()) / scale;
		my = (my * client.getWindow().getScaleFactor()) / scale;
		for(GuiElement element : elements) {
			if(selected != null) {
				if(element == selected) {
					selected.scroll(mx, my, 0, 0, vAmount);
					return true;
				}
			} else {
				if(element.scroll(mx, my, 0, 0, vAmount)) {
					return true;
				}
			}
		}
		return super.mouseScrolled(mx, my, hAmount, vAmount);
	}
	
	@Override
	public boolean keyPressed(int code, int scanCode, int modifiers) {
		if(selected != null) {
			if(!selected.type(code, scanCode, modifiers)) {
				selected = null;
			}
			return true;
		}
		for(GuiElement element : elements) {
			if(element.type(code, scanCode, modifiers)) {
				return true;
			}
		}
		return super.keyPressed(code, scanCode, modifiers);
	}
	
	@Override
	public boolean charTyped(char c, int modifiers) {
		if(selected != null) {
			selected.typeChar(c, modifiers);
			return true;
		}
		for(GuiElement element : elements) {
			if(element.typeChar(c, modifiers)) {
				return true;
			}
		}
		return super.charTyped(c, modifiers);
	}
	
	public void markDirty() {
		dirty = true;
	}
	
	@Override
	public void close() {
		client.setScreen(last);
	}
	
	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		if(last != null || alwaysDrawBackground) {
			super.renderBackground(context, mouseX, mouseY, delta);
		}
	}
}
