package net.grilledham.hamhacks.notification;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventClick;
import net.grilledham.hamhacks.event.events.EventRender;
import net.grilledham.hamhacks.page.Page;
import net.grilledham.hamhacks.setting.ColorSetting;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.grilledham.hamhacks.setting.SettingCategory;
import net.grilledham.hamhacks.util.Color;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class Notifications extends Page {
	
	private static final List<Notification> notifications = new ArrayList<>();
	
	private final SettingCategory GENERAL_CATEGORY = new SettingCategory("hamhacks.page.notifications.category.general");
	
	public final ColorSetting accentColor = new ColorSetting("hamhacks.page.notifications.accentColor", new Color(0xFFAA0000), () -> true);
	
	public final ColorSetting bgColor = new ColorSetting("hamhacks.page.notifications.backgroundColor", new Color(0xFF000000), () -> true);
	
	public final ColorSetting bgColorHovered = new ColorSetting("hamhacks.page.notifications.backgroundColorHovered", new Color(0xFF222222), () -> true);
	
	public final ColorSetting progressColor = new ColorSetting("hamhacks.page.notifications.progressColor", new Color(0xFFAA0000), () -> true);
	
	public final ColorSetting progressColorBG = new ColorSetting("hamhacks.page.notifications.progressColorBackground", new Color(0xFF220000), () -> true);
	
	public final NumberSetting lifeSpan = new NumberSetting("hamhacks.page.notifications.lifeSpan", 5, () -> true, 1, 10, 1, false);
	
	public Notifications() {
		super(Text.translatable("hamhacks.page.notifications"));
		settingCategories.add(0, GENERAL_CATEGORY);
		GENERAL_CATEGORY.add(accentColor);
		GENERAL_CATEGORY.add(bgColor);
		GENERAL_CATEGORY.add(bgColorHovered);
		GENERAL_CATEGORY.add(progressColor);
		GENERAL_CATEGORY.add(progressColorBG);
		GENERAL_CATEGORY.add(lifeSpan);
	}
	
	@EventListener
	public void render(EventRender e) {
		List<Notification> completed = new ArrayList<>();
		float yAdd = 0;
		double mx = mc.mouse.getX() * (double)mc.getWindow().getScaledWidth() / (double)mc.getWindow().getWidth();
		double my = mc.mouse.getY() * (double)mc.getWindow().getScaledHeight() / (double)mc.getWindow().getHeight();
		for(Notification n : notifications) {
			yAdd += n.render(e.context, mx, my, yAdd, e.tickDelta) + 10;
			if(n.isComplete()) {
				completed.add(n);
			}
		}
		notifications.removeAll(completed);
	}
	
	@EventListener
	public void click(EventClick e) {
		for(Notification n : notifications) {
			if(n.click(e.x, e.y, e.button)) {
				e.canceled = true;
				break;
			}
		}
	}
	
	public static void notify(Notification n) {
		notifications.add(n);
	}
	
	public static void notify(String title, String info, Runnable clickEvent) {
		notify(new Notification(title, info, clickEvent));
	}
	
	public static void notify(String title, String info) {
		notify(title, info, null);
	}
	
	public static void notify(String info) {
		notify("HamHacks", info);
	}
}
