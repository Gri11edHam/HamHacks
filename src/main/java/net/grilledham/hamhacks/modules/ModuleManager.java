package net.grilledham.hamhacks.modules;

import com.google.common.collect.Lists;
import net.grilledham.hamhacks.event.Event;
import net.grilledham.hamhacks.event.EventKey;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {
	
	private static final List<Module> modules = Lists.newArrayList();
	
	public static void register(Module m) {
		modules.add(m);
	}
	
	public static void onEvent(Event e) {
		for(Module m : modules) {
			m.onEvent(e);
		}
	}
	
	public static List<Module> getModules() {
		return modules;
	}
	
	public static List<Module> getModules(Module.Category category) {
		return modules.stream().filter(module -> module.category == category).collect(Collectors.toList());
	}
}
