package net.grilledham.hamhacks.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import net.fabricmc.loader.api.FabricLoader;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.util.setting.Setting;

import java.io.*;
import java.util.ConcurrentModificationException;
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
		file = new File(FabricLoader.getInstance().getConfigDir().toFile(), HamHacksClient.MOD_ID + ".json");
	}
	
	public static void initializeConfig() {
		load();
	}
	
	public static void load() {
		prepareConfigFile();
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
				parseSettings(new JsonParser().parse(builder.trim()).getAsJsonObject());
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
				addSettings(modSettings, m.getSettings());
				mod.add("settings", modSettings);
				modules.add(m.getName(), mod);
			}
			object.add("modules", modules);
			writeToFile(file, object);
		} catch (Exception ex) {
			System.out.printf("Could not save config file! (\"%s\")%n", file.getName());
			ex.printStackTrace();
		}
	}
	
	private static void addSettings(JsonObject obj, List<Setting> settings) {
		for(Setting s : settings) {
			if(s != null) {
				addSetting(obj, s);
			}
		}
	}
	
	private static void addSettings(JsonArray obj, List<Setting> settings) {
		for(Setting s : settings) {
			if(s != null) {
				JsonObject setting = new JsonObject();
				setting.addProperty("name", s.getName());
				addSetting(setting, s);
				obj.add(setting);
			}
		}
	}
	
	private static void addSetting(JsonObject obj, Setting s) {
		obj.add(s.getName(), s.getAsJsonObject().get(s.getName()));
	}
	
	private static void parseSettings(JsonObject obj) {
		JsonObject categories = obj.getAsJsonObject("categories");
		for(Module.Category category : Module.Category.values()) {
			JsonObject cObj = categories.getAsJsonObject(category.name().toLowerCase(Locale.ROOT));
			category.setPos(cObj.get("x").getAsInt(), cObj.get("y").getAsInt());
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
				parseSettings(modSettings, m.getSettings());
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void parseSettings(JsonObject obj, List<Setting> settings) {
		try {
			for(Setting s : settings) {
				try {
					parseSetting(obj, s);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		} catch(ConcurrentModificationException e) {
			parseSettings(obj, settings);
		}
	}
	
	private static void parseSetting(JsonObject obj, Setting s) {
		if(obj.has(s.getName())) {
			try {
				s.set(obj.get(s.getName()));
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
	
	public static <T> T readFromJson(File file, Class<T> c) {
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			
			StringBuilder builder = new StringBuilder();
			
			String line;
			while((line = bufferedReader.readLine()) != null) {
				builder.append(line);
			}
			bufferedReader.close();
			inputStreamReader.close();
			fileInputStream.close();
			
			return HamHacksClient.GSON.fromJson(builder.toString(), c);
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
