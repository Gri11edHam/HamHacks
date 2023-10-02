package net.grilledham.hamhacks.mixin;

import net.minecraft.client.session.telemetry.TelemetryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TelemetryManager.class)
public class MixinTelemetryManager {
	
	@Redirect(method = "computeSender", at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;isDevelopment:Z"))
	private boolean disableTelemetry() {
		return true;
	}
}
