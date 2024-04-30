package net.grilledham.hamhacks.modules.combat;

import com.google.common.collect.Lists;
import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.player.Encase;
import net.grilledham.hamhacks.util.RotationHack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class CrystalAura extends Module {
	
	public CrystalAura() {
		super(Text.translatable("hamhacks.module.crystalAura"), Category.COMBAT, new Keybind(GLFW.GLFW_KEY_C));
	}
	
	@EventListener
	public void onTick(EventTick e) {
		if(mc.world == null) {
			setEnabled(false);
			return;
		}
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
				
				if(stack.getItem() == Items.END_CRYSTAL) {
					slot = i;
				}
			}
			if(slot < 0) {
				setEnabled(false);
				return;
			}
			mc.player.getInventory().selectedSlot = slot;
			Vec3d eyesPos = new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ());
			
			List<Vec3i> possiblePositions = Lists.newArrayList();
			for(int x = -2; x < 3; x++) {
				for(int y = -2; y < 4; y++) {
					for(int z = -2; z < 3; z++) {
						possiblePositions.add(new Vec3i(x, y, z));
					}
				}
			}
			possiblePositions.sort((o1, o2) -> {
				if(o1.getSquaredDistance(new Vec3i(0, 0, 0)) < o2.getSquaredDistance(new Vec3i(0, 0, 0))) {
					return 1;
				} else if(o1.getSquaredDistance(new Vec3i(0, 0, 0)) > o2.getSquaredDistance(new Vec3i(0, 0, 0))) {
					return -1;
				}
				return 0;
			});
			for(Vec3i possiblePosition : possiblePositions) {
				if(tryAura(p, eyesPos, possiblePosition)) {
					break;
				}
			}
			mc.player.getInventory().selectedSlot = oldSlot;
		}
	}
	
	private boolean tryAura(PlayerEntity p, Vec3d eyesPos, Vec3i offset) {
		BlockPos pos = p.getBlockPos().add(offset);
		for(Direction side : Direction.values()) {
			BlockPos neighbor = pos.offset(side);
			Direction side2 = side.getOpposite();
			
			Vec3d hitVec = Vec3d.ofCenter(neighbor).add(Vec3d.of(side2.getVector()).multiply(0.5));
			
			if(hitVec.distanceTo(mc.player.getPos()) <= 3 && !ModuleManager.getModule(Encase.class).playerSafe) {
				continue;
			}
			if(hitVec.distanceTo(p.getPos()) >= 4) {
				continue;
			}
			
			if(eyesPos.distanceTo(hitVec) > 6) {
				return false;
			}
			
			for(Entity entity : mc.world.getEntities()) {
				if(entity instanceof EndCrystalEntity) {
					if(entity.getPos().squaredDistanceTo(hitVec) <= 4) {
						if(entity.isAlive()) {
							imc.hamHacks$getInteractionManager().hamHacks$leftClickEntity(entity);
							return true;
						}
					}
				}
			}
			
			if(hitVec.isInRange(mc.player.getPos(), 5)) {
				return false;
			}
			
			RotationHack.faceVectorPacket(hitVec);
			if(imc.hamHacks$getInteractionManager().hamHacks$rightClickBlock(neighbor, side2, hitVec)) {
				mc.player.swingHand(Hand.MAIN_HAND);
				return true;
			}
		}
		return false;
	}
}
