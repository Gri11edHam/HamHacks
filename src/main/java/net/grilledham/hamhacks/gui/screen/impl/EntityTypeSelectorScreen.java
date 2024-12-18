package net.grilledham.hamhacks.gui.screen.impl;

import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.gui.element.impl.SearchableScrollableElement;
import net.grilledham.hamhacks.gui.element.impl.SettingCategoryElement;
import net.grilledham.hamhacks.gui.element.impl.SettingElement;
import net.grilledham.hamhacks.gui.screen.GuiScreen;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.grilledham.hamhacks.setting.EntityTypeSelector;
import net.grilledham.hamhacks.setting.SettingCategory;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

public class EntityTypeSelectorScreen extends GuiScreen {
	
	private EntityTypeSelector setting;
	
	private GuiElement topElement;
	private SearchableScrollableElement scrollArea;
	
	public EntityTypeSelectorScreen(Screen last, double scale, EntityTypeSelector setting) {
		super(Text.translatable("hamhacks.menu.clickGui.module.settings"), last, scale);
		this.setting = setting;
	}
	
	@Override
	protected void init() {
		super.init();
		float maxWidth = 0;
		topElement = new GuiElement(-1, 0, RenderUtil.getStringWidth(setting.getName()) + 2 + 2, 16, scale) {
			@Override
			public void draw(DrawContext ctx, int mx, int my, float scrollX, float scrollY, float partialTicks) {
				MatrixStack stack = ctx.getMatrices();
				float x = this.x + scrollX;
				float y = this.y + scrollY;
				stack.push();
				RenderUtil.preRender();
				
				int bgC = PageManager.getPage(ClickGUI.class).accentColor.get().getRGB();
				RenderUtil.drawRect(stack, x, y, width, height, bgC);
				
				RenderUtil.drawString(ctx, setting.getName(), x + width / 2f - RenderUtil.getStringWidth(setting.getName()) / 2f, y + 4, PageManager.getPage(ClickGUI.class).textColor.get().getRGB(), true);
				
				RenderUtil.postRender();
				stack.pop();
			}
		};
		SettingCategory ANIMAL = new SettingCategory("hamhacks.setting.entityTypeSelector.category.animal");
		SettingCategoryElement animalElement = new SettingCategoryElement(ANIMAL, 0, 0, scale);
		SettingCategory WATER_ANIMAL = new SettingCategory("hamhacks.setting.entityTypeSelector.category.waterAnimal");
		SettingCategoryElement waterAnimalElement = new SettingCategoryElement(WATER_ANIMAL, 0, 0, scale);
		SettingCategory MONSTER = new SettingCategory("hamhacks.setting.entityTypeSelector.category.monster");
		SettingCategoryElement monsterElement = new SettingCategoryElement(MONSTER, 0, 0, scale);
		SettingCategory AMBIENT = new SettingCategory("hamhacks.setting.entityTypeSelector.category.ambient");
		SettingCategoryElement ambientElement = new SettingCategoryElement(AMBIENT, 0, 0, scale);
		SettingCategory MISCELLANEOUS = new SettingCategory("hamhacks.setting.entityTypeSelector.category.miscellaneous");
		SettingCategoryElement miscellaneousElement = new SettingCategoryElement(MISCELLANEOUS, 0, 0, scale);
		
		this.scrollArea = new SearchableScrollableElement(0, 0, 0, (int)(height * (2 / 3f)), scale);
		
		for(EntityType<?> type : Registries.ENTITY_TYPE) {
			if(!setting.filter.test(type)) {
				continue;
			}
			switch(type.getSpawnGroup()) {
				case CREATURE -> {
					ANIMAL.add(setting.get().get(type));
					animalElement.addElement(setting.get().get(type).getElement(0, 0, scale));
				}
				case WATER_AMBIENT, WATER_CREATURE, UNDERGROUND_WATER_CREATURE, AXOLOTLS -> {
					WATER_ANIMAL.add(setting.get().get(type));
					waterAnimalElement.addElement(setting.get().get(type).getElement(0, 0, scale));
				}
				case MONSTER -> {
					MONSTER.add(setting.get().get(type));
					monsterElement.addElement(setting.get().get(type).getElement(0, 0, scale));
				}
				case AMBIENT -> {
					AMBIENT.add(setting.get().get(type));
					ambientElement.addElement(setting.get().get(type).getElement(0, 0, scale));
				}
				case MISC -> {
					MISCELLANEOUS.add(setting.get().get(type));
					miscellaneousElement.addElement(setting.get().get(type).getElement(0, 0, scale));
				}
			}
		}
		
		this.scrollArea.addElement(animalElement);
		maxWidth = Math.max(maxWidth, animalElement.getWidth());
		this.scrollArea.addElement(waterAnimalElement);
		maxWidth = Math.max(maxWidth, waterAnimalElement.getWidth());
		this.scrollArea.addElement(monsterElement);
		maxWidth = Math.max(maxWidth, monsterElement.getWidth());
		this.scrollArea.addElement(ambientElement);
		maxWidth = Math.max(maxWidth, ambientElement.getWidth());
		this.scrollArea.addElement(miscellaneousElement);
		maxWidth = Math.max(maxWidth, miscellaneousElement.getWidth());
		scrollArea.moveTo(width / 2f - maxWidth / 2, (int)(height - Math.min(height * (5 / 6f), height * (5 / 6f))));
		scrollArea.resize(maxWidth, 0);
		topElement.moveTo(width / 2f - maxWidth / 2 - 1, (int)(height - Math.min(height * (5 / 6f), height * (5 / 6f))) - topElement.getHeight());
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
			if(element instanceof SettingCategoryElement) {
				for(GuiElement subElement : ((SettingCategoryElement)element).getElements()) {
					if(subElement instanceof SettingElement) {
						boolean shouldShow = ((SettingElement)subElement).shouldShow();
						((SettingCategoryElement)element).setEnabled(subElement, shouldShow);
					}
				}
				scrollArea.setEnabled(element, true);
				totalHeight += element.getHeight();
				if(maxWidth < element.getWidth()) {
					maxWidth = element.getWidth();
				}
			}
		}
		scrollArea.moveTo(width / 2f - maxWidth / 2, (int)(height - Math.min(height * (5 / 6f), totalHeight + height * (5 / 6f))));
		scrollArea.resize(maxWidth, 0);
		topElement.moveTo(width / 2f - maxWidth / 2 - 1, (int)(height - Math.min(height * (5 / 6f), totalHeight + height * (5 / 6f))) - topElement.getHeight());
		topElement.resize(maxWidth + 2, topElement.getHeight());
	}
	
	@Override
	public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
		updatePartVisibility();
		super.render(ctx, mouseX, mouseY, delta);
	}
	
	@Override
	public boolean shouldPause() {
		return false;
	}
}
