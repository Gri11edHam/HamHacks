package net.grilledham.hamhacks.gui.parts.impl;

import net.grilledham.hamhacks.modules.render.ClickGUI;
import net.grilledham.hamhacks.util.RenderUtil;
import net.grilledham.hamhacks.util.setting.SettingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;

public class BoolSettingPart extends SettingPart {
	
	private float hoverAnimation;
	private float enableAnimation;
	
	protected boolean drawBackground = true;
	
	public BoolSettingPart(int x, int y, Field setting, Object obj) {
		super(x, y, MinecraftClient.getInstance().textRenderer.getWidth(SettingHelper.getName(setting).getString()) + 22, setting, obj);
	}
	
	@Override
	public void render(MatrixStack stack, int mx, int my, int scrollX, int scrollY, float partialTicks) {
		int x = this.x + scrollX;
		int y = this.y + scrollY;
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
		
		mc.textRenderer.drawWithShadow(stack, SettingHelper.getName(setting), x + 2, y + 4, ClickGUI.getInstance().textColor.getRGB());
		
		RenderUtil.postRender();
		stack.pop();
		
		if(hovered) {
			hoverAnimation += partialTicks / 5;
		} else {
			hoverAnimation -= partialTicks / 5;
		}
		hoverAnimation = Math.min(1, Math.max(0, hoverAnimation));
		
		try {
			if(setting.getBoolean(obj)) {
				enableAnimation += partialTicks / 5;
			} else {
				enableAnimation -= partialTicks / 5;
			}
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		}
		enableAnimation = Math.min(1, Math.max(0, enableAnimation));
	}
	
	@Override
	public boolean release(double mx, double my, int scrollX, int scrollY, int button) {
		super.release(mx, my, scrollX, scrollY, button);
		int x = this.x + scrollX;
		int y = this.y + scrollY;
		if(mx >= x + width - 12 && mx < x + width - 4 && my >= y + 4 && my < y + 12) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				try {
					setting.setBoolean(obj, !setting.getBoolean(obj));
				} catch(IllegalAccessException e) {
					e.printStackTrace();
				}
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				try {
					SettingHelper.reset(setting, obj);
				} catch(IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			return true;
		}
		return super.release(mx, my, scrollX, scrollY, button);
	}
}
