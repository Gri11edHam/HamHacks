package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.client.HamHacksClient;
import net.grilledham.hamhacks.modules.misc.AntiBanModule;
import net.minecraft.client.util.ProfileKeys;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.Signer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ProfileKeys.class)
public class MixinProfileKeys {
	
	@Inject(method = "getPublicKey", at = @At("HEAD"), cancellable = true)
	private void removePublicKey(CallbackInfoReturnable<Optional<PlayerPublicKey>> cir) {
		if(AntiBanModule.getInstance().isEnabled() && !AntiBanModule.getInstance().hasConnected) {
			cir.setReturnValue(Optional.empty());
		} else {
			if(AntiBanModule.getInstance().isEnabled()) {
			}
		}
	}
	
	@Inject(method = "getPublicKeyData", at = @At("HEAD"), cancellable = true)
	private void onProfilePublicKeyData(CallbackInfoReturnable<Optional<PlayerPublicKey.PublicKeyData>> cir) {
		if(AntiBanModule.getInstance().isEnabled() && !AntiBanModule.getInstance().hasConnected) {
			cir.setReturnValue(Optional.empty());
		} else {
			if(AntiBanModule.getInstance().isEnabled()) {
			}
		}
	}
	
	@Inject(method = "getSigner", at = @At("HEAD"), cancellable = true)
	private void onSigner(CallbackInfoReturnable<Optional<Signer>> cir) {
		if(AntiBanModule.getInstance().isEnabled() && !AntiBanModule.getInstance().hasConnected) {
			HamHacksClient.LOGGER.info("Successfully removed signer!");
			cir.setReturnValue(null);
		} else {
			if(AntiBanModule.getInstance().isEnabled()) {
			}
		}
	}
}
