package net.grilledham.hamhacks.util;

import java.util.List;

public class SelectableList {
	
	private String value;
	
	private final String defaultValue;
	
	private final List<String> possibilities;
	
	public SelectableList(String defaultValue, String... possibilities) {
		this.defaultValue = defaultValue;
		this.value = defaultValue;
		this.possibilities = List.of(possibilities);
	}
	
	public void set(String value) {
		if(possibilities.contains(value)) {
			this.value = value;
		}
	}
	
	public void reset() {
		value = defaultValue;
	}
	
	public String get() {
		return value;
	}
	
	public String getDefault() {
		return defaultValue;
	}
	
	public List<String> getPossibilities() {
		return possibilities;
	}
}
