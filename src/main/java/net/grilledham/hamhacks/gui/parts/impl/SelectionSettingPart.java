package net.grilledham.hamhacks.gui.parts.impl;

import net.grilledham.hamhacks.gui.parts.GuiPart;
import net.grilledham.hamhacks.modules.render.ClickGUI;
import net.grilledham.hamhacks.util.Animation;
import net.grilledham.hamhacks.util.RenderUtil;
import net.grilledham.hamhacks.util.SelectableList;
import net.grilledham.hamhacks.util.setting.SettingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SelectionSettingPart extends SettingPart {
	
	private final Animation hoverAnimation = Animation.getInOutQuad(0.25);
	private final Animation selectionAnimation = Animation.getInOutQuad(0.25);
	
	private boolean selected = false;
	
	private final List<GuiPart> parts = new ArrayList<>();
	
	private float maxWidth;
	
	public SelectionSettingPart(float x, float y, Field setting, Object obj) {
		super(x, y, MinecraftClient.getInstance().textRenderer.getWidth(SettingHelper.getName(setting).getString()), setting, obj);
		maxWidth = 0;
		GuiPart part;
		try {
			for(String string : ((SelectableList)setting.get(obj)).getPossibilities()) {
				String s = Text.translatable(string).getString();
				parts.add(part = new ButtonPart(s, 0, 0, mc.textRenderer.getWidth(s) + 4, 16, () -> {
					try {
						((SelectableList)setting.get(obj)).set(string);
					} catch(IllegalAccessException e) {
						e.printStackTrace();
					}
				}));
				if(maxWidth < part.getWidth()) {
					maxWidth = part.getWidth();
				}
			}
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		}
		resize(MinecraftClient.getInstance().textRenderer.getWidth(SettingHelper.getName(setting)) + maxWidth + 6, 16);
		int yAdd = 0;
		for(GuiPart guiPart : parts) {
			guiPart.moveTo(x + width - maxWidth, y + yAdd);
			guiPart.resize(maxWidth, guiPart.getHeight());
			yAdd += guiPart.getHeight();
		}
	}
	
	@Override
	public void moveTo(float x, float y) {
		super.moveTo(x, y);
		int yAdd = 0;
		for(GuiPart guiPart : parts) {
			guiPart.moveTo(x + width - maxWidth, y + yAdd);
			yAdd += guiPart.getHeight();
		}
	}
	
	@Override
	public void moveBy(float x, float y) {
		super.moveBy(x, y);
		for(GuiPart guiPart : parts) {
			guiPart.moveBy(x, y);
		}
	}
	
	@Override
	public void resize(float maxW, float maxH) {
		super.resize(maxW, maxH);
		int yAdd = 0;
		for(GuiPart guiPart : parts) {
			guiPart.moveTo(x + width - maxWidth, y + yAdd);
			yAdd += guiPart.getHeight();
		}
	}
	
	@Override
	public void render(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		stack.push();
		RenderUtil.preRender();
		
		int bgC = ClickGUI.getInstance().bgColor.getRGB();
		RenderUtil.drawRect(stack, x, y, width - maxWidth, height, bgC);
		
		boolean hovered = mx >= x + width - maxWidth && mx < x + width && my >= y && my < y + height;
		bgC = RenderUtil.mix(ClickGUI.getInstance().bgColorHovered.getRGB(), bgC, hoverAnimation.get());
		RenderUtil.drawRect(stack, x + width - maxWidth, y, maxWidth, height, bgC);
		
		int outlineC = 0xffcccccc;
		RenderUtil.drawHRect(stack, x + width - maxWidth, y, maxWidth, height, outlineC);
		
		mc.textRenderer.drawWithShadow(stack, SettingHelper.getName(setting), x + 2, y + 4, ClickGUI.getInstance().textColor.getRGB());
		try {
			String text = Text.translatable(((SelectableList)setting.get(obj)).get()).getString();
			mc.textRenderer.drawWithShadow(stack, text, x + width - mc.textRenderer.getWidth(text) - 2, y + 4, ClickGUI.getInstance().textColor.getRGB());
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		}
		
		RenderUtil.postRender();
		stack.pop();
		
		hoverAnimation.set(hovered);
		hoverAnimation.update();
		
		selectionAnimation.set(selected);
		selectionAnimation.update();
	}
	
	@Override
	protected void renderTop(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		stack.push();
		RenderUtil.preRender();
		RenderUtil.pushScissor(x, y, width, (height * parts.size()) * (float)selectionAnimation.get(), ClickGUI.getInstance().scale);
		RenderUtil.applyScissor();
		
		for(GuiPart part : parts) {
			part.draw(stack, mx, my, scrollX, scrollY, partialTicks);
		}
		
		RenderUtil.postRender();
		RenderUtil.popScissor();
		stack.pop();
		super.renderTop(stack, mx, my, scrollX, scrollY, partialTicks);
	}
	
	@Override
	public boolean click(double mx, double my, float scrollX, float scrollY, int button) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		if(selected) {
			return true;
		}
		if(mx >= x + width - maxWidth && mx < x + width && my >= y && my < y + height) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			
			}
		}
		return super.click(mx, my, scrollX, scrollY, button);
	}
	
	@Override
	public boolean release(double mx, double my, float scrollX, float scrollY, int button) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		super.release(mx, my, scrollX, scrollY, button);
		if(selected) {
			selected = false;
			for(GuiPart part : parts) {
				part.release(mx, my, scrollX, scrollY, button);
			}
			return false;
		} else if(mx >= x + width - maxWidth && mx < x + width && my >= y && my < y + height) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				selected = true;
				return true;
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				try {
					SettingHelper.reset(setting, obj);
				} catch(IllegalAccessException e) {
					e.printStackTrace();
				}
				return false;
			}
		}
		return super.release(mx, my, scrollX, scrollY, button);
	}
}
