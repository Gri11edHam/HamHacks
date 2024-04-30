package net.grilledham.hamhacks.mixin;

import com.mojang.datafixers.DataFixer;
import net.grilledham.hamhacks.mixininterface.IIntegratedServer;
import net.minecraft.client.network.LanServerPinger;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.ApiServices;
import net.minecraft.world.level.storage.LevelStorage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.net.Proxy;

@Mixin(IntegratedServer.class)
public abstract class MixinIntegratedServer extends MinecraftServer implements IIntegratedServer {
	
	@Shadow @Nullable private LanServerPinger lanPinger;
	
	public MixinIntegratedServer(Thread serverThread, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, Proxy proxy, DataFixer dataFixer, ApiServices apiServices, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory) {
		super(serverThread, session, dataPackManager, saveLoader, proxy, dataFixer, apiServices, worldGenerationProgressListenerFactory);
	}
	
	@Override
	public boolean hamHacks$isOpenToLAN() {
		return lanPinger != null;
	}
}
