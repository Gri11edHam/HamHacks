package net.grilledham.hamhacks.modules;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {
	
	private static final List<Module> modules = Lists.newArrayList();
	
	public static void register(Module m) {
		modules.add(m);
	}
	
	public static List<Module> getModules() {
		return modules;
	}
	
	public static List<Module> getModules(Module.Category category) {
		return modules.stream().filter(module -> module.category == category).collect(Collectors.toList());
	}
	
	public static Module getModule(String name) {
		return modules.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
}
