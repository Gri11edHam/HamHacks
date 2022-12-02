package net.grilledham.hamhacks.modules;

import net.grilledham.hamhacks.event.EventManager;
import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.gui.element.impl.SettingCategoryElement;
import net.grilledham.hamhacks.mixininterface.IMinecraftClient;
import net.grilledham.hamhacks.setting.BoolSetting;
import net.grilledham.hamhacks.setting.KeySetting;
import net.grilledham.hamhacks.setting.Setting;
import net.grilledham.hamhacks.setting.SettingCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

import java.util.ArrayList;
import java.util.List;

public class Module {
	
	protected Text name;
	
	protected Text toolTip;
	
	protected Category category;
	
	protected final List<SettingCategory> settingCategories = new ArrayList<>();
	
	protected final SettingCategory GENERAL_CATEGORY = new SettingCategory("hamhacks.module.generic.category.general");
	
	protected final BoolSetting showModule = new BoolSetting("hamhacks.module.generic.showModule", true, () -> true);
	
	protected final KeySetting key;
	
	public BoolSetting enabled = new BoolSetting("hamhacks.module.generic.enabled", false, () -> true);
	
	protected MinecraftClient mc = MinecraftClient.getInstance();
	protected IMinecraftClient imc = (IMinecraftClient)mc;
	
	private int forceDisabled = 0;
	protected boolean wasEnabled;
	protected boolean lastEnabled;
	
	public Module(Text name, Category category, Keybind key) {
		this.name = name;
		this.toolTip = Text.translatable(getConfigName() + ".tooltip");
		this.category = category;
		this.key = new KeySetting("hamhacks.module.generic.keybind", key, () -> true);
		
		settingCategories.add(GENERAL_CATEGORY);
		
		GENERAL_CATEGORY.add(showModule, true);
		GENERAL_CATEGORY.add(this.key, true);
		GENERAL_CATEGORY.add(enabled, true);
	}
	
	public void checkKeybind() {
		while(key.get().wasPressed()) {
			toggle();
		}
	}
	
	public void toggle() {
		if(this.forceDisabled == 0) {
			enabled.toggle();
		}
	}
	
	public void setEnabled(boolean enabled) {
		if(this.forceDisabled == 0) {
			this.enabled.set(enabled);
		}
	}
	
	public void forceDisable() {
		if(forceDisabled == 0) {
			wasEnabled = isEnabled();
		}
		setEnabled(false);
		forceDisabled++;
	}
	
	public void reEnable() {
		forceDisabled--;
		if(forceDisabled == 0) {
			setEnabled(wasEnabled);
		}
	}
	
	public void onEnable() {
		EventManager.register(this);
	}
	
	public void onDisable() {
		EventManager.unRegister(this);
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
	
	public boolean isEnabled() {
		return this.enabled.get();
	}
	
	public boolean shouldShowModule() {
		return showModule.get();
	}
	
	public Keybind getKey() {
		return key.get();
	}
	
	public List<SettingCategory> getSettingCategories() {
		return settingCategories;
	}
	
	public String getConfigName() {
		return ((TranslatableTextContent)name.getContent()).getKey();
	}
	
	public void updateEnabled() {
		if(lastEnabled != enabled.get()) {
			if(enabled.get()) {
				onEnable();
			} else{
				onDisable();
			}
		}
		lastEnabled = enabled.get();
	}
	
	public String getHUDText() {
		return getName();
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
		return "Module{" +
				"name=" + getName() +
				", config_name=" + getConfigName() +
				'}';
	}
}
