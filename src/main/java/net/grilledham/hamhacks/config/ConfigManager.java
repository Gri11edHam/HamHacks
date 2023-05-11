package net.grilledham.hamhacks.config;

import net.grilledham.hamhacks.profile.Profile;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
	
	private static final List<Config> configs = new ArrayList<>();
	private static final List<Config> staticConfigs = new ArrayList<>();
	
	public static Profile profile;
	
	private static boolean initialized = false;
	
	private ConfigManager() {}
	
	public static void register(Config config) {
		configs.add(config);
	}
	
	public static void registerStatic(Config config) {
		staticConfigs.add(config);
	}
	
	public static void init() {
		staticConfigs.forEach(Config::init);
		configs.forEach(Config::init);
	}
	
	public static void setProfile(Profile profile) {
		ConfigManager.profile = profile;
		if(initialized) {
			configs.forEach(Config::save);
			configs.forEach((c) -> c.setProfile(profile));
		}
	}
	
	public static void initialLoad() {
		staticConfigs.forEach(Config::initializeConfig);
		configs.forEach((c) -> c.setProfile(profile)); // setting profile loads the config
		initialized = true;
	}
	
	public static void load() {
		staticConfigs.forEach(Config::load);
		configs.forEach(Config::load);
	}
	
	public static void save() {
		staticConfigs.forEach(Config::save);
		configs.forEach(Config::save);
	}
}
