package net.grilledham.hamhacks.gui.element.impl;

import net.grilledham.hamhacks.animation.Animation;
import net.grilledham.hamhacks.animation.AnimationBuilder;
import net.grilledham.hamhacks.animation.AnimationType;
import net.grilledham.hamhacks.gui.screen.impl.SettingContainerScreen;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.grilledham.hamhacks.setting.SettingContainer;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

public class SettingContainerElement extends SettingElement<SettingContainer<?, ?>> {
	
	private final Animation hoverAnimation = AnimationBuilder.create(AnimationType.IN_OUT_QUAD, 0.25).build();
	
	protected boolean drawBackground = true;
	
	public SettingContainerElement(float x, float y, double scale, SettingContainer<?, ?> setting) {
		super(x, y, MinecraftClient.getInstance().textRenderer.getWidth(setting.getName()) + 22, scale, setting);
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
			RenderUtil.drawRect(stack, x, y, width - 50, height, bgC);
		}
		
		int outlineC = 0xffcccccc;
		RenderUtil.drawHRect(stack, x + width - 50, y, 50, height, outlineC);
		
		boolean hovered = mx >= x + width - 50 && mx < x + width && my >= y && my < y + height;
		int boxC = RenderUtil.mix(ui.bgColorHovered.get().getRGB(), ui.bgColor.get().getRGB(), hoverAnimation.get());
		RenderUtil.drawRect(stack, x + width - 49, y + 1, 48, height - 2, boxC);
		
		mc.textRenderer.drawWithShadow(stack, setting.getName(), x + 2, y + 4, ui.textColor.get().getRGB());
		
		mc.textRenderer.drawWithShadow(stack, "Edit", x + width - (25 + mc.textRenderer.getWidth("Edit") / 2f), y + 4, ui.textColor.get().getRGB());
		
		RenderUtil.postRender();
		stack.pop();
		
		hoverAnimation.set(hovered);
		hoverAnimation.update();
	}
	
	@Override
	public boolean release(double mx, double my, float scrollX, float scrollY, int button) {
		super.release(mx, my, scrollX, scrollY, button);
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		if(mx >= x + width - 50 && mx < x + width && my >= y && my < y + height) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				mc.setScreen(new SettingContainerScreen(mc.currentScreen, scale, setting));
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				setting.reset();
			}
		}
		return super.release(mx, my, scrollX, scrollY, button);
	}
}
