package net.grilledham.hamhacks.setting;

import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockTypeSelector implements SettingContainer<Boolean> {
	
	private final List<String> defaults = new ArrayList<>();
	private final Map<String, Boolean> selectedBlocks = new HashMap<>();
	private final Runnable onChange;
	
	public BlockTypeSelector(Runnable onChange, Block... defaultBlocks) {
		this.onChange = onChange;
		for(Block block : defaultBlocks) {
			defaults.add(block.getTranslationKey());
		}
		for(Field field : Blocks.class.getFields()) {
			try {
				String key = ((Block)field.get(Blocks.class)).getTranslationKey();
				selectedBlocks.put(key, defaults.contains(key));
			} catch(IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public boolean isSelected(Block block) {
		return selectedBlocks.get(block.getTranslationKey());
	}
	
	@Override
	public List<String> getKeys() {
		return selectedBlocks.keySet().stream().toList();
	}
	
	@Override
	public void setValue(String key, Boolean value) {
		selectedBlocks.put(key, value);
		onChange.run();
	}
	
	@Override
	public Boolean getValue(String key) {
		return selectedBlocks.getOrDefault(key, false);
	}
	
	@Override
	public void addSaveData(JsonObject saveData) {
		JsonObject blockData = new JsonObject();
		selectedBlocks.forEach(blockData::addProperty);
		saveData.add(this.getClass().getAnnotation(SettingPage.class).name(), blockData);
	}
	
	@Override
	public void parseSaveData(JsonObject saveData) {
		JsonObject blockData = saveData.getAsJsonObject(this.getClass().getAnnotation(SettingPage.class).name());
		selectedBlocks.forEach((key, value) -> selectedBlocks.put(key, blockData.get(key).getAsBoolean()));
	}
	
	@Override
	public void reset() {
		for(Field field : Blocks.class.getFields()) {
			try {
				String key = ((Block)field.get(Blocks.class)).getTranslationKey();
				selectedBlocks.put(key, defaults.contains(key));
			} catch(IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
