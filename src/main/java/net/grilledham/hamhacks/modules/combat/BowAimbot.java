package net.grilledham.hamhacks.modules.combat;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.BoolSetting;
import net.grilledham.hamhacks.setting.EntityTypeSelector;
import net.grilledham.hamhacks.util.RotationHack;
import net.grilledham.hamhacks.util.TrajectoryUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class BowAimbot extends Module {
	
	private Entity target = null;
	
	private final EntityTypeSelector entitySelector = new EntityTypeSelector("hamhacks.module.bowAimbot.entitySelector", () -> true, EntityType.PLAYER);
	
	public BowAimbot() {
		super(Text.translatable("hamhacks.module.bowAimbot"), Category.COMBAT, new Keybind());
		
		GENERAL_CATEGORY.add(entitySelector);
	}
	
	@Override
	public String getHUDText() {
		return super.getHUDText() + " \u00a77" + (target == null ? "None" : target.getName().getString());
	}
	
	@EventListener
	public void onTick(EventTick e) {
		target = null;
		if(mc.player == null) return;
		
		if(mc.player.isUsingItem() && mc.player.getActiveItem().getItem() == Items.BOW) {
			float velocity = (mc.player.getItemUseTime() - mc.player.getItemUseTimeLeft()) / 20f;
			velocity = (velocity * velocity + velocity * 2) / 3;
			if (velocity > 1) velocity = 1;
			
			Stream<Entity> stream = mc.world.getEntitiesByType(TypeFilter.instanceOf(Entity.class), new Box(mc.player.getBlockPos().add(-64, -64, -64).toCenterPos(), mc.player.getBlockPos().add(64, 64, 64).toCenterPos()), Objects::nonNull).stream()
					.filter(entity -> entity instanceof LivingEntity)
					.filter(entity -> !entity.isRemoved() && entity.isAlive())
					.filter(entity -> entity != mc.player)
					.filter(entity -> Math.abs(entity.getY() - mc.player.getY()) <= 1e6)
					.filter(entity -> entitySelector.get(entity.getType()));
			
			List<Entity> entities = stream.toList();
			
			if(!entities.isEmpty()) {
				target = getClosestEntityToCrosshair(entities);
				Vec2f look = TrajectoryUtil.getAngle(mc.player.getPos(), target.getPos().subtract(0, 1.9f - target.getHeight(), 0), velocity);
				if(Float.isNaN(look.y)) {
					target = null;
				} else {
					RotationHack.facePacket(look);
				}
			}
		}
	}
	
	public static float[] getYawPitchBetween(
			double sourceX, double sourceY, double sourceZ,
			double targetX, double targetY, double targetZ) {
		
		double diffX = targetX - sourceX;
		double diffY = targetY - sourceY;
		double diffZ = targetZ - sourceZ;
		
		double dist = MathHelper.sqrt((float)(diffX * diffX + diffZ * diffZ));
		
		float yaw = (float)((Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F);
		float pitch = (float)-(Math.atan2(diffY, dist) * 180.0D / Math.PI);
		
		return new float[]{yaw, pitch};
	}
	
	private float[] getClosestYawPitchBetween(PlayerEntity source, Entity target) {
		float[] bestYawPitch = new float[]{Float.MAX_VALUE, Float.MAX_VALUE};
		
		for(float factor : new float[]{0f, 0.05f, 0.1f, 0.25f, 0.5f, 0.75f, 1.0f}) {
			float[] yawPitch = getYawPitchBetween(
					// source
					source.getPos().x,
					source.getPos().y + source.getEyeHeight(source.getPose()),
					source.getPos().z,
					// target
					target.getPos().x,
					target.getPos().y + target.getEyeHeight(target.getPose()),
					target.getPos().z
			);
			
			if(Math.abs(yawPitch[0]) + Math.abs(yawPitch[1]) < Math.abs(bestYawPitch[0]) + Math.abs(bestYawPitch[1])) {
				bestYawPitch = yawPitch;
			}
		}
		return bestYawPitch;
	}
	
	public Entity getClosestEntityToCrosshair(List<Entity> entities) {
		float minDist = Float.MAX_VALUE;
		Entity closest = null;
		
		for(Entity entity : entities) {
			// Get distance between the two entities (rotations)
			float[] yawPitch = getClosestYawPitchBetween(
					mc.player, entity
			);
			
			// Compute the distance from the player's crosshair
			float distYaw = MathHelper.abs(MathHelper.wrapDegrees(yawPitch[0] - mc.player.headYaw));
			float distPitch = MathHelper.abs(MathHelper.wrapDegrees(yawPitch[1] - mc.player.renderPitch));
			float dist = MathHelper.sqrt(distYaw * distYaw + distPitch * distPitch);
			
			// Get the closest entity
			if(dist < minDist) {
				closest = entity;
				minDist = dist;
			}
		}
		
		return closest;
	}
}
