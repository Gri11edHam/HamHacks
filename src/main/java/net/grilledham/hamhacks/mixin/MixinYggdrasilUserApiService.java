package net.grilledham.hamhacks.mixin;

import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;
import com.mojang.authlib.yggdrasil.response.UserAttributesResponse;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.misc.NoTelemetry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.Executor;

@Mixin(YggdrasilUserApiService.class)
public class MixinYggdrasilUserApiService {
	
	@Inject(method = "newTelemetrySession", at = @At("RETURN"), cancellable = true, remap = false)
	private void disableTelemetry(Executor executor, CallbackInfoReturnable<TelemetrySession> cir) {
		if(ModuleManager.getModule(NoTelemetry.class).isEnabled()) {
			cir.setReturnValue(TelemetrySession.DISABLED);
		}
	}
	
	@Redirect(method = "fetchProperties", at = @At(value = "INVOKE", target = "Lcom/mojang/authlib/yggdrasil/response/UserAttributesResponse$Privileges;getTelemetry()Z", remap = false), remap = false)
	private boolean disableTelemetry(UserAttributesResponse.Privileges instance) {
		return false;
	}
}
