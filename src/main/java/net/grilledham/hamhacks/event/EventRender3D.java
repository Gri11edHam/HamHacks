package net.grilledham.hamhacks.event;

import net.minecraft.client.util.math.MatrixStack;

public class EventRender3D extends Event {
	
	public float tickDelta;
	public long limitTime;
	public MatrixStack matrices;
	
	public EventRender3D(float tickDelta, long limitTime, MatrixStack matrices) {
		this.tickDelta = tickDelta;
		this.limitTime = limitTime;
		this.matrices = matrices;
	}
}
