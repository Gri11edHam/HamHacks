package net.grilledham.hamhacks.event;

import com.google.common.collect.Lists;
import net.grilledham.hamhacks.HamHacksClient;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class EventManager {
	
	private static final HashMap<Class<? extends Event>, HashMap<Object, ArrayList<Method>>> listeners = new HashMap<>();
	
	private EventManager() {}
	
	public static void register(Object o) {
		for(Method m : getEventMethods(o)) {
			register(o, m, m.getParameters()[0].getType().asSubclass(Event.class));
		}
	}
	
	public static void register(Object o, Method m, Class<? extends Event> c) {
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
		if(listeners.containsKey(c)) {
			if(listeners.get(c).containsKey(o)) {
				listeners.get(c).get(o).remove(m);
			}
		}
	}
	
	public static void call(Event e) {
		if(listeners.containsKey(e.getClass())) {
			if(!listeners.get(e.getClass()).isEmpty()) {
				for(Object o : listeners.get(e.getClass()).keySet()) {
					for(Method m : listeners.get(e.getClass()).get(o)) {
						try {
//							System.out.println("Calling " + o.getClass().getSimpleName() + "." + m.getName() + "(" + e.getClass().getSimpleName() + ")");
							m.invoke(o, e);
						} catch(IllegalAccessException | InvocationTargetException ex) {
							HamHacksClient.LOGGER.error("Calling event", ex);
						}
					}
				}
			}
		}
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
