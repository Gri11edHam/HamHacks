package net.grilledham.hamhacks.event.events;

import net.grilledham.hamhacks.event.Event;
import net.minecraft.client.util.math.MatrixStack;

public class EventRender extends Event {
	
	public MatrixStack matrices;
	public float tickDelta;
	
	public EventRender(MatrixStack matrices, float tickDelta) {
		this.matrices = matrices;
		this.tickDelta = tickDelta;
	}
}
