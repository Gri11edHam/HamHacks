package net.grilledham.hamhacks.modules.player;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventMotion;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.movement.Speed;
import net.grilledham.hamhacks.notification.Notifications;
import net.grilledham.hamhacks.util.RotationHack;
import net.grilledham.hamhacks.util.math.Vec3;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class Encase extends Module {
	
	public boolean playerSafe = false;
	
	private Vec3 anchorPos = new Vec3();
	
	private boolean disabledSpeed = false;
	
	public Encase() {
		super(Text.translatable("hamhacks.module.encase"), Category.PLAYER, new Keybind(GLFW.GLFW_KEY_LEFT_ALT));
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		anchorPos = null;
		playerSafe = false;
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		anchorPos = null;
		playerSafe = false;
		Speed speed = ModuleManager.getModule(Speed.class);
		if(disabledSpeed) {
			speed.reEnable();
			if(speed.isEnabled()) {
				Notifications.notify(getName(), "Re-Enabled " + speed.getName());
			}
			disabledSpeed = false;
		}
	}
	
	@EventListener
	public void onTick(EventTick e) {
		if(mc.player == null) {
			anchorPos = null;
			setEnabled(false);
			return;
		}
		int slot = -1;
		int oldSlot = mc.player.getInventory().getSelectedSlot();
		for(int i = 0; i < 9; i++) {
			ItemStack stack = mc.player.getInventory().getStack(i);
			
			if(stack.getItem() == Items.OBSIDIAN) {
				slot = i;
			}
		}
		if(slot < 0) {
			anchorPos = null;
			setEnabled(false);
			return;
		}
		mc.player.getInventory().setSelectedSlot(slot);
		if(anchorPos == null) {
			anchorPos = new Vec3();
		}
		anchorPos.set(Vec3d.ofCenter(mc.player.getBlockPos()));
		anchorPos.setY(Math.floor(anchorPos.getY()) - 0.1);
		Vec3 eyesPos = new Vec3(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ());
		BlockPos pos = mc.player.getBlockPos();
		for(Direction side : Direction.stream().filter(d -> d != Direction.UP).toList()) {
			BlockPos neighbor = pos.offset(side);
			Direction side2 = side.getOpposite();
			
			Vec3d hitVec = Vec3d.ofCenter(neighbor).add(Vec3d.of(side2.getVector()).multiply(0.5));
			
			if(eyesPos.get().squaredDistanceTo(hitVec) > 30.25) {
				break;
			}
			
			RotationHack.faceVectorPacket(hitVec);
			playerSafe = false;
			if(imc.hamHacks$getInteractionManager().hamHacks$rightClickBlock(neighbor, side2, hitVec)) {
				mc.player.swingHand(Hand.MAIN_HAND);
				playerSafe = true;
				break;
			}
		}
		mc.player.getInventory().setSelectedSlot(oldSlot);
	}
	
	@EventListener
	public void onMove(EventMotion e) {
		if(e.type == EventMotion.Type.PRE) {
			Speed speed = ModuleManager.getModule(Speed.class);
			if(anchorPos != null) {
				if(!disabledSpeed) {
					if(speed.isEnabled()) {
						Notifications.notify(getName(), "Disabled " + speed.getName());
					}
					speed.forceDisable();
					disabledSpeed = true;
				}
				mc.player.setVelocity(mc.player.getVelocity().x, getVelocityTo(anchorPos, 0.25).y, mc.player.getVelocity().z);
				if(mc.options.forwardKey.isPressed() || mc.options.backKey.isPressed() || mc.options.leftKey.isPressed() || mc.options.rightKey.isPressed()) {
					return;
				}
				mc.player.setVelocity(getVelocityTo(anchorPos, 0.25));
			} else {
				if(disabledSpeed) {
					speed.reEnable();
					if(speed.isEnabled()) {
						Notifications.notify(getName(), "Re-Enabled " + speed.getName());
					}
					disabledSpeed = false;
				}
			}
		}
	}
	
	private Vec3d getVelocityTo(Vec3 pos, double speed) {
		return pos.get().subtract(mc.player.getPos()).multiply(speed);
	}
}
