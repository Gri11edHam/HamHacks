package net.grilledham.hamhacks.event.events;

import net.grilledham.hamhacks.event.Event;
import net.minecraft.client.util.math.MatrixStack;

public class EventRender3D extends Event {
	
	public float tickDelta;
	public MatrixStack matrices;
	
	public EventRender3D(float tickDelta, MatrixStack matrices) {
		this.tickDelta = tickDelta;
		this.matrices = matrices;
	}
}
