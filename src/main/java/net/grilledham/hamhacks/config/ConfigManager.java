package net.grilledham.hamhacks.config;

import net.grilledham.hamhacks.profile.Profile;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
	
	private static final List<Config> configs = new ArrayList<>();
	
	public static Profile profile;
	
	private static boolean initialized = false;
	
	private ConfigManager() {}
	
	public static void register(Config config) {
		configs.add(config);
	}
	
	public static void init() {
		configs.forEach(Config::init);
		initialized = true;
	}
	
	public static void setProfile(Profile profile) {
		ConfigManager.profile = profile;
		if(initialized)
			configs.forEach((c) -> c.setProfile(profile));
	}
	
	public static void initialLoad() {
		configs.forEach((c) -> c.setProfile(profile)); // setting profile loads the config
	}
	
	public static void load() {
		configs.forEach(Config::load);
	}
	
	public static void save() {
		configs.forEach(Config::save);
	}
}
