package net.grilledham.hamhacks.gui.screen.impl;

import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.gui.element.impl.SearchableScrollableElement;
import net.grilledham.hamhacks.gui.element.impl.SettingElement;
import net.grilledham.hamhacks.gui.screen.GuiScreen;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.grilledham.hamhacks.setting.Setting;
import net.grilledham.hamhacks.setting.SettingContainer;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class SettingContainerScreen extends GuiScreen {
	
	private final SettingContainer<?, ?> setting;
	
	private GuiElement topElement;
	private SearchableScrollableElement scrollArea;
	
	public SettingContainerScreen(Screen last, double scale, SettingContainer<?, ?> setting) {
		super(Text.translatable("hamhacks.menu.clickGui.module.settings"), last, scale);
		this.setting = setting;
	}
	
	@Override
	protected void init() {
		super.init();
		float maxWidth = 0;
		topElement = new GuiElement(-1, 0, client.textRenderer.getWidth(setting.getName()) + 2 + 2, 16, scale) {
			@Override
			public void render(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
				float x = this.x + scrollX;
				float y = this.y + scrollY;
				stack.push();
				RenderUtil.preRender();
				
				int bgC = PageManager.getPage(ClickGUI.class).accentColor.get().getRGB();
				RenderUtil.drawRect(stack, x, y, width, height, bgC);
				
				mc.textRenderer.drawWithShadow(stack, setting.getName(), x + width / 2f - mc.textRenderer.getWidth(setting.getName()) / 2f, y + 4, PageManager.getPage(ClickGUI.class).textColor.get().getRGB());
				
				RenderUtil.postRender();
				stack.pop();
			}
		};
		List<GuiElement> settingElements = new ArrayList<>();
		GuiElement element;
		int totalHeight = 0;
		for(Setting<?> setting : setting.getSettings()) {
			settingElements.add(element = setting.getElement(0, 0, scale));
			if(maxWidth < element.getWidth()) {
				maxWidth = element.getWidth();
			}
			totalHeight += element.getHeight();
		}
		int yAdd = 0;
		scrollArea = new SearchableScrollableElement(0, 0, 0, (int)(height * (2 / 3f)), scale);
		scrollArea.clearElements();
		for(GuiElement guiElement : settingElements) {
			guiElement.moveTo(width / 2f - maxWidth / 2, (int)(height - Math.min(height * (5 / 6f), totalHeight + height * (5 / 6f)) + yAdd));
			guiElement.resize(maxWidth, guiElement.getHeight());
			yAdd += guiElement.getHeight();
			if(guiElement instanceof SettingElement<?>) {
				scrollArea.addElement(guiElement);
			}
		}
		scrollArea.moveTo(width / 2f - maxWidth / 2, (int)(height - Math.min(height * (5 / 6f), totalHeight + height * (5 / 6f))));
		scrollArea.resize(maxWidth, 0);
		topElement.moveTo(width / 2f - maxWidth / 2 - 1, (int)(height - Math.min(height * (5 / 6f), totalHeight + height * (5 / 6f))) - topElement.getHeight());
		topElement.resize(maxWidth + 2, topElement.getHeight());
		elements.add(topElement);
		elements.add(scrollArea);
		updatePartVisibility();
	}
	
	public void updatePartVisibility() {
		if(!scrollArea.shouldUpdateVisibility()) {
			return;
		}
		int totalHeight = 0;
		float maxWidth = topElement.getPreferredWidth();
		for(GuiElement element : scrollArea.getElements()) {
			if(element instanceof SettingElement<?>) {
				boolean shouldShow = ((SettingElement<?>)element).shouldShow();
				scrollArea.setEnabled(element, shouldShow);
				if(shouldShow) {
					totalHeight += element.getHeight();
					if(maxWidth < element.getWidth()) {
						maxWidth = element.getWidth();
					}
				}
			}
		}
		scrollArea.moveTo(width / 2f - maxWidth / 2, (int)(height - Math.min(height * (5 / 6f), totalHeight + height * (5 / 6f))));
		scrollArea.resize(maxWidth, 0);
		topElement.moveTo(width / 2f - maxWidth / 2 - 1, (int)(height - Math.min(height * (5 / 6f), totalHeight + height * (5 / 6f))) - topElement.getHeight());
		topElement.resize(maxWidth + 2, topElement.getHeight());
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		updatePartVisibility();
		super.render(matrices, mouseX, mouseY, delta);
	}
	
	@Override
	public boolean shouldPause() {
		return false;
	}
}
