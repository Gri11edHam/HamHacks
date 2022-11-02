package net.grilledham.hamhacks.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

import java.util.List;

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
		for(Category category : Category.values()) {
			List<Module> categoryModules = ModuleManager.getModules(category);
			int categoryWidth = textRenderer.getWidth(category.getName());
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
		for(Category category : Category.values()) {
			List<Module> categoryModules = ModuleManager.getModules(category);
			int categoryWidth = textRenderer.getWidth(category.getName());
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
	
	public String getName() {
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
	
	public String getTranslationKey() {
		return ((TranslatableTextContent)text.getContent()).getKey();
	}
}
