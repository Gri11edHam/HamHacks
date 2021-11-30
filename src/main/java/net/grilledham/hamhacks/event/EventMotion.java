package net.grilledham.hamhacks.event;

import net.grilledham.hamhacks.util.RotationHack;

public class EventMotion extends EventCancelable {
	
	public Type type;
	
	public EventMotion(Type type) {
		this.type = type;
		RotationHack.motionEvent(this);
	}
	
	public enum Type {
		PRE,
		POST
	}
}
