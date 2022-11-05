package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.movement.BoatFly;
import net.minecraft.entity.vehicle.BoatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BoatEntity.class)
public class MixinBoatEntity {
	
	@Shadow private boolean pressingLeft;
	@Shadow private boolean pressingRight;
	@Shadow private boolean pressingForward;
	@Shadow private boolean pressingBack;
	
	@Inject(method = "setInputs", at = @At("HEAD"), cancellable = true)
	public void updatePositionAndRotation(boolean pressingLeft, boolean pressingRight, boolean pressingForward, boolean pressingBack, CallbackInfo ci) {
		if(ModuleManager.getModule(BoatFly.class).isEnabled()) {
			ci.cancel();
			this.pressingLeft = false;
			this.pressingRight = false;
			this.pressingForward = false;
			this.pressingBack = false;
		}
	}
}
