package net.grilledham.hamhacks.gui.parts.impl;

import net.grilledham.hamhacks.animation.Animation;
import net.grilledham.hamhacks.animation.AnimationBuilder;
import net.grilledham.hamhacks.animation.AnimationType;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.render.ClickGUI;
import net.grilledham.hamhacks.setting.SettingHelper;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;

public class BoolSettingPart extends SettingPart {
	
	private final Animation hoverAnimation = AnimationBuilder.create(AnimationType.IN_OUT_QUAD, 0.25).build();
	private final Animation enableAnimation = AnimationBuilder.create(AnimationType.IN_OUT_QUAD, 0.25).build();
	
	protected boolean drawBackground = true;
	
	public BoolSettingPart(float x, float y, Field setting, Object obj) {
		super(x, y, MinecraftClient.getInstance().textRenderer.getWidth(SettingHelper.getName(setting).getString()) + 22, setting, obj);
		try {
			enableAnimation.setAbsolute(setting.getBoolean(obj));
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void render(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		stack.push();
		RenderUtil.preRender();
		
		if(drawBackground) {
			int bgC = ModuleManager.getModule(ClickGUI.class).bgColor.getRGB();
			RenderUtil.drawRect(stack, x, y, width, height, bgC);
		}
		
		int outlineC = 0xffcccccc;
		RenderUtil.drawHRect(stack, x + width - 14, y + 2, 12, 12, outlineC);
		
		boolean hovered = mx >= x + width - 12 && mx < x + width - 4 && my >= y + 4 && my < y + 12;
		int boxC = RenderUtil.mix((ModuleManager.getModule(ClickGUI.class).accentColor.getRGB() & 0xff000000) + 0xffffff, (ModuleManager.getModule(ClickGUI.class).accentColor.getRGB() & 0xff000000) + RenderUtil.mix(0x00a400, 0xa40000, enableAnimation.get()), hoverAnimation.get() / 4);
		RenderUtil.drawRect(stack, x + width - 12, y + 4, 8, 8, boxC);
		
		mc.textRenderer.drawWithShadow(stack, SettingHelper.getName(setting), x + 2, y + 4, ModuleManager.getModule(ClickGUI.class).textColor.getRGB());
		
		RenderUtil.postRender();
		stack.pop();
		
		hoverAnimation.set(hovered);
		hoverAnimation.update();
		
		try {
			enableAnimation.set(setting.getBoolean(obj));
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		}
		enableAnimation.update();
	}
	
	@Override
	public boolean release(double mx, double my, float scrollX, float scrollY, int button) {
		super.release(mx, my, scrollX, scrollY, button);
		float x = this.x + scrollX;
		float y = this.y + scrollY;
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
		}
		return super.release(mx, my, scrollX, scrollY, button);
	}
}
