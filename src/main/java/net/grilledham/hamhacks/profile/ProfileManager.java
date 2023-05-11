package net.grilledham.hamhacks.profile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.grilledham.hamhacks.HamHacksClient;
import net.grilledham.hamhacks.config.Config;
import net.grilledham.hamhacks.config.ConfigFixer;
import net.grilledham.hamhacks.config.ConfigManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileManager extends Config {
	
	private static final List<Profile> profiles = new ArrayList<>();
	
	private static Profile selectedProfile;
	
	public ProfileManager() {
		super(HamHacksClient.MOD_ID, "../profiles.json", -1, ConfigFixer.DEFAULT, true);
	}
	
	public void init() {
	}
	
	public static void addProfile(String name) {
		profiles.add(new Profile(name));
	}
	
	public static void selectProfile(int i) {
		selectProfile(profiles.get(i));
	}
	
	public static void selectProfile(String name) {
		selectProfile(getProfile(name));
	}
	
	public static void selectProfile(Profile profile) {
		ConfigManager.setProfile(profile);
		selectedProfile = profile;
	}
	
	public static void removeProfile(String name) {
		removeProfile(getProfile(name));
	}
	
	public static void removeProfile(Profile profile) {
		if(profile == null) return;
		profiles.remove(profile);
		if(selectedProfile == profile) {
			if(profiles.isEmpty()) {
				addProfile("Default");
				selectProfile("Default");
			}
			selectProfile(profiles.get(0));
		}
	}
	
	public static Profile getProfile(String name) {
		for(Profile profile : profiles) {
			if(profile.name().equals(name)) {
				return profile;
			}
		}
		return null;
	}
	
	public static List<Profile> getProfiles() {
		return Collections.unmodifiableList(profiles);
	}
	
	public void save() {
		try {
			if(!file.getParentFile().exists())
				file.getParentFile().mkdirs();
			if(!file.exists() &&
					!file.createNewFile())
				return;
			JsonObject object = new JsonObject();
			JsonArray profiles = new JsonArray();
			if(selectedProfile == null)
				return;
			for(Profile profile : ProfileManager.profiles) {
				profiles.add(profile.name());
			}
			object.addProperty("config_version", HamHacksClient.CONFIG_VERSION);
			object.addProperty("selected", selectedProfile.name());
			object.add("profiles", profiles);
			writeToFile(file, object);
		} catch(Exception ex) {
			System.out.printf("Could not save config file! (\"%s\")%n", file.getName());
			ex.printStackTrace();
		}
	}
	
	protected void parseSettings(JsonObject obj) {
		if(obj.has("profiles")) {
			JsonArray profiles = obj.getAsJsonArray("profiles");
			for(JsonElement e : profiles) {
				String profile = e.getAsString();
				ProfileManager.profiles.add(new Profile(profile));
			}
		}
		if(obj.has("selected")) {
			selectProfile(obj.get("selected").getAsString());
		}
		if(profiles.isEmpty() || selectedProfile == null) {
			addProfile("Default");
			selectProfile("Default");
		}
	}
	
	public static Profile getSelectedProfile() {
		return selectedProfile;
	}
	
	public static int indexOfSelected() {
		return profiles.indexOf(selectedProfile);
	}
}
