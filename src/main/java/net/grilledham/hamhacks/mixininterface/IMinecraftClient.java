package net.grilledham.hamhacks.mixininterface;

public interface IMinecraftClient {
	IClientPlayerInteractionManager getInteractionManager();
	
	IRenderTickCounter getRenderTickCounter();
}
