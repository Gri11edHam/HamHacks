package net.grilledham.hamhacks.page;

import com.google.common.collect.Lists;

import java.util.*;

public class PageManager {
	
	private static final List<Page> pages = Lists.newArrayList();
	private static final Map<String, List<Page>> pageMap = new HashMap<>();
	
	public static void register(String modId, Page p) {
		pages.add(p);
		if(!pageMap.containsKey(modId)) {
			pageMap.put(modId, new ArrayList<>());
		}
		pageMap.get(modId).add(p);
	}
	
	public static List<Page> getPages() {
		return new ArrayList<>(pages);
	}
	
	public static List<Page> getPages(String modId) {
		if(pageMap.containsKey(modId)) {
			return new ArrayList<>(pageMap.get(modId));
		} else {
			return new ArrayList<>();
		}
	}
	
	public static <T extends Page> T getPage(Class<T> clazz) {
		T m = clazz.cast(pages.stream().filter(module -> module.getClass() == clazz).findFirst().orElse(null));
		if(m == null) {
			throw new IllegalArgumentException("Page of class " + clazz.getName() + " is not registered:\n" + Arrays.toString(pages.toArray()));
		}
		return m;
	}
}
