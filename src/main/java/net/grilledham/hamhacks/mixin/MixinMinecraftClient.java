package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.HamHacksClient;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.gui.screen.impl.NewVersionScreen;
import net.grilledham.hamhacks.mixininterface.IClientPlayerInteractionManager;
import net.grilledham.hamhacks.mixininterface.IMinecraftClient;
import net.grilledham.hamhacks.mixininterface.IRenderTickCounter;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.misc.NoTelemetry;
import net.grilledham.hamhacks.modules.misc.TitleBar;
import net.grilledham.hamhacks.notification.Notifications;
import net.grilledham.hamhacks.util.MouseUtil;
import net.grilledham.hamhacks.util.Updater;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.session.Session;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceReload;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.ModStatus;
import net.minecraft.util.Unit;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient extends ReentrantThreadExecutor<Runnable> implements WindowEventHandler, IMinecraftClient {
	
	@Shadow
	public ClientPlayerInteractionManager interactionManager;
	
	@Shadow
	@Final
	private RenderTickCounter renderTickCounter;
	
	@Shadow public abstract Session getSession();
	
	@Shadow @Nullable public Screen currentScreen;
	
	@Shadow
	public static ModStatus getModStatus() {
		return null;
	}
	
	@Shadow public abstract @Nullable ClientPlayNetworkHandler getNetworkHandler();
	
	@Shadow private @Nullable IntegratedServer server;
	
	@Shadow public abstract ServerInfo getCurrentServerEntry();
	
	public MixinMinecraftClient(String string) {
		super(string);
	}
	
	@Override
	public IClientPlayerInteractionManager hamHacks$getInteractionManager() {
		return (IClientPlayerInteractionManager)interactionManager;
	}
	
	@Override
	public IRenderTickCounter hamHacks$getRenderTickCounter() {
		return (IRenderTickCounter)renderTickCounter;
	}
	
	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ReloadableResourceManagerImpl;reload(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;Ljava/util/List;)Lnet/minecraft/resource/ResourceReload;"))
	public ResourceReload loadResources(ReloadableResourceManagerImpl instance, Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> packs) {
		ResourceReload toReturn = instance.reload(prepareExecutor, applyExecutor, initialStage, packs);
		toReturn.whenComplete().thenRun(() -> {
			if(HamHacksClient.firstTime) {
				Notifications.notify("HamHacks", "Welcome to HamHacks, " + getSession().getUsername());
			} else {
				Notifications.notify("HamHacks", "Welcome back, " + getSession().getUsername());
			}
			if(Updater.newVersionAvailable()) {
				Notifications.notify("HamHacks", "New version available: " + Updater.getLatest().getVersion(0, true) + ". Click to update", () -> MinecraftClient.getInstance().setScreen(new NewVersionScreen(currentScreen)));
			}
			HamHacksClient.reloadResources();
		});
		return toReturn;
	}
	
	@Inject(method = "getWindowTitle", at = @At("HEAD"), cancellable = true)
	public void modifyWindowTitle(CallbackInfoReturnable<String> cir) {
		TitleBar titleBar = ModuleManager.getModule(TitleBar.class);
		if(titleBar != null && titleBar.isEnabled() && !titleBar.title.get().equals("")) {
			StringBuilder stringBuilder = new StringBuilder();
			ClientPlayNetworkHandler clientPlayNetworkHandler = getNetworkHandler();
			if (clientPlayNetworkHandler != null && clientPlayNetworkHandler.getConnection().isOpen()) {
				if (server != null && !server.isRemote()) {
					stringBuilder.append(I18n.translate("title.singleplayer"));
				} else if (getCurrentServerEntry() != null && getCurrentServerEntry().isRealm()) {
					stringBuilder.append(I18n.translate("title.multiplayer.realms"));
				} else if (this.server == null && (getCurrentServerEntry() == null || !getCurrentServerEntry().isLocal())) {
					stringBuilder.append(I18n.translate("title.multiplayer.other"));
				} else {
					stringBuilder.append(I18n.translate("title.multiplayer.lan"));
				}
			}
			cir.setReturnValue(titleBar.titleProvider.getTitle(getModStatus().isModded(), HamHacksClient.VERSION.getVersion(0, true), SharedConstants.getGameVersion().getName(), stringBuilder.toString(), ModuleManager.getModules().stream().filter(Module::isEnabled).toList().size(), ModuleManager.getModules().size()));
		}
	}
	
	@Inject(method = "reloadResources()Ljava/util/concurrent/CompletableFuture;", at = @At("TAIL"), cancellable = true)
	public void onResourceReload(CallbackInfoReturnable<CompletableFuture<Void>> cir) {
		cir.setReturnValue(cir.getReturnValue().thenRun(HamHacksClient::reloadResources));
	}
	
	@Inject(method = "<init>", at = @At("TAIL"))
	public void init(RunArgs args, CallbackInfo ci) {
		HamHacksClient.init();
	}
	
	@Inject(method = "tick", at = @At("TAIL"))
	public void tickEvent(CallbackInfo ci) {
		new EventTick().call();
		MouseUtil.checkForMouseMove();
		ModuleManager.updateKeybinds();
	}
	
	@Inject(method = "isTelemetryEnabledByApi", at = @At("HEAD"), cancellable = true)
	private void disableTelemetry(CallbackInfoReturnable<Boolean> cir) {
		if(ModuleManager.getModule(NoTelemetry.class).isEnabled()) {
			cir.setReturnValue(false);
		}
	}
	
	@Inject(method = "stop", at = @At("HEAD"))
	public void shutdown(CallbackInfo ci) {
		HamHacksClient.shutdown();
	}
}
