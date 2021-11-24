package net.grilledham.hamhacks.event;

import net.grilledham.hamhacks.modules.ModuleManager;

public class Event {
	
	public void call() {
		ModuleManager.onEvent(this);
	}
}
