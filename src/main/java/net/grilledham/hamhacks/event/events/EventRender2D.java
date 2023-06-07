package net.grilledham.hamhacks.event.events;

import net.grilledham.hamhacks.event.Event;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class EventRender2D extends Event {
	
	public DrawContext context;
	public MatrixStack matrices;
	public float tickDelta;
	
	public EventRender2D(DrawContext context, float tickDelta) {
		this.context = context;
		this.matrices = context.getMatrices();
		this.tickDelta = tickDelta;
	}
}
