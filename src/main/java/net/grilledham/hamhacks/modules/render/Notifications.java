package net.grilledham.hamhacks.modules.render;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventClick;
import net.grilledham.hamhacks.event.events.EventRender;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.Color;
import net.grilledham.hamhacks.util.Notification;
import net.grilledham.hamhacks.util.setting.ColorSetting;
import net.grilledham.hamhacks.util.setting.NumberSetting;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class Notifications extends Module {
	
	private static final List<Notification> notifications = new ArrayList<>();
	
	@ColorSetting(name = "hamhacks.module.notifications.accentColor")
	public Color accentColor = new Color(0x80AA0000);
	
	@ColorSetting(name = "hamhacks.module.notifications.backgroundColor")
	public Color bgColor = new Color(0x80000000);
	
	@ColorSetting(name = "hamhacks.module.notifications.backgroundColorHovered")
	public Color bgColorHovered = new Color(0x80ffffff);
	
	@ColorSetting(name = "hamhacks.module.notifications.progressColor")
	public Color progressColor = new Color(0x80AA0000);
	
	@ColorSetting(name = "hamhacks.module.notifications.progressColorBackground")
	public Color progressColorBG = new Color(0x80000000);
	
	@NumberSetting(
			name = "hamhacks.module.notifications.lifeSpan",
			defaultValue = 5,
			min = 1,
			max = 10,
			forceStep = false,
			step = 1
	)
	public float lifeSpan = 5;
	
	public Notifications() {
		super(Text.translatable("hamhacks.module.notifications"), Category.RENDER, new Keybind(0));
		enabled = true;
		showModule = false;
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		setEnabled(true);
		onEnable();
	}
	
	@EventListener
	public void render(EventRender e) {
		List<Notification> completed = new ArrayList<>();
		float yAdd = 0;
		double mx = mc.mouse.getX() * (double)mc.getWindow().getScaledWidth() / (double)mc.getWindow().getWidth();
		double my = mc.mouse.getY() * (double)mc.getWindow().getScaledHeight() / (double)mc.getWindow().getHeight();
		for(Notification n : notifications) {
			yAdd += n.render(e.matrices, mx, my, yAdd, e.tickDelta) + 10;
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
