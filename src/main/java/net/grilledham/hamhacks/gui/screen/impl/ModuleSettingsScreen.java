package net.grilledham.hamhacks.gui.screen.impl;

import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.gui.element.impl.ScrollableElement;
import net.grilledham.hamhacks.gui.element.impl.SettingCategoryElement;
import net.grilledham.hamhacks.gui.element.impl.SettingElement;
import net.grilledham.hamhacks.gui.screen.GuiScreen;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.List;

public class ModuleSettingsScreen extends GuiScreen {
	
	private final Module module;
	
	private GuiElement topElement;
	private ScrollableElement scrollArea;
	
	public ModuleSettingsScreen(Screen last, Module module, double scale) {
		super(Text.translatable("hamhacks.menu.clickGui.module"), last, scale);
		this.module = module;
	}
	
	@Override
	protected void init() {
		super.init();
		float maxWidth = 0;
		topElement = new GuiElement(-1, 0, client.textRenderer.getWidth(module.getName()) + 2 + 2, 16, scale) {
			@Override
			public void render(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
				float x = this.x + scrollX;
				float y = this.y + scrollY;
				stack.push();
				RenderUtil.preRender();
				
				int bgC = PageManager.getPage(ClickGUI.class).accentColor.get().getRGB();
				RenderUtil.drawRect(stack, x, y, width, height, bgC);
				
				mc.textRenderer.drawWithShadow(stack, module.getName(), x + width / 2f - mc.textRenderer.getWidth(module.getName()) / 2f, y + 4, PageManager.getPage(ClickGUI.class).textColor.get().getRGB());
				
				RenderUtil.postRender();
				stack.pop();
			}
		};
		List<GuiElement> settingElements = module.getGuiElements(scale);
		int totalHeight = 0;
		boolean hasCategory = false;
		for(GuiElement e : settingElements) {
			totalHeight += e.getHeight();
			if(maxWidth < e.getWidth()) {
				maxWidth = e.getWidth();
			}
			if(e instanceof SettingCategoryElement) {
				hasCategory = true;
			}
		}
		int yAdd = 0;
		scrollArea = new ScrollableElement(0, 0, 0, (int)(height * (2 / 3f)), scale);
		scrollArea.clearElements();
		for(GuiElement guiElement : settingElements) {
			guiElement.moveTo(width / 2f - maxWidth / 2, (int)(height - Math.min(height * (5 / 6f), totalHeight + height * (5 / 6f)) + yAdd));
			guiElement.resize(maxWidth, guiElement.getHeight());
			yAdd += guiElement.getHeight();
			if(guiElement instanceof SettingCategoryElement || !hasCategory) {
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
		int totalHeight = 0;
		float maxWidth = topElement.getPreferredWidth();
		for(GuiElement categoryElement : scrollArea.getElements()) {
			if(categoryElement instanceof SettingCategoryElement) {
				boolean shouldShow = false;
				for(GuiElement element : ((SettingCategoryElement)categoryElement).getElements()) {
					if(element instanceof SettingElement<?>) {
						boolean shouldShowElement = ((SettingElement<?>)element).shouldShow();
						((SettingCategoryElement)categoryElement).setEnabled(element, shouldShowElement);
						if(shouldShowElement) {
							shouldShow = true;
						}
					}
				}
				scrollArea.setEnabled(categoryElement, shouldShow);
				if(shouldShow) {
					totalHeight += categoryElement.getHeight();
					if(maxWidth < categoryElement.getWidth()) {
						maxWidth = categoryElement.getWidth();
					}
				}
			}
			if(categoryElement instanceof SettingElement<?>) {
				boolean shouldShowElement = ((SettingElement<?>)categoryElement).shouldShow();
				scrollArea.setEnabled(categoryElement, shouldShowElement);
				if(shouldShowElement) {
					totalHeight += categoryElement.getHeight();
					if(maxWidth < categoryElement.getWidth()) {
						maxWidth = categoryElement.getWidth();
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
