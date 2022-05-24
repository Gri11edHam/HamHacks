package net.grilledham.hamhacks.gui.parts.impl;

import net.grilledham.hamhacks.gui.parts.GuiPart;
import net.grilledham.hamhacks.modules.render.ClickGUI;
import net.grilledham.hamhacks.util.RenderUtil;
import net.grilledham.hamhacks.util.setting.settings.SelectionSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class SelectionSettingPart extends SettingPart {
	
	private float hoverAnimation;
	private float selectionAnimation;
	
	private final SelectionSetting setting;
	
	private boolean selected = false;
	
	private final List<GuiPart> parts = new ArrayList<>();
	
	private int maxWidth;
	
	public SelectionSettingPart(int x, int y, SelectionSetting setting) {
		super(x, y, MinecraftClient.getInstance().textRenderer.getWidth(setting.getName()), setting);
		this.setting = setting;
		maxWidth = 0;
		GuiPart part;
		for(Text s : setting.getPossibleValues()) {
			parts.add(part = new ButtonPart(s.getString(), 0, 0, mc.textRenderer.getWidth(s) + 4, 16, () -> setting.setValue(s)));
			if(maxWidth < part.getWidth()) {
				maxWidth = part.getWidth();
			}
		}
		resize(MinecraftClient.getInstance().textRenderer.getWidth(setting.getName()) + maxWidth + 6, 16);
		int yAdd = 0;
		for(GuiPart guiPart : parts) {
			guiPart.moveTo(x + width - maxWidth, y + yAdd);
			guiPart.resize(maxWidth, guiPart.getHeight());
			yAdd += guiPart.getHeight();
		}
	}
	
	@Override
	public void moveTo(int x, int y) {
		super.moveTo(x, y);
		int yAdd = 0;
		for(GuiPart guiPart : parts) {
			guiPart.moveTo(x + width - maxWidth, y + yAdd);
			yAdd += guiPart.getHeight();
		}
	}
	
	@Override
	public void moveBy(int x, int y) {
		super.moveBy(x, y);
		for(GuiPart guiPart : parts) {
			guiPart.moveBy(x, y);
		}
	}
	
	@Override
	public void resize(int maxW, int maxH) {
		super.resize(maxW, maxH);
		int yAdd = 0;
		for(GuiPart guiPart : parts) {
			guiPart.moveTo(x + width - maxWidth, y + yAdd);
			yAdd += guiPart.getHeight();
		}
	}
	
	@Override
	public void render(MatrixStack stack, int mx, int my, float partialTicks) {
		stack.push();
		RenderUtil.preRender();
		
		int bgC = ClickGUI.getInstance().bgColor.getRGB();
		RenderUtil.drawRect(stack, x, y, width - maxWidth, height, bgC);
		
		boolean hovered = mx >= x + width - maxWidth && mx < x + width && my >= y && my < y + height;
		bgC = RenderUtil.mix(ClickGUI.getInstance().bgColorHovered.getRGB(), bgC, hoverAnimation);
		RenderUtil.drawRect(stack, x + width - maxWidth, y, maxWidth, height, bgC);
		
		int outlineC = 0xffcccccc;
		RenderUtil.drawHRect(stack, x + width - maxWidth, y, maxWidth, height, outlineC);
		
		mc.textRenderer.drawWithShadow(stack, setting.getName(), x + 2, y + 4, ClickGUI.getInstance().textColor.getRGB());
		mc.textRenderer.drawWithShadow(stack, setting.getValue(), x + width - mc.textRenderer.getWidth(setting.getValue()) - 2, y + 4, ClickGUI.getInstance().textColor.getRGB());
		
		RenderUtil.postRender();
		stack.pop();
		
		if(hovered) {
			hoverAnimation += partialTicks / 5;
		} else {
			hoverAnimation -= partialTicks / 5;
		}
		hoverAnimation = Math.min(1, Math.max(0, hoverAnimation));
		
		if(selected) {
			selectionAnimation += partialTicks / 5;
		} else {
			selectionAnimation -= partialTicks / 5;
		}
		selectionAnimation = Math.min(1, Math.max(0, selectionAnimation));
	}
	
	@Override
	protected void renderTop(MatrixStack stack, int mx, int my, float partialTicks) {
		stack.push();
		RenderUtil.preRender();
		RenderUtil.pushScissor(x, y, width, (height * parts.size()) * selectionAnimation, ClickGUI.getInstance().scale.getValue());
		RenderUtil.applyScissor();
		
		for(GuiPart part : parts) {
			part.draw(stack, mx, my, partialTicks);
		}
		
		RenderUtil.postRender();
		RenderUtil.popScissor();
		stack.pop();
		super.renderTop(stack, mx, my, partialTicks);
	}
	
	@Override
	public boolean click(double mx, double my, int button) {
		if(selected) {
			return true;
		}
		if(mx >= x + width - maxWidth && mx < x + width && my >= y && my < y + height) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			
			}
			return true;
		}
		return super.click(mx, my, button);
	}
	
	@Override
	public boolean release(double mx, double my, int button) {
		super.release(mx, my, button);
		if(selected) {
			selected = false;
			for(GuiPart part : parts) {
				part.release(mx, my, button);
			}
			return true;
		} else if(mx >= x + width - maxWidth && mx < x + width && my >= y && my < y + height) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				selected = true;
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				setting.reset();
			}
			return true;
		}
		return super.release(mx, my, button);
	}
}
