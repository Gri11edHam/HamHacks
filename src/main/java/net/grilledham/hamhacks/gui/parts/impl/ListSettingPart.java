package net.grilledham.hamhacks.gui.parts.impl;

import net.grilledham.hamhacks.modules.render.ClickGUI;
import net.grilledham.hamhacks.util.RenderUtil;
import net.grilledham.hamhacks.util.StringList;
import net.grilledham.hamhacks.util.setting.SettingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ListSettingPart extends SettingPart {
	
	private float hoverAnimation;
	private float selectionAnimation;
	
	private final List<StringSettingPart> stringParts = new ArrayList<>();
	private final List<ButtonPart> removeButtons = new ArrayList<>();
	
	private ButtonPart addButton;
	
	private boolean selected = false;
	
	float maxWidth = 0;
	
	public ListSettingPart(float x, float y, Field setting, Object obj) {
		super(x, y, MinecraftClient.getInstance().textRenderer.getWidth(SettingHelper.getName(setting).getString()) + 18, setting, obj);
		updateList();
	}
	
	private void updateList() {
		stringParts.clear();
		removeButtons.clear();
		maxWidth = 106 + 16;
		int i = 0;
		try {
			for(String s : ((StringList)setting.get(obj)).getList()) {
				int finalI = i;
				StringSettingPart strSetPart;
				Field finalSetting = setting;
				final Object finalObj = obj;
				stringParts.add(strSetPart = new StringSettingPart(x, y, s) {
					@Override
					public void updateValue(String value) {
						super.updateValue(value);
						try {
							((StringList)finalSetting.get(finalObj)).getList().set(finalI, value);
						} catch(IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				});
				strSetPart.drawBackground = false;
				ButtonPart bPart;
				removeButtons.add(bPart = new ButtonPart("-", x, y, 16, height, () -> {
					try {
						((StringList)setting.get(obj)).remove(finalI);
					} catch(IllegalAccessException e) {
						e.printStackTrace();
					}
					updateList();
				}));
				i++;
				maxWidth = Math.max(maxWidth, strSetPart.getWidth() + bPart.getWidth());
			}
			for(i = 0; i < stringParts.size(); i++) {
				stringParts.get(i).moveTo(x + width - maxWidth - 2, y + height * (i + 1));
				removeButtons.get(i).moveTo(x + width - maxWidth + stringParts.get(i).getWidth(), y + height * (i + 1));
			}
			addButton = new ButtonPart("+", x + width - maxWidth, y + height * (i + 1), maxWidth, height, () -> {
				try {
					((StringList)setting.get(obj)).getList().add("");
				} catch(IllegalAccessException e) {
					e.printStackTrace();
				}
				updateList();
			});
		} catch(IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void resize(float maxW, float maxH) {
		super.resize(maxW, maxH);
		updateList();
	}
	
	@Override
	public void moveTo(float x, float y) {
		super.moveTo(x, y);
		updateList();
	}
	
	@Override
	public void moveBy(float x, float y) {
		super.moveBy(x, y);
		updateList();
	}
	
	@Override
	protected void render(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		stack.push();
		RenderUtil.preRender();
		
		int bgC = ClickGUI.getInstance().bgColor.getRGB();
		RenderUtil.drawRect(stack, x, y, width - height, height, bgC);
		
		boolean hovered = mx >= x + width - height && mx < x + width && my >= y && my < y + height;
		bgC = RenderUtil.mix(ClickGUI.getInstance().bgColorHovered.getRGB(), bgC, hoverAnimation);
		RenderUtil.drawRect(stack, x + width - height, y, height, height, bgC);
		
		int outlineC = 0xffcccccc;
		RenderUtil.drawHRect(stack, x + width - height, y, height, height, outlineC);
		
		mc.textRenderer.drawWithShadow(stack, SettingHelper.getName(setting), x + 2, y + 4, ClickGUI.getInstance().textColor.getRGB());
		float dropDownX = x + width - height / 2f - mc.textRenderer.getWidth("<") / 2f;
		float dropDownCenterX = dropDownX + mc.textRenderer.getWidth("<") / 2f;
		float dropDownCenterY = y + 4 + mc.textRenderer.fontHeight / 2f;
		stack.translate(dropDownCenterX, dropDownCenterY, 0);
		stack.peek().getPositionMatrix().multiply(new Quaternion(new Vec3f(0, 0, 1), selectionAnimation * -90, true));
		stack.translate(-dropDownCenterX, -dropDownCenterY, 0);
		mc.textRenderer.drawWithShadow(stack, "<", dropDownX, y + 4, ClickGUI.getInstance().textColor.getRGB());
		
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
	protected void renderTop(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		stack.push();
		RenderUtil.preRender();
		RenderUtil.pushScissor(x + width - maxWidth, y + height, maxWidth, (height * (stringParts.size() + 1)) * selectionAnimation, ClickGUI.getInstance().scale);
		RenderUtil.applyScissor();
		
		RenderUtil.drawRect(stack, x + width - maxWidth, y + height, maxWidth, height * (stringParts.size() + 1), 0xff202020);
		
		addButton.draw(stack, mx, my, scrollX, scrollY, partialTicks);
		
		for(int i = 0; i < removeButtons.size(); i++) {
			removeButtons.get(i).draw(stack, mx, my, scrollX, scrollY, partialTicks);
		}
		
		for(int i = 0; i < removeButtons.size(); i++) {
			if(((stringParts.size() + 1)) * selectionAnimation > i) {
				stringParts.get(i).draw(stack, mx, my, scrollX, scrollY, partialTicks);
			}
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
			for(int i = 0; i < stringParts.size(); i++) {
				if(stringParts.get(i).click(mx, my, scrollX, scrollY, button)) {
					return true;
				}
				if(removeButtons.get(i).click(mx, my, scrollX, scrollY, button)) {
					return true;
				}
			}
			if(addButton.click(mx, my, scrollX, scrollY, button)) {
				return true;
			}
		}
		if(mx >= x + width - height && mx < x + width && my >= y && my < y + height) {
			return true;
		}
		return super.click(mx, my, scrollX, scrollY, button);
	}
	
	@Override
	public boolean release(double mx, double my, float scrollX, float scrollY, int button) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		super.release(mx, my, scrollX, scrollY, button);
		try {
			if(selected) {
				if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && (mx >= x + width - height && mx < x + width && my >= y && my < y + height)) {
					SettingHelper.reset(setting, obj);
					updateList();
					return true;
				}
				for(int i = 0; i < stringParts.size(); i++) {
					if(stringParts.get(i).release(mx, my, scrollX, scrollY, button)) {
						return true;
					}
					if(removeButtons.get(i).release(mx, my, scrollX, scrollY, button)) {
						return true;
					}
				}
				if(addButton.release(mx, my, scrollX, scrollY, button)) {
					return true;
				}
				selected = false;
			} else if(mx >= x + width - height && mx < x + width && my >= y && my < y + height) {
				if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
					selected = true;
				} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
					SettingHelper.reset(setting, obj);
					updateList();
				}
				return true;
			}
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		}
		return super.release(mx, my, scrollX, scrollY, button);
	}
	
	@Override
	public boolean type(int code, int scanCode, int modifiers) {
		if(selected) {
			for(int i = 0; i < stringParts.size(); i++) {
				if(stringParts.get(i).type(code, scanCode, modifiers)) {
					return true;
				}
				if(removeButtons.get(i).type(code, scanCode, modifiers)) {
					return true;
				}
			}
			if(addButton.type(code, scanCode, modifiers)) {
				return true;
			}
			selected = false;
		}
		return super.type(code, scanCode, modifiers);
	}
	
	@Override
	public boolean typeChar(char c, int modifiers) {
		if(selected) {
			for(int i = 0; i < stringParts.size(); i++) {
				if(stringParts.get(i).typeChar(c, modifiers)) {
					return true;
				}
				if(removeButtons.get(i).typeChar(c, modifiers)) {
					return true;
				}
			}
			if(addButton.typeChar(c, modifiers)) {
				return true;
			}
			selected = false;
		}
		return super.typeChar(c, modifiers);
	}
}
