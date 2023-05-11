package net.grilledham.hamhacks.config.impl;

import com.google.gson.JsonObject;
import net.grilledham.hamhacks.HamHacksClient;
import net.grilledham.hamhacks.config.Config;
import net.grilledham.hamhacks.config.ConfigFixer;
import net.grilledham.hamhacks.modules.Category;

import java.util.Locale;

public class CategoryConfig extends Config {
	
	public CategoryConfig() {
		super(HamHacksClient.MOD_ID, "../categories.json", -1, ConfigFixer.DEFAULT, true);
	}
	
	@Override
	public void init() {}
	
	public void save() {
		try {
			if (!file.getParentFile().exists())
				file.getParentFile().mkdirs();
			if (!file.exists() &&
					!file.createNewFile())
				return;
			JsonObject object = new JsonObject();
			object.addProperty("config_version", HamHacksClient.CONFIG_VERSION);
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
			System.out.printf("Could not save config file! (\"%s\")%n", file.getName());
			ex.printStackTrace();
		}
	}
}
