package net.grilledham.hamhacks.gui.element.impl;

import net.grilledham.hamhacks.animation.Animation;
import net.grilledham.hamhacks.animation.AnimationType;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.grilledham.hamhacks.setting.BoolSetting;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

public class BoolSettingElement extends SettingElement<Boolean> {
	
	private final Animation hoverAnimation = new Animation(AnimationType.EASE_IN_OUT, 0.25);
	private final Animation enableAnimation = new Animation(AnimationType.EASE_IN_OUT, 0.25);
	
	protected boolean drawBackground = true;
	
	public BoolSettingElement(float x, float y, double scale, BoolSetting setting) {
		this(x, y, scale, setting::getName, setting.hasTooltip() ? setting::getTooltip : () -> "", setting::shouldShow, setting::get, setting::set, setting::reset);
	}
	
	public BoolSettingElement(float x, float y, double scale, Get<String> getName, Get<String> getTooltip, Get<Boolean> shouldShow, Get<Boolean> get, Set<Boolean> set, Runnable reset) {
		super(x, y, MinecraftClient.getInstance().textRenderer.getWidth(getName.get()) + 22, scale, getName, getTooltip, shouldShow, get, set, reset);
		enableAnimation.setAbsolute(get.get());
	}
	
	@Override
	public void render(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		stack.push();
		RenderUtil.preRender();
		
		ClickGUI ui = PageManager.getPage(ClickGUI.class);
		if(drawBackground) {
			int bgC = ui.bgColor.get().getRGB();
			RenderUtil.drawRect(stack, x, y, width, height, bgC);
		}
		
		int outlineC = 0xffcccccc;
		RenderUtil.drawHRect(stack, x + width - 14, y + 2, 12, 12, outlineC);
		
		boolean hovered = mx >= x + width - 12 && mx < x + width - 4 && my >= y + 4 && my < y + 12;
		int boxC = RenderUtil.mix((ui.accentColor.get().getRGB() & 0xff000000) + 0xffffff, (ui.accentColor.get().getRGB() & 0xff000000) + RenderUtil.mix(0x00a400, 0xa40000, enableAnimation.get()), hoverAnimation.get() / 4);
		RenderUtil.drawRect(stack, x + width - 12, y + 4, 8, 8, boxC);
		
		mc.textRenderer.drawWithShadow(stack, getName.get(), x + 2, y + 4, ui.textColor.get().getRGB());
		
		RenderUtil.postRender();
		stack.pop();
		
		hoverAnimation.set(hovered);
		hoverAnimation.update();
		
		enableAnimation.set(get.get());
		enableAnimation.update();
	}
	
	@Override
	public boolean release(double mx, double my, float scrollX, float scrollY, int button) {
		super.release(mx, my, scrollX, scrollY, button);
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		if(mx >= x + width - 12 && mx < x + width - 4 && my >= y + 4 && my < y + 12) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				set.set(!get.get());
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				reset.run();
			}
		}
		return super.release(mx, my, scrollX, scrollY, button);
	}
}
