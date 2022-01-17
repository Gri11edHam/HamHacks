package net.grilledham.hamhacks.event;

public class Event {
	
	public void call() {
		EventManager.call(this);
	}
}
