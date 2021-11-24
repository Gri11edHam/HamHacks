package net.grilledham.hamhacks.modules.player;

import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class Encase extends Module {
	
	public Encase() {
		super("Encase", Category.PLAYER, new Keybind(GLFW.GLFW_KEY_V));
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		int slot = -1;
		int oldSlot = mc.player.getInventory().selectedSlot;
		for(int i = 0; i < 9; i++) {
			ItemStack stack = mc.player.getInventory().getStack(i);
			
			if(stack.getItem() == Items.OBSIDIAN) {
				slot = i;
			}
		}
		if(slot < 0) {
			return;
		}
		mc.player.getInventory().selectedSlot = slot;
		Vec3d eyesPos = new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ());
		for(int x = 0; x < 1; x++) {
			BlockPos pos = mc.player.getBlockPos().add(0, x, 0);
			for(Direction side : Direction.values()) {
				BlockPos neighbor = pos.offset(side);
				Direction side2 = side.getOpposite();
				
				Vec3d hitVec = Vec3d.ofCenter(neighbor).add(Vec3d.of(side2.getVector()).multiply(0.5));
				
				if(eyesPos.squaredDistanceTo(hitVec) > 30.25) {
					continue;
				}
				
				double diffX = hitVec.x - eyesPos.x;
				double diffY = hitVec.y - eyesPos.y;
				double diffZ = hitVec.z - eyesPos.z;
				
				double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
				
				float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
				float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));
				
				PlayerMoveC2SPacket.LookAndOnGround packet = new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, mc.player.isOnGround());
				mc.player.networkHandler.sendPacket(packet);
				imc.getInteractionManager().rightClickBlock(neighbor, side2, hitVec);
				mc.player.swingHand(Hand.MAIN_HAND);
			}
		}
		mc.player.getInventory().selectedSlot = oldSlot;
		enabled.setValue(false);
		onDisable();
	}
}
