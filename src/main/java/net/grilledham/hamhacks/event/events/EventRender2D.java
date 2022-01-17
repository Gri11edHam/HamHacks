package net.grilledham.hamhacks.event.events;

import net.grilledham.hamhacks.event.Event;
import net.minecraft.client.util.math.MatrixStack;

public class EventRender2D extends Event {
	
	public MatrixStack matrices;
	public float tickDelta;
	
	public EventRender2D(MatrixStack matrices, float tickDelta) {
		this.matrices = matrices;
		this.tickDelta = tickDelta;
	}
}
