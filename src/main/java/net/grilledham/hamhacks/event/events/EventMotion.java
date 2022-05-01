package net.grilledham.hamhacks.event.events;

import net.grilledham.hamhacks.event.EventCancelable;

public class EventMotion extends EventCancelable {
	
	public Type type;
	
	public EventMotion(Type type) {
		this.type = type;
	}
	
	public enum Type {
		PRE,
		POST
	}
}
