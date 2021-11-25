package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.event.EventKey;
import net.grilledham.hamhacks.mixininterface.IKeyBinding;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBinding.class)
public class MixinKeyBinding implements Comparable<KeyBinding>, IKeyBinding {
	
	
	@Shadow private InputUtil.Key boundKey;
	
	@Inject(method = "updatePressedStates", at = @At("HEAD"))
	private static void keyPressed(CallbackInfo ci) {
		new EventKey().call();
	}
	
	@Shadow
	public int compareTo(@NotNull KeyBinding o) {
		return 0;
	}
	
	@Override
	public InputUtil.Key getBound() {
		return boundKey;
	}
}
