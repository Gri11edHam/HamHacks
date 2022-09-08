package net.grilledham.hamhacks.modules.render;

import net.grilledham.hamhacks.event.EventListener;
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
	
	private static Notifications INSTANCE;
	
	private static final List<Notification> notifications = new ArrayList<>();
	
	@ColorSetting(name = "hamhacks.module.notifications.backgroundColor")
	public Color bgColor = new Color(0x80000000);
	
	@ColorSetting(name = "hamhacks.module.notifications.accentColor")
	public Color accentColor = new Color(0x80AA0000);
	
	@ColorSetting(name = "hamhacks.module.notifications.progressColorBackground")
	public Color progressColorBG = new Color(0x80000000);
	
	@ColorSetting(name = "hamhacks.module.notifications.progressColor")
	public Color progressColor = new Color(0x80AA0000);
	
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
		INSTANCE = this;
		enabled = true;
		showModule = false;
	}
	
	public static Notifications getInstance() {
		return INSTANCE;
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
		for(Notification n : notifications) {
			yAdd += n.render(e.matrices, yAdd, e.tickDelta) + 10;
			if(n.isComplete()) {
				completed.add(n);
			}
		}
		notifications.removeAll(completed);
	}
	
	public static void notify(Notification n) {
		notifications.add(n);
	}
	
	public static void notify(String title, String info) {
		notify(new Notification(title, info));
	}
	
	public static void notify(String info) {
		notify("HamHacks", info);
	}
}
