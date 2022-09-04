package net.grilledham.hamhacks.util;

import com.google.common.collect.Lists;

import java.util.List;

public class StringList {
	
	private final List<String> list;
	
	private final List<String> defaults;
	
	public StringList(String... args) {
		list = Lists.newArrayList(args);
		defaults = List.of(args);
	}
	
	public void add(String s) {
		list.add(s);
	}
	
	public void remove(int i) {
		list.remove(i);
	}
	
	public void remove(String s) {
		list.remove(s);
	}
	
	public void swap(int a, int b) {
		String temp = list.get(a);
		list.set(a, list.get(b));
		list.set(b, temp);
	}
	
	public void moveUp(int i) {
		if(i > 0) {
			swap(i, i - 1);
		}
	}
	
	public void moveDown(int i) {
		if(i < list.size()) {
			swap(i, i + 1);
		}
	}
	
	public void reset() {
		list.clear();
		list.addAll(defaults);
	}
	
	public void clear() {
		list.clear();
	}
	
	public List<String> getList() {
		return list;
	}
}
