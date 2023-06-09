package net.grilledham.hamhacks.modules;

import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

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
		int x = 4;
		int y = 24;
		int categoryWidth = PageManager.getPage(ClickGUI.class).categoriesWidth.get().intValue();
		for(Category category : Category.values()) {
			category.setPos(x, y);
			category.width = categoryWidth;
			category.height = (float)17;
			if(x + categoryWidth + 2 > MinecraftClient.getInstance().getWindow().getScaledWidth()) {
				x = 4;
				y += 20;
			} else {
				x += categoryWidth + 6;
			}
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
