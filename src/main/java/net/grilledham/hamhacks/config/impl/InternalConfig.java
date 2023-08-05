package net.grilledham.hamhacks.config.impl;

import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.grilledham.hamhacks.HamHacksClient;
import net.grilledham.hamhacks.config.Config;
import net.grilledham.hamhacks.config.ConfigFixer;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.util.Version;

import java.io.File;
import java.util.Locale;

public class InternalConfig extends Config {
	
	public InternalConfig() {
		super(HamHacksClient.MOD_ID, "../config.json", -1, ConfigFixer.DEFAULT, true);
	}
	
	@Override
	public void init() {}
	
	@Override
	protected void prepareConfigFile() {
		super.prepareConfigFile();
		if(!file.exists()) {
			file = new File(FabricLoader.getInstance().getGameDir().toFile(), HamHacksClient.MOD_ID + "/categories.json");
			load();
			super.prepareConfigFile();
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
			object.addProperty("seen_version", HamHacksClient.seenVersion.getVersion(0, true));
			object.addProperty("last_launched", HamHacksClient.VERSION.getVersion(0, true));
			JsonObject categories = new JsonObject();
			for(Category category : Category.values()) {
				JsonObject cObj = new JsonObject();
				cObj.addProperty("x", category.getX());
				cObj.addProperty("y", category.getY());
				cObj.addProperty("expanded", category.isExpanded());
				categories.add(category.getTranslationKey().toLowerCase(Locale.ROOT), cObj);
			}
			object.add("categories", categories);
			writeToFile(file, object);
		} catch (Exception ex) {
			HamHacksClient.LOGGER.error("Could not save config file! (\"{}\")", file.getName(), ex);
		}
	}
	
	@Override
	protected void parseSettings(JsonObject obj) {
		super.parseSettings(obj);
		if(obj.has("seen_version")) {
			HamHacksClient.seenVersion = new Version(obj.get("seen_version").getAsString());
		}
		if(obj.has("last_launched")) {
			if(HamHacksClient.VERSION.isNewerThan(obj.get("last_launched").getAsString())) {
				HamHacksClient.updated = true;
			}
		} else {
			HamHacksClient.updated = true;
		}
	}
}
