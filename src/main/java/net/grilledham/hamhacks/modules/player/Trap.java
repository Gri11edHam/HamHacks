package net.grilledham.hamhacks.modules.player;

import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class Trap extends Module {
	
	public Trap() {
		super(Text.translatable("hamhacks.module.trap"), Category.PLAYER, new Keybind(GLFW.GLFW_KEY_M));
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		PlayerEntity p = null;
		for(PlayerEntity player : mc.world.getPlayers()) {
			if(player != mc.player) {
				if(p == null) {
					p = player;
				} else if(mc.player.distanceTo(player) < mc.player.distanceTo(p)) {
					p = player;
				}
			}
		}
		if(p != null) {
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
			for(int x = 0; x < 2; x++) {
				BlockPos pos = p.getBlockPos().add(0, x, 0);
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
		}
		setEnabled(false);
	}
}
