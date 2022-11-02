package net.grilledham.hamhacks.modules;

import net.grilledham.hamhacks.event.EventManager;
import net.grilledham.hamhacks.mixininterface.IMinecraftClient;
import net.grilledham.hamhacks.setting.BoolSetting;
import net.grilledham.hamhacks.setting.KeySetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

public class Module {
	
	protected Text name;
	
	protected Text toolTip;
	
	protected Category category;
	
	@BoolSetting(name = "hamhacks.module.generic.showModule", defaultValue = true)
	public boolean showModule = true;
	
	@KeySetting(name = "hamhacks.module.generic.keybind")
	public Keybind key;
	
	@BoolSetting(name = "hamhacks.module.generic.enabled")
	public boolean enabled = false;
	
	protected MinecraftClient mc = MinecraftClient.getInstance();
	protected IMinecraftClient imc = (IMinecraftClient)mc;
	
	private int forceDisabled = 0;
	protected boolean wasEnabled;
	protected boolean lastEnabled;
	
	public Module(Text name, Category category, Keybind key) {
		this.name = name;
		this.toolTip = Text.translatable(getConfigName() + ".tooltip");
		this.category = category;
		this.key = key;
	}
	
	public void checkKeybind() {
		while(key.wasPressed()) {
			toggle();
		}
	}
	
	public void toggle() {
		if(this.forceDisabled == 0) {
			enabled = !enabled;
		}
	}
	
	public void setEnabled(boolean enabled) {
		if(this.forceDisabled == 0) {
			this.enabled = enabled;
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
		return this.enabled;
	}
	
	public boolean shouldShowModule() {
		return showModule;
	}
	
	public Keybind getKey() {
		return key;
	}
	
	public String getConfigName() {
		return ((TranslatableTextContent)name.getContent()).getKey();
	}
	
	public void updateEnabled() {
		if(lastEnabled != enabled) {
			if(enabled){
				onEnable();
			} else{
				onDisable();
			}
		}
		lastEnabled = enabled;
	}
	
	public String getHUDText() {
		return getName();
	}
	
	@Override
	public String toString() {
		return "Module{" +
				"name=" + getName() +
				", config_name=" + getConfigName() +
				'}';
	}
	
}
