package net.grilledham.hamhacks.event.events;

import net.grilledham.hamhacks.event.EventCancelable;

public class EventLookDirection extends EventCancelable {
	
	public double dx;
	public double dy;
	
	public EventLookDirection(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
	}
}
