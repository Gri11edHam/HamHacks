package net.grilledham.hamhacks.page;

import net.grilledham.hamhacks.event.EventManager;
import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.gui.element.impl.SettingCategoryElement;
import net.grilledham.hamhacks.setting.Setting;
import net.grilledham.hamhacks.setting.SettingCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

import java.util.ArrayList;
import java.util.List;

public class Page {

	protected Text name;
	
	protected Text toolTip;
	
	protected final List<SettingCategory> settingCategories = new ArrayList<>();
	
	protected MinecraftClient mc = MinecraftClient.getInstance();
	
	public Page(Text name) {
		this.name = name;
		this.toolTip = Text.translatable(getConfigName() + ".tooltip");
		EventManager.register(this);
	}
	
	public String getName() {
		return this.name.getString();
	}
	
	public String getToolTip() {
		return this.toolTip.getString();
	}
	
	public boolean hasToolTip() {
		return !getToolTip().equals(getConfigName() + ".tooltip");
	}
	
	public List<SettingCategory> getSettingCategories() {
		return settingCategories;
	}
	
	public String getConfigName() {
		return ((TranslatableTextContent)name.getContent()).getKey();
	}
	
	public List<GuiElement> getGuiElements(double scale) {
		List<GuiElement> settingElements = new ArrayList<>();
		GuiElement element;
		float maxWidth = 0;
		SettingCategoryElement categoryElement;
		for(SettingCategory category : getSettingCategories()) {
			categoryElement = new SettingCategoryElement(category, 0, 0, scale);
			for(Setting<?> setting : category.getSettings()) {
				categoryElement.addElement(element = setting.getElement(0, 0, scale));
				settingElements.add(element);
				if(maxWidth < element.getWidth()) {
					maxWidth = element.getWidth();
				}
			}
			settingElements.add(categoryElement);
			if(maxWidth < categoryElement.getWidth()) {
				maxWidth = categoryElement.getWidth();
			}
		}
		return settingElements;
	}
	
	@Override
	public String toString() {
		return "Page{" +
				"name=" + getName() +
				", config_name=" + getConfigName() +
				'}';
	}
}
