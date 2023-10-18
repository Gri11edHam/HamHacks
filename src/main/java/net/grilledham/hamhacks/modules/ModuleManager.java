package net.grilledham.hamhacks.modules;

import java.util.*;
import java.util.stream.Collectors;

public class ModuleManager {
	
	private static final Map<Class<? extends Module>, Module> modules = new HashMap<>();
	private static final Map<String, Map<Class<? extends Module>, Module>> moduleMap = new HashMap<>();
	
	private static List<Module> sortedModules = null;
	
	public static void register(String modId, Module m) {
		modules.put(m.getClass(), m);
		if(!moduleMap.containsKey(modId)) {
			moduleMap.put(modId, new HashMap<>());
		}
		moduleMap.get(modId).put(m.getClass(), m);
	}
	
	public static void unregister(String modId, Module m) {
		modules.remove(m.getClass());
		moduleMap.get(modId).remove(m.getClass());
	}
	
	public static List<Module> getModules() {
		if(sortedModules == null) sortModules(Comparator.comparing(Module::getName));
		return sortedModules;
	}
	
	public static List<Module> getModules(Category category) {
		if(sortedModules == null) sortModules(Comparator.comparing(Module::getName));
		return sortedModules.stream().filter(module -> module.category == category).toList();
	}
	
	public static List<Module> getModules(String modId) {
		if(moduleMap.containsKey(modId)) {
			return moduleMap.get(modId).values().stream().toList();
		} else {
			return new ArrayList<>();
		}
	}
	
	public static Module getModule(String name) {
		return modules.values().stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	public static synchronized <T extends Module> T getModule(Class<T> clazz) {
		return clazz.cast(modules.getOrDefault(clazz, null));
	}
	
	public static void updateKeybinds() {
		modules.forEach((c, m) -> m.checkKeybind());
	}
	
	public static synchronized void sortModules(Comparator<Module> comparator) {
		sortedModules = modules.values().stream().sorted(comparator).collect(Collectors.toList());
	}
}
