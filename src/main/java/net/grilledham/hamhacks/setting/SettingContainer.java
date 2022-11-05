package net.grilledham.hamhacks.setting;

import com.google.gson.JsonObject;

import java.util.List;

public interface SettingContainer<T> {
	
	List<String> getKeys();
	
	void setValue(String key, T value);
	
	T getValue(String key);
	
	void addSaveData(JsonObject saveData);
	
	void parseSaveData(JsonObject saveData);
	
	void reset();
}
