package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.mixininterface.IKeyBinding;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(KeyBinding.class)
public abstract class MixinKeyBinding implements Comparable<KeyBinding>, IKeyBinding {
	
	@Shadow private InputUtil.Key boundKey;
	
	@Override
	public InputUtil.Key getBound() {
		return boundKey;
	}
}
