package net.grilledham.hamhacks.gui.parts.impl;

import net.grilledham.hamhacks.modules.render.ClickGUI;
import net.grilledham.hamhacks.util.RenderUtil;
import net.grilledham.hamhacks.util.setting.settings.BoolSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

public class BoolSettingPart extends SettingPart {
	
	private float hoverAnimation;
	private float enableAnimation;
	
	private final BoolSetting setting;
	
	protected boolean drawBackground = true;
	
	public BoolSettingPart(int x, int y, BoolSetting setting) {
		super(x, y, MinecraftClient.getInstance().textRenderer.getWidth(setting.getName()) + 22, setting);
		this.setting = setting;
	}
	
	@Override
	public void render(MatrixStack stack, int mx, int my, float partialTicks) {
		stack.push();
		RenderUtil.preRender();
		
		if(drawBackground) {
			int bgC = ClickGUI.getInstance().bgColor.getRGB();
			RenderUtil.drawRect(stack, x, y, width, height, bgC);
		}
		
		int outlineC = 0xffcccccc;
		RenderUtil.drawHRect(stack, x + width - 14, y + 2, 12, 12, outlineC);
		
		boolean hovered = mx >= x + width - 12 && mx < x + width - 4 && my >= y + 4 && my < y + 12;
		int boxC = RenderUtil.mix((ClickGUI.getInstance().accentColor.getRGB() & 0xff000000) + 0xffffff, (ClickGUI.getInstance().accentColor.getRGB() & 0xff000000) + RenderUtil.mix(0x00a400, 0xa40000, enableAnimation), hoverAnimation / 4);
		RenderUtil.drawRect(stack, x + width - 12, y + 4, 8, 8, boxC);
		
		mc.textRenderer.drawWithShadow(stack, setting.getName(), x + 2, y + 4, ClickGUI.getInstance().textColor.getRGB());
		
		RenderUtil.postRender();
		stack.pop();
		
		if(hovered) {
			hoverAnimation += partialTicks / 5;
		} else {
			hoverAnimation -= partialTicks / 5;
		}
		hoverAnimation = Math.min(1, Math.max(0, hoverAnimation));
		
		if(setting.getValue()) {
			enableAnimation += partialTicks / 5;
		} else {
			enableAnimation -= partialTicks / 5;
		}
		enableAnimation = Math.min(1, Math.max(0, enableAnimation));
	}
	
	@Override
	public boolean release(double mx, double my, int button) {
		super.release(mx, my, button);
		if(mx >= x + width - 12 && mx < x + width - 4 && my >= y + 4 && my < y + 12) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				setting.setValue(!setting.getValue());
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				setting.reset();
			}
			return true;
		}
		return super.release(mx, my, button);
	}
}
