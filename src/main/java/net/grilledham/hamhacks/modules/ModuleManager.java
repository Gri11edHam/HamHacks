package net.grilledham.hamhacks.modules;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ModuleManager {
	
	private static final List<Module> modules = Lists.newArrayList();
	
	public static void register(Module m) {
		modules.add(m);
	}
	
	public static List<Module> getModules() {
		return modules;
	}
	
	public static List<Module> getModules(Module.Category category) {
		return modules.stream().filter(module -> module.category == category).toList();
	}
	
	public static Module getModule(String name) {
		return modules.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	public static <T extends Module> T getModule(Class<T> clazz) {
		T m = clazz.cast(modules.stream().filter(module -> module.getClass() == clazz).findFirst().orElse(null));
		if(m == null) {
			throw new IllegalArgumentException("Module of class " + clazz.getName() + " is not registered:\n" + Arrays.toString(modules.toArray()));
		}
		return m;
	}
	
	public static void updateKeybinds() {
		modules.forEach(Module::checkKeybind);
	}
	
	public static void updateEnabled() {
		modules.forEach(Module::updateEnabled);
	}
	
	public static void sortModules(Comparator<Module> comparator) {
		modules.sort(comparator);
	}
}
