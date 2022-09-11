package net.grilledham.hamhacks.mixin;

import com.mojang.authlib.yggdrasil.response.KeyPairResponse;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.misc.AntiBan;
import net.minecraft.client.util.ProfileKeys;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.Signer;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ProfileKeys.class)
public class MixinProfileKeys {
	
	@Inject(method = "getPublicKey", at = @At("HEAD"), cancellable = true)
	private void removePublicKey(CallbackInfoReturnable<Optional<PlayerPublicKey>> cir) {
		if(ModuleManager.getModule(AntiBan.class).isEnabled() && !ModuleManager.getModule(AntiBan.class).hasConnected) {
			cir.setReturnValue(Optional.empty());
		}
	}
	
	@Inject(method = "decodeKeyPairResponse", at = @At("HEAD"), cancellable = true)
	private static void decodeKeyPairResponse(KeyPairResponse keyPairResponse, CallbackInfoReturnable<PlayerPublicKey.PublicKeyData> cir) {
		if(ModuleManager.getModule(AntiBan.class).isEnabled() && !ModuleManager.getModule(AntiBan.class).hasConnected) {
			cir.setReturnValue(null);
		}
	}
	
	@Dynamic("1.19.1")
	@Inject(method = {"getPublicKeyData()Ljava/util/Optional;", "method_43784()Ljava/util/Optional;"}, at = @At("HEAD"), cancellable = true)
	private void onProfilePublicKeyData(CallbackInfoReturnable<Optional<PlayerPublicKey.PublicKeyData>> cir) {
		if(ModuleManager.getModule(AntiBan.class).isEnabled() && !ModuleManager.getModule(AntiBan.class).hasConnected) {
			cir.setReturnValue(Optional.empty());
		}
	}
	
	@Inject(method = "getSigner", at = @At("HEAD"), cancellable = true)
	private void onSigner(CallbackInfoReturnable<Optional<Signer>> cir) {
		if(ModuleManager.getModule(AntiBan.class).isEnabled() && !ModuleManager.getModule(AntiBan.class).hasConnected) {
			cir.setReturnValue(null);
		}
	}
}
