package net.grilledham.hamhacks.event.events;

import net.grilledham.hamhacks.event.EventCancelable;

public class EventClick extends EventCancelable {
	
	public double x;
	public double y;
	public int button;
	
	public EventClick(double x, double y, int button) {
		this.x = x;
		this.y = y;
		this.button = button;
	}
}
