package net.grilledham.hamhacks.event;

import com.google.common.collect.Lists;
import net.grilledham.hamhacks.HamHacksClient;
import org.apache.commons.lang3.tuple.Triple;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class EventManager {
	
	private static final HashMap<Class<? extends Event>, HashMap<Object, ArrayList<Method>>> listeners = new HashMap<>();
	private static int calling = 0;
	private static final ArrayList<Triple<Object, Method, Class<? extends Event>>> toAdd = new ArrayList<>();
	private static final ArrayList<Triple<Object, Method, Class<? extends Event>>> toRemove = new ArrayList<>();
	
	private EventManager() {}
	
	public static void register(Object o) {
		for(Method m : getEventMethods(o)) {
			register(o, m, m.getParameters()[0].getType().asSubclass(Event.class));
		}
	}
	
	public static void register(Object o, Method m, Class<? extends Event> c) {
		if(calling > 0) {
			toAdd.add(Triple.of(o, m, c));
			return;
		}
		if(listeners.containsKey(c)) {
			if(listeners.get(c).containsKey(o)) {
				listeners.get(c).get(o).add(m);
			} else {
				listeners.get(c).put(o, Lists.newArrayList(m));
			}
		} else {
			HashMap<Object, ArrayList<Method>> map = new HashMap<>();
			map.put(o, Lists.newArrayList(m));
			listeners.put(c, map);
		}
	}
	
	public static void unRegister(Object o) {
		for(Method m : getEventMethods(o)) {
			unRegister(o, m, m.getParameters()[0].getType().asSubclass(Event.class));
		}
	}
	
	public static void unRegister(Object o, Method m, Class<? extends Event> c) {
		if(calling > 0) {
			toRemove.add(Triple.of(o, m, c));
			return;
		}
		if(listeners.containsKey(c)) {
			if(listeners.get(c).containsKey(o)) {
				listeners.get(c).get(o).remove(m);
			}
		}
	}
	
	public static void call(Event e) {
		if(listeners.containsKey(e.getClass())) {
			final HashMap<Object, ArrayList<Method>> l = EventManager.listeners.get(e.getClass());
			if(!l.isEmpty()) {
				calling++;
				for(Object o : l.keySet()) {
					for(Method m : l.get(o)) {
						try {
//							System.out.println("Calling " + o.getClass().getSimpleName() + "." + m.getName() + "(" + e.getClass().getSimpleName() + ")");
							m.invoke(o, e);
						} catch(IllegalAccessException | InvocationTargetException ex) {
							HamHacksClient.LOGGER.error("Calling event", ex);
						}
					}
				}
				calling--;
				if(calling < 1) {
					updateListeners();
				}
			}
		}
	}
	
	private static void updateListeners() {
		toAdd.forEach(t -> register(t.getLeft(), t.getMiddle(), t.getRight()));
		toRemove.forEach(t -> unRegister(t.getLeft(), t.getMiddle(), t.getRight()));
		toAdd.clear();
		toRemove.clear();
	}
	
	private static ArrayList<Method> getEventMethods(Object o) {
		ArrayList<Method> methods = new ArrayList<>();
		for(Method m : o.getClass().getMethods()) {
			if(m.isAnnotationPresent(EventListener.class)) {
				methods.add(m);
			}
		}
		return methods;
	}
}
