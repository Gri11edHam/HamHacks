package net.grilledham.hamhacks.modules.movement;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventMotion;
import net.grilledham.hamhacks.mixininterface.IClientEntityPlayer;
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
			boolean canSprint = (float)mc.player.getHungerManager().getFoodLevel() > 6.0F || mc.player.getAbilities().allowFlying;
			if(!mc.player.isSprinting() && (!mc.player.isTouchingWater() || mc.player.isSubmergedInWater()) && ((IClientEntityPlayer)mc.player).walking() && canSprint && !mc.player.isUsingItem() && !mc.player.hasStatusEffect(StatusEffects.BLINDNESS)) {
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
