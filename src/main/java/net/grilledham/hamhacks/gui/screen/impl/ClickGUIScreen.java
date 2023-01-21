package net.grilledham.hamhacks.gui.screen.impl;

import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.gui.element.impl.*;
import net.grilledham.hamhacks.gui.screen.GuiScreen;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.page.Page;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ClickGUIScreen extends GuiScreen {
	
	private static Page page;
	
	private GuiElement topElement;
	private ScrollableElement scrollArea;
	
	public ClickGUIScreen(Screen last) {
		super(Text.translatable("hamhacks.menu.clickGui"), last, PageManager.getPage(ClickGUI.class).scale.get());
	}
	
	@Override
	protected void init() {
		super.init();
		if(page == null) {
			for(Category category : Category.values()) {
				elements.add(new CategoryElement(this, category, scale));
			}
		} else {
			float maxWidth = 0;
			topElement = new GuiElement(-1, 0, client.textRenderer.getWidth(page.getName()) + 2 + 2, 16, scale) {
				@Override
				public void render(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
					float x = this.x + scrollX;
					float y = this.y + scrollY;
					stack.push();
					RenderUtil.preRender();
					
					int bgC = PageManager.getPage(ClickGUI.class).accentColor.get().getRGB();
					RenderUtil.drawRect(stack, x, y, width, height, bgC);
					
					mc.textRenderer.drawWithShadow(stack, page.getName(), x + width / 2f - mc.textRenderer.getWidth(page.getName()) / 2f, y + 4, PageManager.getPage(ClickGUI.class).textColor.get().getRGB());
					
					RenderUtil.postRender();
					stack.pop();
				}
			};
			List<GuiElement> settingElements = page.getGuiElements(scale);
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
		int i = 0;
		int pages = PageManager.getPages().size() + 1;
		List<PageElement> pageElements = new ArrayList<>();
		float pagesWidth = 0;
		pageElements.add(new PageElement(this, 0, 0, scale, null, true, i == pages - 1));
		pagesWidth += pageElements.get(i).getWidth();
		i++;
		for(Page p : PageManager.getPages()) {
			pageElements.add(new PageElement(this, 0, 0, scale, p, false, i == pages - 1));
			pagesWidth += pageElements.get(i).getWidth();
			i++;
		}
		float xAdd = 0;
		for(PageElement pageElement : pageElements) {
			pageElement.moveTo(width / 2f - pagesWidth / 2f + xAdd, 0);
			xAdd += pageElement.getWidth();
			elements.add(pageElement);
		}
	}
	
	public void updatePartVisibility() {
		if(topElement == null || scrollArea == null) {
			return;
		}
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
	public void render(MatrixStack stack, int mx, int my, float tickDelta) {
		if(page != null) {
			updatePartVisibility();
		}
		super.render(stack, mx, my, tickDelta);
	}
	
	public void setPage(Page page) {
		if(page != ClickGUIScreen.page) {
			ClickGUIScreen.page = page;
			markDirty();
		}
	}

	@Override
	public boolean shouldPause() {
		return false;
	}
}
