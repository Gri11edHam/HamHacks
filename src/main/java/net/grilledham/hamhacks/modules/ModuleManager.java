package net.grilledham.hamhacks.modules;

import com.google.common.collect.Lists;
import net.grilledham.hamhacks.HamHacksClient;

import java.util.*;

public class ModuleManager {
	
	private static final List<Module> modules = Lists.newArrayList();
	private static final Map<String, List<Module>> moduleMap = new HashMap<>();
	
	public static void register(String modId, Module m) {
		modules.add(m);
		if(!moduleMap.containsKey(modId)) {
			moduleMap.put(modId, new ArrayList<>());
		}
		moduleMap.get(modId).add(m);
	}
	
	public static void unregister(String modId, Module m) {
		modules.remove(m);
		moduleMap.get(modId).remove(m);
	}
	
	public static List<Module> getModules() {
		return modules;
	}
	
	public static List<Module> getModules(Category category) {
		return modules.stream().filter(module -> module.category == category).toList();
	}
	
	public static List<Module> getModules(String modId) {
		if(moduleMap.containsKey(modId)) {
			return new ArrayList<>(moduleMap.get(modId));
		} else {
			return new ArrayList<>();
		}
	}
	
	public static Module getModule(String name) {
		return modules.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	public static <T extends Module> T getModule(Class<T> clazz) {
		try {
			return clazz.cast(modules.stream().filter(module -> module.getClass() == clazz).findFirst().orElse(null));
		} catch(ConcurrentModificationException e) {
			HamHacksClient.LOGGER.error("Getting module from class", e);
			return null;
		}
	}
	
	public static void updateKeybinds() {
		modules.forEach(Module::checkKeybind);
	}
	
	public static void sortModules(Comparator<Module> comparator) {
		modules.sort(comparator);
	}
}
