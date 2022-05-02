package net.grilledham.hamhacks.modules;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.grilledham.hamhacks.event.EventManager;
import net.grilledham.hamhacks.mixininterface.IMinecraftClient;
import net.grilledham.hamhacks.util.setting.Setting;
import net.grilledham.hamhacks.util.setting.settings.BoolSetting;
import net.grilledham.hamhacks.util.setting.settings.KeySetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

import java.util.ArrayList;
import java.util.List;

public class Module {
	
	protected String name;
	protected BoolSetting enabled = new BoolSetting("Enabled", false) {
		@Override
		protected void valueChanged() {
			super.valueChanged();
			if(getValue()) {
				onEnable();
			} else {
				onDisable();
			}
		}
	};
	protected Category category;
	protected KeySetting key;
	protected BoolSetting showModule = new BoolSetting("HUD Text", true);
	
	protected List<Setting> shownSettings = new ArrayList<>();
	protected List<Setting> settings = new ArrayList<>();
	
	protected MinecraftClient mc = MinecraftClient.getInstance();
	protected IMinecraftClient imc = (IMinecraftClient)mc;
	
	protected BoolSetting forceDisabled = new BoolSetting("internal.forcedisabled", false);
	protected boolean wasEnabled;
	
	public Module(String name, Category category, Keybind key) {
		this.name = name;
		this.category = category;
		this.key = new KeySetting("Keybind", key);
		
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if(key.wasPressed()) {
				toggle();
			}
		});
		addSettings();
		addSetting(this.showModule);
		addSetting(this.key);
		addSetting(this.enabled);
		addSetting(this.forceDisabled);
		hideSetting(this.forceDisabled);
	}
	
	protected void addSetting(Setting s) {
		settings.add(s);
		shownSettings.add(s);
	}
	
	protected void showSetting(Setting s, int index) {
		shownSettings.add(index, s);
	}
	
	protected void hideSetting(Setting s) {
		shownSettings.remove(s);
	}
	
	public void toggle() {
		if(!this.forceDisabled.getValue()) {
			enabled.setValue(!enabled.getValue());
		}
	}
	
	public void setEnabled(boolean enabled) {
		if(!this.forceDisabled.getValue()) {
			this.enabled.setValue(enabled);
		}
	}
	
	public void forceDisable() {
		wasEnabled = isEnabled();
		setEnabled(false);
		forceDisabled.setValue(true);
	}
	
	public void reEnable() {
		forceDisabled.setValue(false);
		setEnabled(wasEnabled);
	}
	
	public void addSettings() {
	}
	
	public void onEnable() {
		EventManager.register(this);
	}
	
	public void onDisable() {
		EventManager.unRegister(this);
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean isEnabled() {
		return this.enabled.getValue();
	}
	
	public boolean shouldShowModule() {
		return showModule.getValue();
	}
	
	public List<Setting> getShownSettings() {
		return shownSettings;
	}
	
	public List<Setting> getSettings() {
		return settings;
	}
	
	public Keybind getKey() {
		return key.getKeybind();
	}
	
	public enum Category {
		MOVEMENT("Movement"),
		COMBAT("Combat"),
		RENDER("Render"),
		PLAYER("Player"),
		WORLD("World"),
		MISC("Misc");
		
		private final String text;
		private int x;
		private int y;
		private int width;
		private int height;
		
		private boolean expanded = false;
		
		private static boolean hasInitialized = false;
		
		public static void init() {
			if(hasInitialized) {
				return;
			}
			hasInitialized = true;
			int x = 1;
			int y = 3;
			TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
			for(Module.Category category : Module.Category.values()) {
				List<Module> categoryModules = ModuleManager.getModules(category);
				int categoryWidth = textRenderer.getWidth(category.getText());
				for(Module module : categoryModules) {
					categoryWidth = Math.max(textRenderer.getWidth(module.getName()), categoryWidth);
				}
				categoryWidth += 2;
				category.setPos(x, y);
				category.setDimensions(categoryWidth + 4, 17);
				if(x + categoryWidth + 2 > MinecraftClient.getInstance().getWindow().getScaledWidth()) {
					x = 1;
					y += 19;
				} else {
					x += categoryWidth + 6;
				}
			}
		}
		
		Category(String text) {
			this.text = text;
			x = 0;
			y = 0;
		}
		
		public String getText() {
			return text;
		}
		
		public void setPos(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public void setDimensions(int width, int height) {
			this.width = width;
			this.height = height;
		}
		
		public void expand(boolean expanded) {
			this.expanded = expanded;
		}
		
		public boolean isExpanded() {
			return expanded;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		public int getWidth() {
			return width;
		}
		
		public int getHeight() {
			return height;
		}
		
		public Category fromText(String text) {
			for(Category category : values()) {
				if(category.text.equals(text)) {
					return category;
				}
			}
			return null;
		}
	}
}
