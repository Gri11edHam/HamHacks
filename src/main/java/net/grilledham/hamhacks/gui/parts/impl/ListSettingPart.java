package net.grilledham.hamhacks.gui.parts.impl;

import net.grilledham.hamhacks.modules.render.ClickGUI;
import net.grilledham.hamhacks.util.RenderUtil;
import net.grilledham.hamhacks.util.setting.settings.ListSetting;
import net.grilledham.hamhacks.util.setting.settings.StringSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ListSettingPart extends SettingPart {
	
	private float hoverAnimation;
	private float selectionAnimation;
	
	private final ListSetting setting;
	
	private final List<StringSetting> strings = new ArrayList<>();
	private final List<StringSettingPart> stringParts = new ArrayList<>();
	private final List<ButtonPart> removeButtons = new ArrayList<>();
	
	private ButtonPart addButton;
	
	private boolean selected = false;
	
	float maxWidth = 0;
	
	public ListSettingPart(int x, int y, ListSetting setting) {
		super(x, y, MinecraftClient.getInstance().textRenderer.getWidth(setting.getName()) + 18, setting);
		this.setting = setting;
		updateList();
	}
	
	private void updateList() {
		strings.clear();
		stringParts.clear();
		removeButtons.clear();
		maxWidth = 106 + 16;
		int i = 0;
		for(String s : setting.getValue()) {
			int finalI = i;
			StringSetting strSet;
			strings.add(strSet = new StringSetting(Text.translatable("setting.listsettingpart.internal.stringsetting"), s));
			StringSettingPart strSetPart;
			stringParts.add(strSetPart = new StringSettingPart(x, y, strSet));
			strSetPart.drawBackground = false;
			ButtonPart bPart;
			removeButtons.add(bPart = new ButtonPart("-", x, y, 16, height, () -> {
				setting.remove(strings.get(finalI).getValue());
				updateList();
			}));
			i++;
			maxWidth = Math.max(maxWidth, strSetPart.getWidth() + bPart.getWidth());
		}
		for(i = 0; i < stringParts.size(); i++) {
			stringParts.get(i).moveTo((int)(x + width - maxWidth - 2), y + height * (i + 1));
			removeButtons.get(i).moveTo((int)(x + width - maxWidth + stringParts.get(i).getWidth()), y + height * (i + 1));
		}
		addButton = new ButtonPart("+", (int)(x + width - maxWidth), y + height * (i + 1), (int)maxWidth, height, () -> {
			setting.add("");
			updateList();
		});
	}
	
	@Override
	public void resize(int maxW, int maxH) {
		super.resize(maxW, maxH);
		updateList();
	}
	
	@Override
	public void moveTo(int x, int y) {
		super.moveTo(x, y);
		updateList();
	}
	
	@Override
	public void moveBy(int x, int y) {
		super.moveBy(x, y);
		updateList();
	}
	
	@Override
	protected void render(MatrixStack stack, int mx, int my, float partialTicks) {
		stack.push();
		RenderUtil.preRender();
		
		int bgC = ClickGUI.getInstance().bgColor.getRGB();
		RenderUtil.drawRect(stack, x, y, width - height, height, bgC);
		
		boolean hovered = mx >= x + width - height && mx < x + width && my >= y && my < y + height;
		bgC = RenderUtil.mix(ClickGUI.getInstance().bgColorHovered.getRGB(), bgC, hoverAnimation);
		RenderUtil.drawRect(stack, x + width - height, y, height, height, bgC);
		
		int outlineC = 0xffcccccc;
		RenderUtil.drawHRect(stack, x + width - height, y, height, height, outlineC);
		
		mc.textRenderer.drawWithShadow(stack, setting.getName(), x + 2, y + 4, ClickGUI.getInstance().textColor.getRGB());
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
	protected void renderTop(MatrixStack stack, int mx, int my, float partialTicks) {
		stack.push();
		RenderUtil.preRender();
		RenderUtil.pushScissor(x + width - maxWidth, y + height, maxWidth, (height * (stringParts.size() + 1)) * selectionAnimation, ClickGUI.getInstance().scale.getValue());
		RenderUtil.applyScissor();
		
		RenderUtil.drawRect(stack, x + width - maxWidth, y + height, maxWidth, height * (stringParts.size() + 1), 0xff202020);
		
		addButton.draw(stack, mx, my, partialTicks);
		
		for(int i = 0; i < removeButtons.size(); i++) {
			removeButtons.get(i).draw(stack, mx, my, partialTicks);
		}
		
		for(int i = 0; i < removeButtons.size(); i++) {
			if(((stringParts.size() + 1)) * selectionAnimation > i) {
				stringParts.get(i).draw(stack, mx, my, partialTicks);
			}
		}
		
		RenderUtil.postRender();
		RenderUtil.popScissor();
		stack.pop();
		super.renderTop(stack, mx, my, partialTicks);
	}
	
	@Override
	public boolean click(double mx, double my, int button) {
		if(selected) {
			for(int i = 0; i < stringParts.size(); i++) {
				if(stringParts.get(i).click(mx, my, button)) {
					return true;
				}
				if(removeButtons.get(i).click(mx, my, button)) {
					return true;
				}
			}
			if(addButton.click(mx, my, button)) {
				return true;
			}
		}
		if(mx >= x + width - height && mx < x + width && my >= y && my < y + height) {
			return true;
		}
		return super.click(mx, my, button);
	}
	
	@Override
	public boolean release(double mx, double my, int button) {
		super.release(mx, my, button);
		for(int i = 0; i < strings.size(); i++) {
			setting.set(i, strings.get(i).getValue());
		}
		if(selected) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && (mx >= x + width - height && mx < x + width && my >= y && my < y + height)) {
				setting.reset();
				updateList();
				return true;
			}
			for(int i = 0; i < stringParts.size(); i++) {
				if(stringParts.get(i).release(mx, my, button)) {
					return true;
				}
				if(removeButtons.get(i).release(mx, my, button)) {
					return true;
				}
			}
			if(addButton.release(mx, my, button)) {
				return true;
			}
			selected = false;
		} else if(mx >= x + width - height && mx < x + width && my >= y && my < y + height) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				selected = true;
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				setting.reset();
				updateList();
			}
			return true;
		}
		return super.release(mx, my, button);
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
		for(int i = 0; i < strings.size(); i++) {
			setting.set(i, strings.get(i).getValue());
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
