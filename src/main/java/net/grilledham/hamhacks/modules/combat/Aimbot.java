package net.grilledham.hamhacks.modules.combat;

import com.google.common.collect.Lists;
import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.BoolSetting;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.grilledham.hamhacks.setting.SettingCategory;
import net.grilledham.hamhacks.util.MouseUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public class Aimbot extends Module {
	
	private final SettingCategory OPTIONS_CATEGORY = new SettingCategory("hamhacks.module.aimbot.category.options");
	
	private final NumberSetting speed = new NumberSetting("hamhacks.module.aimbot.speed", 10, () -> true, 0.1, 100);
	
	private final NumberSetting fov = new NumberSetting("hamhacks.module.aimbot.fov", 90, () -> true, 0.1, 360);
	
	private final SettingCategory TARGETING_CATEGORY = new SettingCategory("hamhacks.module.aimbot.category.targeting");
	
	private final BoolSetting targetEntities = new BoolSetting("hamhacks.module.aimbot.targetEntities", true, () -> true);
	
	private final BoolSetting keepAiming = new BoolSetting("hamhacks.module.aimbot.keepAiming", false, targetEntities::get);
	
	private final BoolSetting targetPlayers = new BoolSetting("hamhacks.module.aimbot.targetPlayers", true, targetEntities::get);
	
	private final BoolSetting targetPassive = new BoolSetting("hamhacks.module.aimbot.targetPassive", false, targetEntities::get);
	
	private final BoolSetting targetHostile = new BoolSetting("hamhacks.module.aimbot.targetHostile", false, targetEntities::get);
	
	private final BoolSetting targetBlocks = new BoolSetting("hamhacks.module.aimbot.targetBlocks", false, () -> true);
	
	private Entity entity = null;
	private Entity entityToAim = null;
	private HitResult blockToAim = null;
	
	public Aimbot() {
		super(Text.translatable("hamhacks.module.aimbot"), Category.COMBAT, new Keybind(0));
		settingCategories.add(0, OPTIONS_CATEGORY);
		OPTIONS_CATEGORY.add(speed);
		OPTIONS_CATEGORY.add(fov);
		settingCategories.add(1, TARGETING_CATEGORY);
		TARGETING_CATEGORY.add(targetEntities);
		TARGETING_CATEGORY.add(keepAiming);
		TARGETING_CATEGORY.add(targetPlayers);
		TARGETING_CATEGORY.add(targetPassive);
		TARGETING_CATEGORY.add(targetHostile);
		TARGETING_CATEGORY.add(targetBlocks);
	}
	
	@Override
	public String getHUDText() {
		String extra;
		if(entity != null) {
			extra = entity.getName().getString();
		} else if(blockToAim != null) {
			extra = blockToAim.getType().name();
		} else {
			extra = "None";
		}
		return super.getHUDText() + " \u00a77" + extra;
	}
	
	@EventListener
	public void onTick(EventTick e) {
		entity = null;
		if(mc.world == null) {
			return;
		}
		if(keepAiming.get()) {
			if(entityToAim != null && !MouseUtil.mouseMoved()) {
				entity = entityToAim;
				entityToAim.getPos().floorAlongAxes(EnumSet.allOf(Direction.Axis.class)).add(0.5, 0.5, 0.5);
				float[] rotation = getRotationsNeeded(entityToAim, null, fov.get(), fov.get(), speed.get(), speed.get());
				
				mc.player.setPitch(rotation[1]);
				mc.player.setYaw(rotation[0]);
			} else if(MouseUtil.mouseMoved()) {
				if((mc.targetedEntity instanceof PlayerEntity && targetPlayers.get()) || (mc.targetedEntity instanceof PassiveEntity && targetPassive.get()) || (mc.targetedEntity instanceof HostileEntity && targetHostile.get())) {
					entityToAim = mc.targetedEntity;
				}
			}
		} else {
			Entity entity = getClosestEntityToCrosshair(Lists.newCopyOnWriteArrayList(mc.world.getEntities()).stream().filter(ent -> ent.getPos().distanceTo(mc.player.getPos()) < 6 && ent != mc.player/* && ent instanceof PlayerEntity*/).collect(Collectors.toList()));
			if(entity != null && targetEntities.get()) {
				if((entity instanceof PlayerEntity && targetPlayers.get()) || (entity instanceof PassiveEntity && targetPassive.get()) || (entity instanceof HostileEntity && targetHostile.get())) {
					this.entity = entity;
					float[] rotation = getRotationsNeeded(entity, null, fov.get(), fov.get(), speed.get(), speed.get());
					
					mc.player.setPitch(rotation[1]);
					mc.player.setYaw(rotation[0]);
				}
			}
		}
		if(targetBlocks.get()) {
			HitResult pos = blockToAim;
			if(pos != null && !MouseUtil.mouseMoved()) {
				pos.getPos().floorAlongAxes(EnumSet.allOf(Direction.Axis.class)).add(0.5, 0.5, 0.5);
				float[] rotation = getRotationsNeeded(null, pos.getPos(), 360, 360, speed.get(), speed.get());
				
				mc.player.setPitch(rotation[1]);
				mc.player.setYaw(rotation[0]);
			} else if(MouseUtil.mouseMoved()) {
				if(mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
					blockToAim = mc.crosshairTarget;
				} else {
					blockToAim = null;
				}
			}
		}
	}
	
	public float[] getRotationsNeeded(Entity target, Vec3d blockPos, double fovX, double fovY, double stepX, double stepY) {
		float[] yawPitch = new float[2];
		if(target != null) {
			yawPitch = getClosestYawPitchBetween(
					mc.player,
					target
			);
		}
		if(blockPos != null) {
			yawPitch = getClosestYawPitchBetween(
					mc.player.getPos().x,
					mc.player.getPos().y + mc.player.getEyeHeight(mc.player.getPose()),
					mc.player.getPos().z,
					blockPos.getX(),
					blockPos.getY(),
					blockPos.getZ()
			);
		}
		
		float yaw = yawPitch[0];
		float pitch = yawPitch[1];
		
		boolean inFovX = MathHelper.abs(MathHelper.wrapDegrees(yaw - mc.player.getYaw())) <= fovX;
		boolean inFovY = MathHelper.abs(MathHelper.wrapDegrees(pitch - mc.player.getPitch())) <= fovY;
		
		if(inFovX && inFovY) {
			float yawFinal, pitchFinal;
			yawFinal = (float)(((MathHelper.wrapDegrees(yaw - mc.player.getYaw())) * stepX) / 100);
			pitchFinal = (float)(((MathHelper.wrapDegrees(pitch - mc.player.getPitch())) * stepY) / 100);
			
			return new float[]{mc.player.getYaw() + yawFinal, mc.player.getPitch() + pitchFinal};
		} else {
			return new float[]{mc.player.getYaw(), mc.player.getPitch()};
		}
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
	
	private float[] getClosestYawPitchBetween(double sourceX, double sourceY, double sourceZ,
											  double targetX, double targetY, double targetZ) {
		float[] bestYawPitch = new float[]{Float.MAX_VALUE, Float.MAX_VALUE};
		
		for(float factor : new float[]{0f, 0.05f, 0.1f, 0.25f, 0.5f, 0.75f, 1.0f}) {
			float[] yawPitch = getYawPitchBetween(
					// source
					sourceX,
					sourceY,
					sourceZ,
					// target
					targetX,
					targetY,
					targetZ
			);
			
			if(Math.abs(yawPitch[0]) + Math.abs(yawPitch[1]) < Math.abs(bestYawPitch[0]) + Math.abs(bestYawPitch[1])) {
				bestYawPitch = yawPitch;
			}
		}
		return bestYawPitch;
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
	
	
	public HitResult getPointedBlock(float maxRange) {
		return switch(mc.crosshairTarget.getType()) {
			case BLOCK -> mc.crosshairTarget;
			case MISS -> rayTrace(maxRange);
			default -> null;
		};
	}
	
	public HitResult rayTrace(double range, Vec3d source, float pitch, float yaw) {
		if(mc.player == null) return null;
		
		float f2 = MathHelper.cos(-pitch * ((float)Math.PI / 180F) - (float)Math.PI);
		float f3 = MathHelper.sin(-pitch * ((float)Math.PI / 180F) - (float)Math.PI);
		float f4 = -MathHelper.cos(-yaw * ((float)Math.PI / 180F));
		float f5 = MathHelper.sin(-yaw * ((float)Math.PI / 180F));
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		Vec3d vector3d1 = source.add((double)f6 * range, (double)f5 * range, (double)f7 * range);
		
		return mc.world.raycast(new RaycastContext(
				source,
				vector3d1,
				RaycastContext.ShapeType.OUTLINE,
				RaycastContext.FluidHandling.NONE,
				mc.player
		));
	}
	
	private HitResult rayTrace(double range) {
		return rayTrace(
				range,
				mc.player.getRotationVec(1.0F),
				mc.player.getPitch(),
				mc.player.getYaw()
		);
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
			float distYaw = MathHelper.abs(MathHelper.wrapDegrees(yawPitch[0] - mc.player.getYaw()));
			float distPitch = MathHelper.abs(MathHelper.wrapDegrees(yawPitch[1] - mc.player.getPitch()));
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
