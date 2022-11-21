package net.grilledham.hamhacks.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.gui.element.impl.SettingContainerElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingContainer<K, V> extends Setting<Map<K, Setting<V>>> {
	
	public SettingContainer(String name, ShouldShow shouldShow) {
		super(name, new HashMap<>(), shouldShow);
	}
	
	public V get(K key) {
		return value.get(key).get();
	}
	
	public List<Setting<V>> getSettings() {
		return value.values().stream().toList();
	}
	
	@Override
	public Map<K, Setting<V>> get() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void set(Map<K, Setting<V>> value) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void reset() {
		for(Setting<V> s : value.values()) {
			s.reset();
		}
	}
	
	@Override
	public GuiElement getElement(float x, float y, double scale) {
		return new SettingContainerElement(x, y, scale, this);
	}
	
	@Override
	public JsonObject save() {
		JsonObject obj = new JsonObject();
		for(Setting<V> s : value.values()) {
			obj.add(s.getName(), s.save());
		}
		return obj;
	}
	
	@Override
	public void load(JsonElement e) {
		JsonObject obj = e.getAsJsonObject();
		for(Setting<V> s : value.values()) {
			if(obj.has(s.getConfigName())) {
				s.load(obj.get(s.getConfigName()));
			}
		}
	}
}
