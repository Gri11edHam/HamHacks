package net.grilledham.hamhacks.event.events;

import net.grilledham.hamhacks.event.Event;

public class EventKey extends Event {
	
	public final long handle;
	public final int key;
	public final int scancode;
	public final int action;
	public final int modifiers;
	
	public EventKey(long handle, int key, int scancode, int action, int modifiers) {
		this.handle = handle;
		this.key = key;
		this.scancode = scancode;
		this.action = action;
		this.modifiers = modifiers;
	}
}
