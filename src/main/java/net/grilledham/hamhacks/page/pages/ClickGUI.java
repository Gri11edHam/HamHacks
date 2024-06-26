package net.grilledham.hamhacks.page.pages;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.grilledham.hamhacks.HamHacksClient;
import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.gui.element.impl.ButtonElement;
import net.grilledham.hamhacks.gui.screen.GuiScreen;
import net.grilledham.hamhacks.gui.screen.impl.ClickGUIScreen;
import net.grilledham.hamhacks.gui.screen.impl.ModuleSettingsScreen;
import net.grilledham.hamhacks.gui.screen.impl.SettingContainerScreen;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.page.Page;
import net.grilledham.hamhacks.setting.*;
import net.grilledham.hamhacks.util.Color;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ClickGUI extends Page {
	
	private final SettingCategory APPEARANCE_CATEGORY = new SettingCategory("hamhacks.page.clickGui.category.appearance");
	
	public final ColorSetting accentColor = new ColorSetting("hamhacks.page.clickGui.accentColor", Color.getDarkRed(), () -> true);
	public final ColorSetting bgColor = new ColorSetting("hamhacks.page.clickGui.backgroundColor", new Color(0xFF222222), () -> true);
	public final ColorSetting bgColorHovered = new ColorSetting("hamhacks.page.clickGui.backgroundColorHovered", new Color(0xFF555555), () -> true);
	public final ColorSetting enabledColor = new ColorSetting("hamhacks.page.clickGui.enabledColor", new Color(0xFF660000), () -> true);
	public final ColorSetting enabledColorHovered = new ColorSetting("hamhacks.page.clickGui.enabledColorHovered", new Color(0xFFAA5555), () -> true);
	public final ColorSetting textColor = new ColorSetting("hamhacks.page.clickGui.textColor", Color.getWhite(), () -> true);
	public final NumberSetting scale = new NumberSetting("hamhacks.page.clickGui.scale", 2, () -> true, 1, 5, 1);
	public final NumberSetting categoriesWidth = new NumberSetting("hamhacks.page.clickGui.categoriesWidth", 70, () -> true, 20, 200, 1, true);
	public final SelectionSetting font = new SelectionSetting("hamhacks.page.clickGui.font", 1, () -> true) {
		@Override
		public void onChange() {
			super.onChange();
			RenderUtil.updateFont(value);
			HamHacksClient.reloadResources();
			Screen screen = mc.currentScreen;
			if(screen instanceof GuiScreen) ((GuiScreen)screen).markDirty();
		}
	};
	public final NumberSetting fontResMultiplier = new NumberSetting("hamhacks.page.clickGui.fontResMultiplier", 2, () -> true, 1, 8, 1, true);
	public final Setting<Object> reloadFontButton = new Setting<>("hamhacks.page.clickGui.reloadFontButton", 0, () -> true) {
		@Override
		public GuiElement getElement(float x, float y, double scale) {
			return new ButtonElement(getName(), x, y, RenderUtil.getStringWidth(getName()), 19, scale, () -> {
				RenderUtil.reloadFont();
				HamHacksClient.reloadResources();
				Screen screen = mc.currentScreen;
				if(screen instanceof GuiScreen) ((GuiScreen)screen).markDirty();
			});
		}
		
		@Override
		public JsonElement save() {
			return new JsonPrimitive(0);
		}
		
		@Override
		public void load(JsonElement e) {}
	};
	
	private final SettingCategory OPTIONS_CATEGORY = new SettingCategory("hamhacks.page.clickGui.category.options");
	
	public final KeySetting openMenu = new KeySetting("hamhacks.page.clickGui.openMenu", new Keybind(GLFW.GLFW_KEY_RIGHT_SHIFT), () -> true);
	
	public boolean typing = false;
	
	private boolean shouldOpen = false;
	
	public ClickGUI() {
		super(Text.translatable("hamhacks.page.clickGui"));
		settingCategories.add(0, APPEARANCE_CATEGORY);
		APPEARANCE_CATEGORY.add(accentColor);
		APPEARANCE_CATEGORY.add(bgColor);
		APPEARANCE_CATEGORY.add(bgColorHovered);
		APPEARANCE_CATEGORY.add(enabledColor);
		APPEARANCE_CATEGORY.add(enabledColorHovered);
		APPEARANCE_CATEGORY.add(textColor);
		APPEARANCE_CATEGORY.add(scale);
		APPEARANCE_CATEGORY.add(categoriesWidth);
		APPEARANCE_CATEGORY.add(font);
		APPEARANCE_CATEGORY.add(fontResMultiplier);
		APPEARANCE_CATEGORY.add(reloadFontButton);
		settingCategories.add(1, OPTIONS_CATEGORY);
		OPTIONS_CATEGORY.add(openMenu);
	}
	
	@EventListener
	public void tickEvent(EventTick e) {
		while(openMenu.get().wasPressed() || shouldOpen) {
			shouldOpen = false;
			if(!(mc.currentScreen instanceof ClickGUIScreen)) {
				mc.setScreen(new ClickGUIScreen(mc.currentScreen));
			}
		}
	}
	
	public void openMenu() {
		shouldOpen = true;
	}
	
	public boolean moveInScreen(Screen currentScreen) {
		return !typing && (currentScreen instanceof ClickGUIScreen || currentScreen instanceof ModuleSettingsScreen || currentScreen instanceof SettingContainerScreen || currentScreen == null);
	}
}
