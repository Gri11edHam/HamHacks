package net.grilledham.hamhacks.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.grilledham.hamhacks.HamHacksClient;
import net.grilledham.hamhacks.config.impl.HamHacksConfigFixer;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.page.Page;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.profile.Profile;
import net.grilledham.hamhacks.setting.Setting;
import net.grilledham.hamhacks.setting.SettingCategory;

import java.io.*;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public abstract class Config {
	
	protected File file;
	
	protected final String modId;
	protected final String fileName;
	private final int version;
	private final ConfigFixer fixer;
	private final ConfigFixer internalFixer;
	private final boolean isStatic;
	
	private Profile profile;
	
	/**
	 * Creates a new config that saves and loads your modules
	 * @param file Path to the file in the .minecraft directory
	 */
	public Config(String modId, String file, int version, ConfigFixer fixer) {
		this(modId, file, version, fixer, false);
	}
	
	/**
	 * Creates a new config that saves and loads your modules
	 * @param file Path to the file in the .minecraft directory
	 * @param isStatic <code>true</code> if this config should be the same for every profile. <code>false</code> if this config should be loaded for each profile.
	 */
	public Config(String modId, String file, int version, ConfigFixer fixer, boolean isStatic) {
		this.modId = modId;
		this.fileName = file;
		this.version = version;
		this.fixer = fixer;
		this.isStatic = isStatic;
		internalFixer = new HamHacksConfigFixer();
	}
	
	protected void prepareConfigFile() {
		file = new File(FabricLoader.getInstance().getGameDir().toFile(), HamHacksClient.MOD_ID + (isStatic ? "" : ("/profiles/" + profile.name())) + "/" + modId + "/" + fileName);
	}
	
	public void initializeConfig() {
		prepareConfigFile();
		load();
	}
	
	public void setProfile(Profile profile) {
		this.profile = profile;
		initializeConfig();
	}
	
	public void load() {
		try {
			if (!file.getParentFile().exists() || !file.exists()) {
				save();
				load();
				firstTime();
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
			HamHacksClient.LOGGER.error("Could not load config file! (\"{}\")", file.getName(), ex);
		}
	}
	
	public void save() {
		try {
			if (!file.getParentFile().exists())
				file.getParentFile().mkdirs();
			if (!file.exists() &&
					!file.createNewFile())
				return;
			JsonObject object = new JsonObject();
			object.addProperty("config_version", HamHacksClient.CONFIG_VERSION);
			object.addProperty("custom_version", version);
			JsonObject modules = new JsonObject();
			for(Module m : ModuleManager.getModules(modId)) {
				JsonObject mod = new JsonObject();
				JsonObject modSettings = new JsonObject();
				for(SettingCategory c : m.getSettingCategories()) {
					for(Setting<?> s : c.getSettings()) {
						modSettings.add(s.getConfigName(), s.save());
					}
				}
				mod.add("settings", modSettings);
				modules.add(m.getConfigName(), mod);
			}
			object.add("modules", modules);
			JsonObject pages = new JsonObject();
			for(Page p : PageManager.getPages(modId)) {
				JsonObject page = new JsonObject();
				JsonObject pageSettings = new JsonObject();
				for(SettingCategory c : p.getSettingCategories()) {
					for(Setting<?> s : c.getSettings()) {
						pageSettings.add(s.getConfigName(), s.save());
					}
				}
				page.add("settings", pageSettings);
				pages.add(p.getConfigName(), page);
			}
			object.add("pages", pages);
			writeToFile(file, object);
		} catch (Exception ex) {
			HamHacksClient.LOGGER.error("Could not save config file! (\"{}\")", file.getName(), ex);
		}
	}
	
	protected void parseSettings(JsonObject obj) {
		int configVersion = obj.has("config_version") ? obj.get("config_version").getAsInt() : -1;
		int customVersion = obj.has("custom_version") ? obj.get("custom_version").getAsInt() : -1;
		
		internalFixer.fixConfig(obj, configVersion);
		fixer.fixConfig(obj, customVersion);
		
		if(obj.has("categories")) {
			JsonObject categories = obj.getAsJsonObject("categories");
			for(Category category : Category.values()) {
				try {
					JsonObject cObj = categories.getAsJsonObject(category.getTranslationKey().toLowerCase(Locale.ROOT));
					category.setPos(cObj.get("x").getAsFloat(), cObj.get("y").getAsFloat());
					category.expand(cObj.get("expanded").getAsBoolean());
				} catch(Exception e) {
					HamHacksClient.LOGGER.error("Parsing settings", e);
				}
			}
		}
		if(obj.has("modules")) {
			JsonObject modules = obj.getAsJsonObject("modules");
			for(Module m : ModuleManager.getModules(modId)) {
				try {
					JsonObject mod = modules.getAsJsonObject(m.getConfigName());
					if(mod == null) {
						continue;
					}
					JsonObject modSettings = mod.getAsJsonObject("settings");
					for(SettingCategory c : m.getSettingCategories()) {
						for(Setting<?> s : c.getSettings()) {
							if(modSettings.has(s.getConfigName())) {
								s.load(modSettings.get(s.getConfigName()));
							}
						}
					}
				} catch(Exception e) {
					HamHacksClient.LOGGER.error("Parsing settings", e);
				}
			}
		}
		if(obj.has("pages")) {
			JsonObject pages = obj.getAsJsonObject("pages");
			for(Page p : PageManager.getPages(modId)) {
				try {
					JsonObject page = pages.getAsJsonObject(p.getConfigName());
					if(page == null) {
						continue;
					}
					JsonObject pageSettings = page.getAsJsonObject("settings");
					for(SettingCategory c : p.getSettingCategories()) {
						for(Setting<?> s : c.getSettings()) {
							if(pageSettings.has(s.getConfigName())) {
								s.load(pageSettings.get(s.getConfigName()));
							}
						}
					}
				} catch(Exception e) {
					HamHacksClient.LOGGER.error("Parsing settings", e);
				}
			}
		}
	}
	
	public void writeToFile(File file, JsonObject object) {
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
			HamHacksClient.LOGGER.error("Parsing settings", e);
		}
	}
	
	/**
	 * Where you register your modules and pages
	 */
	public abstract void init();
	
	/**
	 * Override this method to do something when the config file is first created
	 */
	protected void firstTime() {}
}
