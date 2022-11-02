package net.grilledham.hamhacks.gui.screen.impl;

import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.gui.element.impl.*;
import net.grilledham.hamhacks.gui.screen.GuiScreen;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.grilledham.hamhacks.setting.*;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ModuleSettingsScreen extends GuiScreen {
	
	private final Module module;
	
	private GuiElement topElement;
	private ScrollableElement scrollArea;
	
	public ModuleSettingsScreen(Screen last, Module module, float scale) {
		super(Text.translatable("hamhacks.menu.clickGui.module"), last, scale);
		this.module = module;
	}
	
	@Override
	protected void init() {
		super.init();
		client.keyboard.setRepeatEvents(true);
		float maxWidth = 0;
		topElement = new GuiElement(-1, 0, client.textRenderer.getWidth(module.getName()) + 2 + 2, 16, scale) {
			@Override
			public void render(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
				float x = this.x + scrollX;
				float y = this.y + scrollY;
				stack.push();
				RenderUtil.preRender();
				
				int bgC = PageManager.getPage(ClickGUI.class).accentColor.getRGB();
				RenderUtil.drawRect(stack, x, y, width, height, bgC);
				
				mc.textRenderer.drawWithShadow(stack, module.getName(), x + width / 2f - mc.textRenderer.getWidth(module.getName()) / 2f, y + 4, PageManager.getPage(ClickGUI.class).textColor.getRGB());
				
				RenderUtil.postRender();
				stack.pop();
			}
		};
		List<GuiElement> settingElements = new ArrayList<>();
		GuiElement element;
		SettingCategoryElement categoryElement;
		int totalHeight = 0;
		for(Text category : SettingHelper.getCategories(module)) {
			categoryElement = new SettingCategoryElement(module, category, 0, 0, scale);
			for(Field f : SettingHelper.getSettings(module, category)) {
				if(f.isAnnotationPresent(BoolSetting.class)) {
					categoryElement.addElement(element = new BoolSettingElement(0, 0, scale, f, module));
				} else if(f.isAnnotationPresent(ColorSetting.class)) {
					categoryElement.addElement(element = new ColorSettingElement(0, 0, scale, f, module));
				} else if(f.isAnnotationPresent(NumberSetting.class)) {
					categoryElement.addElement(element = new NumberSettingElement(0, 0, scale, f, module));
				} else if(f.isAnnotationPresent(KeySetting.class)) {
					categoryElement.addElement(element = new KeySettingElement(0, 0, scale, f, module));
				} else if(f.isAnnotationPresent(ListSetting.class)) {
					categoryElement.addElement(element = new ListSettingElement(0, 0, scale, f, module));
				} else if(f.isAnnotationPresent(SelectionSetting.class)) {
					categoryElement.addElement(element = new SelectionSettingElement(0, 0, scale, f, module));
				} else if(f.isAnnotationPresent(StringSetting.class)) {
					categoryElement.addElement(element = new StringSettingElement(0, 0, scale, f, module));
				} else {
					categoryElement.addElement(element = new GuiElement(0, 0, 0, 16, scale) {
						@Override
						public void render(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
							float x = this.x + scrollX;
							float y = this.y + scrollY;
							stack.push();
							RenderUtil.preRender();
							
							int bgC = PageManager.getPage(ClickGUI.class).bgColor.getRGB();
							boolean hovered = mx >= x && mx < x + width && my >= y && my < y + height;
							bgC = RenderUtil.mix(PageManager.getPage(ClickGUI.class).bgColorHovered.getRGB(), bgC, hovered ? 1 : 0);
							RenderUtil.drawRect(stack, x, y, width, height, bgC);
							
							mc.textRenderer.drawWithShadow(stack, "uhhhh", x + 2, y + 4, PageManager.getPage(ClickGUI.class).textColor.getRGB());
							
							RenderUtil.postRender();
							stack.pop();
						}
					});
				}
				settingElements.add(element);
				if(maxWidth < element.getWidth()) {
					maxWidth = element.getWidth();
				}
			}
			settingElements.add(categoryElement);
			if(maxWidth < categoryElement.getWidth()) {
				maxWidth = categoryElement.getWidth();
			}
			totalHeight += categoryElement.getHeight();
		}
		int yAdd = 0;
		scrollArea = new ScrollableElement(0, 0, 0, (int)(height * (2 / 3f)), scale);
		scrollArea.clearElements();
		for(GuiElement guiElement : settingElements) {
			guiElement.moveTo(width / 2f - maxWidth / 2, (int)(height - Math.min(height * (5 / 6f), totalHeight + height * (5 / 6f)) + yAdd));
			guiElement.resize(maxWidth, guiElement.getHeight());
			yAdd += guiElement.getHeight();
			if(guiElement instanceof SettingCategoryElement) {
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
					if(element instanceof SettingElement) {
						boolean shouldShowElement = SettingHelper.shouldShow(((SettingElement)element).getSetting(), ((SettingElement)element).getObject());
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
