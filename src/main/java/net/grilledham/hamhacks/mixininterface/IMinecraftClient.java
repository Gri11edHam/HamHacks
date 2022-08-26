package net.grilledham.hamhacks.mixininterface;

public interface IMinecraftClient {
	public IClientPlayerInteractionManager getInteractionManager();
	
	IRenderTickCounter getRenderTickCounter();
}
