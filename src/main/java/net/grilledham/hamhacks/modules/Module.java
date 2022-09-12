package net.grilledham.hamhacks.modules;

import net.grilledham.hamhacks.event.EventManager;
import net.grilledham.hamhacks.mixininterface.IMinecraftClient;
import net.grilledham.hamhacks.util.setting.BoolSetting;
import net.grilledham.hamhacks.util.setting.KeySetting;
import net.grilledham.hamhacks.util.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

import java.util.List;

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
	
	@NumberSetting(name = "hamhacks.module.generic.internal.forceDisabled", min = 0, max = 500, step = 1, neverShow = true)
	public float forceDisabled = 0;
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
		wasEnabled = isEnabled();
		setEnabled(false);
		forceDisabled++;
	}
	
	public void reEnable() {
		forceDisabled--;
		setEnabled(wasEnabled);
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
	
	public enum Category {
		MOVEMENT(Text.translatable("hamhacks.category.movement")),
		COMBAT(Text.translatable("hamhacks.category.combat")),
		RENDER(Text.translatable("hamhacks.category.render")),
		PLAYER(Text.translatable("hamhacks.category.player")),
		WORLD(Text.translatable("hamhacks.category.world")),
		MISC(Text.translatable("hamhacks.category.misc"));
		
		private final Text text;
		private float x;
		private float y;
		private float width;
		private float height;
		
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
		
		public static void updateLanguage() {
			TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
			for(Module.Category category : Module.Category.values()) {
				List<Module> categoryModules = ModuleManager.getModules(category);
				int categoryWidth = textRenderer.getWidth(category.getText());
				for(Module module : categoryModules) {
					categoryWidth = Math.max(textRenderer.getWidth(module.getName()), categoryWidth);
				}
				categoryWidth += 2;
				category.setDimensions(categoryWidth + 4, 17);
			}
		}
		
		Category(Text text) {
			this.text = text;
			x = 0;
			y = 0;
		}
		
		public String getText() {
			return text.getString();
		}
		
		public void setPos(float x, float y) {
			this.x = x;
			this.y = y;
		}
		
		public void setDimensions(float width, float height) {
			this.width = width;
			this.height = height;
		}
		
		public void expand(boolean expanded) {
			this.expanded = expanded;
		}
		
		public boolean isExpanded() {
			return expanded;
		}
		
		public float getX() {
			return x;
		}
		
		public float getY() {
			return y;
		}
		
		public float getWidth() {
			return width;
		}
		
		public float getHeight() {
			return height;
		}
		
		public Category fromText(String text) {
			for(Category category : values()) {
				if(((TranslatableTextContent)category.text.getContent()).getKey().equals(text)) {
					return category;
				}
			}
			return null;
		}
	}
}
