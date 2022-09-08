package net.grilledham.hamhacks.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import net.fabricmc.loader.api.FabricLoader;
import net.grilledham.hamhacks.HamHacksClient;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.util.setting.SettingHelper;

import java.io.*;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class HamHacksConfig {
	
	@Expose(serialize = false)
	private static File file;
	
	private static void prepareConfigFile() {
		if (file != null) {
			return;
		}
		file = new File(FabricLoader.getInstance().getGameDir().toFile(), HamHacksClient.MOD_ID + "/config.json");
	}
	
	public static void initializeConfig() {
		load();
	}
	
	public static void load() {
		prepareConfigFile();
		try {
			if (!file.getParentFile().exists() || !file.exists()) {
				HamHacksClient.firstTime = true;
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
				parseSettings(JsonParser.parseString(builder.trim()).getAsJsonObject());
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
			object.addProperty("config_version", HamHacksClient.CONFIG_VERSION);
			JsonObject categories = new JsonObject();
			for(Module.Category category : Module.Category.values()) {
				JsonObject cObj = new JsonObject();
				cObj.addProperty("x", category.getX());
				cObj.addProperty("y", category.getY());
				cObj.addProperty("expanded", category.isExpanded());
				categories.add(category.name().toLowerCase(Locale.ROOT), cObj);
			}
			object.add("categories", categories);
			JsonObject modules = new JsonObject();
			for(Module m : ModuleManager.getModules()) {
				JsonObject mod = new JsonObject();
				JsonObject modSettings = new JsonObject();
				SettingHelper.addSaveData(m, modSettings);
				mod.add("settings", modSettings);
				modules.add(m.getConfigName(), mod);
			}
			object.add("modules", modules);
			writeToFile(file, object);
		} catch (Exception ex) {
			System.out.printf("Could not save config file! (\"%s\")%n", file.getName());
			ex.printStackTrace();
		}
	}
	
	private static void parseSettings(JsonObject obj) {
		int configVersion = obj.has("config_version") ? obj.get("config_version").getAsInt() : -1;
		if(configVersion == -1) {
			HamHacksClient.LOGGER.warn("Warning: The config was saved in an older version. Unfortunately, your settings will be lost");
		} else if(configVersion < HamHacksClient.CONFIG_VERSION) {
			HamHacksClient.LOGGER.warn("Warning: The config was saved in an older version. Some or all of your settings will be lost.");
			HamHacksClient.LOGGER.warn(configVersion + " < " + HamHacksClient.CONFIG_VERSION);
		} else if(configVersion > HamHacksClient.CONFIG_VERSION) {
			HamHacksClient.LOGGER.warn("Warning: The config was saved in a newer version. Some or all of your settings will be lost.");
			HamHacksClient.LOGGER.warn(configVersion + " > " + HamHacksClient.CONFIG_VERSION);
		} else {
			HamHacksClient.LOGGER.info("Loading config. Version: " + configVersion);
		}
		
		ConfigFixer.fixConfig(obj, configVersion);
		
		JsonObject categories = obj.getAsJsonObject("categories");
		for(Module.Category category : Module.Category.values()) {
			JsonObject cObj = categories.getAsJsonObject(category.name().toLowerCase(Locale.ROOT));
			category.setPos(cObj.get("x").getAsFloat(), cObj.get("y").getAsFloat());
			category.expand(cObj.get("expanded").getAsBoolean());
		}
		JsonObject modules = obj.getAsJsonObject("modules");
		for(Module m : ModuleManager.getModules()) {
			try {
				JsonObject mod = modules.getAsJsonObject(m.getName());
				if(mod == null) {
					continue;
				}
				JsonObject modSettings = mod.getAsJsonObject("settings");
				SettingHelper.parseSaveData(m, modSettings);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void writeToFile(File file, JsonObject object) {
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
}
