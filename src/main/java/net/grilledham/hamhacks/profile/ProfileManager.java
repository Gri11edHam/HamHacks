package net.grilledham.hamhacks.profile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.grilledham.hamhacks.HamHacksClient;
import net.grilledham.hamhacks.config.ConfigManager;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ProfileManager {
	
	private static File file;
	
	private static final List<Profile> profiles = new ArrayList<>();
	
	private static Profile selectedProfile;
	
	public static void init() {
		if (file != null) {
			return;
		}
		file = new File(FabricLoader.getInstance().getGameDir().toFile(), HamHacksClient.MOD_ID + "/profiles.json");
		load();
		if(profiles.isEmpty()) {
			addProfile("Default");
			selectProfile("Default");
		}
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
		ConfigManager.save();
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
	
	public static void load() {
		try {
			if (!file.getParentFile().exists() || !file.exists()) {
				save();
				load();
				return;
			}
			BufferedReader f = new BufferedReader(new FileReader(file));
			List<String> options = (List<String>)f.lines().collect((Collector)Collectors.toList());
			if (options.isEmpty())
				return;
			String builder = String.join("", options);
			if (builder.trim().length() > 0)
				parseProfiles(JsonParser.parseString(builder.trim()).getAsJsonObject());
			f.close();
		} catch (Exception ex) {
			System.out.printf("Could not load config file! (\"%s\")%n", file.getName());
			ex.printStackTrace();
		}
	}
	
	public static void save() {
		try {
			if (!file.getParentFile().exists())
				file.getParentFile().mkdirs();
			if (!file.exists() &&
					!file.createNewFile())
				return;
			JsonObject object = new JsonObject();
			JsonArray profiles = new JsonArray();
			if(selectedProfile == null)
				return;
			for(Profile profile : ProfileManager.profiles) {
				profiles.add(profile.name());
			}
			object.addProperty("selected", selectedProfile.name());
			object.add("profiles", profiles);
			writeToFile(file, object);
		} catch (Exception ex) {
			System.out.printf("Could not save config file! (\"%s\")%n", file.getName());
			ex.printStackTrace();
		}
	}
	
	private static void parseProfiles(JsonObject obj) {
		JsonArray profiles = obj.getAsJsonArray("profiles");
		for(JsonElement e : profiles) {
			String profile = e.getAsString();
			ProfileManager.profiles.add(new Profile(profile));
		}
		selectProfile(obj.get("selected").getAsString());
	}
	
	private static void writeToFile(File file, JsonObject object) {
		if (file == null || (file.exists() && file.isDirectory()))
			return;
		try {
			if (!file.exists()) {
				File parent = file.getParentFile();
				if (parent != null && !parent.exists())
					parent.mkdirs();
				file.createNewFile();
			}
			FileWriter writer = new FileWriter(file);
			BufferedWriter bufferedWriter = new BufferedWriter(writer);
			bufferedWriter.write(HamHacksClient.GSON.toJson(object));
			bufferedWriter.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Profile getSelectedProfile() {
		return selectedProfile;
	}
	
	public static int indexOfSelected() {
		return profiles.indexOf(selectedProfile);
	}
}
