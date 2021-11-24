package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.client.HamHacksClient;
import net.grilledham.hamhacks.event.EventRender;
import net.grilledham.hamhacks.event.EventTick;
import net.grilledham.hamhacks.mixininterface.IClientPlayerInteractionManager;
import net.grilledham.hamhacks.mixininterface.IMinecraftClient;
import net.grilledham.hamhacks.util.MouseUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.snooper.SnooperListener;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient extends ReentrantThreadExecutor<Runnable> implements SnooperListener,
		WindowEventHandler, AutoCloseable, IMinecraftClient {
	
	@Shadow
	public ClientPlayerInteractionManager interactionManager;
	
	public MixinMinecraftClient(String string) {
		super(string);
	}
	
	@Override
	public IClientPlayerInteractionManager getInteractionManager() {
		return (IClientPlayerInteractionManager)interactionManager;
	}
	
	@Inject(method = "<init>", at = @At("TAIL"))
	public void init(RunArgs args, CallbackInfo ci) {
		HamHacksClient.init();
	}
	
	@Inject(method = "tick", at = @At("TAIL"))
	public void tickEvent(CallbackInfo ci) {
		new EventTick().call();
		MouseUtil.checkForMouseMove();
	}
	
	@Inject(method = "render", at = @At("TAIL"))
	public void renderEvent(boolean tick, CallbackInfo ci) {
		new EventRender().call();
	}
}
