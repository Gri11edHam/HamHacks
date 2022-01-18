package net.grilledham.hamhacks.modules;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.grilledham.hamhacks.event.EventManager;
import net.grilledham.hamhacks.gui.BoundingBox;
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
	protected BoolSetting enabled = new BoolSetting("Enabled", false);
	protected Category category;
	protected KeySetting key;
	protected BoolSetting showModule = new BoolSetting("HUD Text", true);
	
	protected List<Setting> settings = new ArrayList<>();
	
	protected MinecraftClient mc = MinecraftClient.getInstance();
	protected IMinecraftClient imc = (IMinecraftClient)mc;
	
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
		settings.add(this.showModule);
		settings.add(this.key);
	}
	
	public void toggle() {
		enabled.setValue(!enabled.getValue());
		if(enabled.getValue()) {
			onEnable();
		} else {
			onDisable();
		}
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled.setValue(enabled);
		if(this.enabled.getValue()) {
			onEnable();
		} else {
			onDisable();
		}
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
		private final BoundingBox box;
		
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
				category.getBox().resizeScreen();
				category.getBox().setScaleFactor(2);
				category.getBox().setScale(1);
				List<Module> categoryModules = ModuleManager.getModules(category);
				int categoryWidth = textRenderer.getWidth(category.getText());
				for(Module module : categoryModules) {
					categoryWidth = Math.max(textRenderer.getWidth(module.getName()), categoryWidth);
				}
				categoryWidth += 4;
				categoryWidth += textRenderer.fontHeight;
				category.setPos(x, y);
				category.setDimensions(categoryWidth, textRenderer.fontHeight + 4);
				if(x + categoryWidth + 2 > MinecraftClient.getInstance().getWindow().getScaledWidth()) {
					x = 1;
					y += textRenderer.fontHeight + 6;
				} else {
					x += categoryWidth + 2;
				}
			}
		}
		
		Category(String text) {
			this.text = text;
			box = new BoundingBox(0, 0, 0, 0, BoundingBox.ScreenQuad.TOP_LEFT);
		}
		
		public String getText() {
			return text;
		}
		
		public void setPos(int x, int y) {
			box.move(x - box.getX(), y - box.getY());
		}
		
		public void resize() {
			box.resizeScreen();
			box.setScaleFactor(2);
		}
		
		public void setDimensions(int width, int height) {
			box.resize(width, height);
		}
		
		public void expand(boolean expanded) {
			this.expanded = expanded;
		}
		
		public boolean isExpanded() {
			return expanded;
		}
		
		public int getX() {
			return (int)box.getX();
		}
		
		public int getY() {
			return (int)box.getY();
		}
		
		public int getWidth() {
			return (int)box.getWidth();
		}
		
		public int getHeight() {
			return (int)box.getHeight();
		}
		
		public BoundingBox getBox() {
			return box;
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
