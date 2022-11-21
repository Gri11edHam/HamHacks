package net.grilledham.hamhacks.setting;

import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class SettingCategory {
	
	protected final Text name;
	protected final Text tooltip;
	
	private final List<Setting<?>> settings = new ArrayList<>();
	private final List<Setting<?>> bottomSettings = new ArrayList<>();
	
	private boolean expanded = true;
	
	public SettingCategory(String name) {
		this.name = Text.translatable(name);
		this.tooltip = Text.translatable(name + ".tooltip");
	}
	
	public void add(Setting<?> setting) {
		add(setting, false);
	}
	
	public void add(Setting<?> setting, boolean bottom) {
		if(bottom) {
			bottomSettings.add(setting);
		} else {
			settings.add(setting);
		}
	}
	
	public String getName() {
		return name.getString();
	}
	
	public List<Setting<?>> getSettings() {
		List<Setting<?>> settings = new ArrayList<>(this.settings);
		settings.addAll(bottomSettings);
		return settings;
	}
	
	public boolean shouldShow() {
		if(settings.isEmpty()) {
			return false;
		}
		for(Setting<?> setting : settings) {
			if(setting.shouldShow()) {
				return true;
			}
		}
		return false;
	}
	
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}
	
	public boolean isExpanded() {
		return expanded;
	}
}
