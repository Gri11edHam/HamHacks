package net.grilledham.hamhacks.event.events;

import net.grilledham.hamhacks.event.EventCancelable;

public class EventScroll extends EventCancelable {
	
	public double vertical;
	public double horizontal;
	
	public EventScroll(double vertical, double horizontal) {
		this.vertical = vertical;
		this.horizontal = horizontal;
	}
}
