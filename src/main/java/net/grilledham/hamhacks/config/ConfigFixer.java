package net.grilledham.hamhacks.config;

import com.google.gson.JsonObject;

public interface ConfigFixer {
	
	ConfigFixer DEFAULT = (obj, configVersion) -> {};
	
	/**
	 * Allows you to modify the json object before it is used
	 * @param obj The json object that was read from the save file
	 * @param configVersion The version that the config was last saved in
	 */
	void fixConfig(JsonObject obj, int configVersion);
}
