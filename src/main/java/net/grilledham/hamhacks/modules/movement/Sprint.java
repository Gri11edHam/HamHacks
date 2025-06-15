package net.grilledham.hamhacks.modules.movement;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventMotion;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class Sprint extends Module {
	
	public Sprint() {
		super(Text.translatable("hamhacks.module.sprint"), Category.MOVEMENT, new Keybind(GLFW.GLFW_KEY_G));
	}
	
	@EventListener
	public void onMove(EventMotion e) {
		if(e.type == EventMotion.Type.PRE) {
			if(mc.player == null) {
				return;
			}
			boolean canSprint = !mc.player.isSprinting() && mc.player.input.hasForwardMovement() && (mc.player.hasVehicle() || (float)mc.player.getHungerManager().getFoodLevel() > 6.0F || mc.player.getAbilities().allowFlying) && !mc.player.isUsingItem() && !mc.player.hasStatusEffect(StatusEffects.BLINDNESS) && (!mc.player.hasVehicle() || (mc.player.getVehicle().canSprintAsVehicle() && mc.player.getVehicle().isLogicalSideForUpdatingMovement()) && (!mc.player.isGliding() || mc.player.isSubmergedInWater()) && (!mc.player.shouldSlowDown() || mc.player.isSubmergedInWater()) && (!mc.player.isTouchingWater() || mc.player.isSubmergedInWater()));
			if(canSprint && !mc.player.isSneaking()) {
				mc.player.setSprinting(true);
			}
		}
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		if(mc.player != null && mc.player.isSprinting()) {
			mc.player.setSprinting(false);
		}
	}
}
