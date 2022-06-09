package net.grilledham.hamhacks.modules.movement;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class BoatFly extends Module {
	
	public BoatFly() {
		super(Text.translatable("module.hamhacks.boatfly"), Text.translatable("module.hamhacks.boatfly.tooltip"), Category.MOVEMENT, new Keybind(GLFW.GLFW_KEY_B));
	}
	
	@EventListener
	public void onTick(EventTick e) {
		if(mc.player == null) {
			return;
		}
		if(mc.player.hasVehicle()) {
			Entity vehicle = mc.player.getVehicle();
			Vec3d velocity = vehicle.getVelocity();
			double motionY = 0;
			if(vehicle.getType() == EntityType.BOAT) {
				motionY = mc.options.jumpKey.isPressed() ? 0.3 : mc.options.sprintKey.isPressed() ? -0.26 : 0.04;
			} else if(vehicle.getType() == EntityType.HORSE || vehicle.getType() == EntityType.SKELETON_HORSE || vehicle.getType() == EntityType.ZOMBIE_HORSE || vehicle.getType() == EntityType.DONKEY || vehicle.getType() == EntityType.MULE) {
				motionY = mc.options.jumpKey.isPressed() ? 0.3 : mc.options.sprintKey.isPressed() ? -0.3 : 0.00;
			}
			vehicle.setVelocity(new Vec3d(velocity.x, motionY, velocity.z));
		}
	}
}
