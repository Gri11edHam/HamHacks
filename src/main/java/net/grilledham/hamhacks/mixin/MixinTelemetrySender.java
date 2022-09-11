package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.misc.NoTelemetry;
import net.minecraft.SharedConstants;
import net.minecraft.client.util.telemetry.TelemetrySender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TelemetrySender.class)
public class MixinTelemetrySender {
	
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;isDevelopment:Z"))
	private boolean disableTelemetry() {
		if(ModuleManager.getModule(NoTelemetry.class).isEnabled()) {
			return true;
		}
		return SharedConstants.isDevelopment;
	}
}
