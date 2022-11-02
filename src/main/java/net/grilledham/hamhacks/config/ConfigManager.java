package net.grilledham.hamhacks.config;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
	
	private static final List<Config> configs = new ArrayList<>();
	
	private ConfigManager() {}
	
	public static void register(Config config) {
		configs.add(config);
	}
	
	public static void init() {
		configs.forEach(Config::init);
	}
	
	public static void initialLoad() {
		configs.forEach(Config::initializeConfig);
	}
	
	public static void load() {
		configs.forEach(Config::load);
	}
	
	public static void save() {
		configs.forEach(Config::save);
	}
}
