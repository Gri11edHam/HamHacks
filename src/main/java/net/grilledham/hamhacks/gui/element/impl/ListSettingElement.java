package net.grilledham.hamhacks.gui.element.impl;

import net.grilledham.hamhacks.animation.Animation;
import net.grilledham.hamhacks.animation.AnimationBuilder;
import net.grilledham.hamhacks.animation.AnimationType;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.grilledham.hamhacks.setting.ListSetting;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ListSettingElement extends SettingElement<List<String>> {
	
	private final Animation hoverAnimation = AnimationBuilder.create(AnimationType.IN_OUT_QUAD, 0.25).build();
	private final Animation selectionAnimation = AnimationBuilder.create(AnimationType.IN_OUT_QUAD, 0.25).build();
	
	private final List<StringSettingElement> stringParts = new ArrayList<>();
	private final List<ButtonElement> removeButtons = new ArrayList<>();
	
	private ButtonElement addButton;
	
	private boolean selected = false;
	
	float maxWidth = 0;
	
	public ListSettingElement(float x, float y, double scale, ListSetting setting) {
		super(x, y, MinecraftClient.getInstance().textRenderer.getWidth(setting.getName()) + 18, scale, setting::getName, setting.hasTooltip() ? setting::getTooltip : () -> "", setting::shouldShow, setting::get, setting::set, setting::reset);
		updateList();
	}
	
	private void updateList() {
		stringParts.clear();
		removeButtons.clear();
		maxWidth = 106 + 16;
		int i = 0;
		for(String s : get.get()) {
			int finalI = i;
			StringSettingElement strSetPart;
			stringParts.add(strSetPart = new StringSettingElement(x, y, scale, s) {
				@Override
				public void updateValue(String value) {
					super.updateValue(value);
					ListSettingElement.this.get.get().set(finalI, value);
				}
			});
			strSetPart.drawBackground = false;
			ButtonElement bPart;
			removeButtons.add(bPart = new ButtonElement("-", x, y, 16, height, scale, () -> {
				ListSettingElement.this.get.get().remove(finalI);
				updateList();
			}));
			i++;
			maxWidth = Math.max(maxWidth, strSetPart.getWidth() + bPart.getWidth());
		}
		for(i = 0; i < stringParts.size(); i++) {
			stringParts.get(i).moveTo(x + width - maxWidth - 2, y + height * (i + 1));
			removeButtons.get(i).moveTo(x + width - maxWidth + stringParts.get(i).getWidth(), y + height * (i + 1));
		}
		addButton = new ButtonElement("+", x + width - maxWidth, y + height * (i + 1), maxWidth, height, scale, () -> {
			get.get().add("");
			updateList();
		});
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
	public void render(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		stack.push();
		RenderUtil.preRender();
		
		ClickGUI ui = PageManager.getPage(ClickGUI.class);
		int bgC = ui.bgColor.get().getRGB();
		RenderUtil.drawRect(stack, x, y, width - height, height, bgC);
		
		boolean hovered = mx >= x + width - height && mx < x + width && my >= y && my < y + height;
		bgC = RenderUtil.mix(ui.bgColorHovered.get().getRGB(), bgC, hoverAnimation.get());
		RenderUtil.drawRect(stack, x + width - height, y, height, height, bgC);
		
		int outlineC = 0xffcccccc;
		RenderUtil.drawHRect(stack, x + width - height, y, height, height, outlineC);
		
		mc.textRenderer.drawWithShadow(stack, getName.get(), x + 2, y + 4, ui.textColor.get().getRGB());
		float dropDownX = x + width - height / 2f - mc.textRenderer.getWidth("<") / 2f;
		float dropDownCenterX = dropDownX + mc.textRenderer.getWidth("<") / 2f;
		float dropDownCenterY = y + 4 + mc.textRenderer.fontHeight / 2f;
		stack.translate(dropDownCenterX, dropDownCenterY, 0);
		stack.peek().getPositionMatrix().multiply(new Quaternion(new Vec3f(0, 0, 1), (float)selectionAnimation.get() * -90, true));
		stack.translate(-dropDownCenterX, -dropDownCenterY, 0);
		mc.textRenderer.drawWithShadow(stack, "<", dropDownX, y + 4, ui.textColor.get().getRGB());
		
		RenderUtil.postRender();
		stack.pop();
		
		hoverAnimation.set(hovered);
		hoverAnimation.update();
		
		selectionAnimation.set(selected);
		selectionAnimation.update();
	}
	
	@Override
	public void renderTop(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		stack.push();
		RenderUtil.preRender();
		RenderUtil.pushScissor(x + width - maxWidth, y + height, maxWidth, (height * (stringParts.size() + 1)) * (float)selectionAnimation.get(), (float)scale);
		RenderUtil.applyScissor();
		
		ClickGUI ui = PageManager.getPage(ClickGUI.class);
		RenderUtil.drawRect(stack, x + width - maxWidth, y + height, maxWidth, height * (stringParts.size() + 1), ui.bgColor.get().getRGB());
		
		addButton.render(stack, mx, my, scrollX, scrollY, partialTicks);
		
		for(int i = 0; i < removeButtons.size(); i++) {
			removeButtons.get(i).render(stack, mx, my, scrollX, scrollY, partialTicks);
		}
		
		for(int i = 0; i < removeButtons.size(); i++) {
			if(((stringParts.size() + 1)) * selectionAnimation.get() > i) {
				stringParts.get(i).render(stack, mx, my, scrollX, scrollY, partialTicks);
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
			addButton.click(mx, my, scrollX, scrollY, button);
			return true;
		}
		return super.click(mx, my, scrollX, scrollY, button);
	}
	
	@Override
	public boolean release(double mx, double my, float scrollX, float scrollY, int button) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		super.release(mx, my, scrollX, scrollY, button);
		if(selected) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && (mx >= x + width - height && mx < x + width && my >= y && my < y + height)) {
				reset.run();
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
			return false;
		} else if(mx >= x + width - height && mx < x + width && my >= y && my < y + height) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				selected = true;
				return true;
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				reset.run();
				updateList();
				return false;
			}
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
			return false;
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
