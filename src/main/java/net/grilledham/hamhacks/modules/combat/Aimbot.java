package net.grilledham.hamhacks.modules.combat;

import com.google.common.collect.Lists;
import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.MouseUtil;
import net.grilledham.hamhacks.util.setting.settings.BoolSetting;
import net.grilledham.hamhacks.util.setting.settings.FloatSetting;
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
	
	private FloatSetting speed;
	private FloatSetting fov;
	
	private BoolSetting targetEntities;
	private BoolSetting keepAiming;
	private BoolSetting targetPlayers;
	private BoolSetting targetPassive;
	private BoolSetting targetHostile;
	private BoolSetting targetBlocks;
	
	private Entity entityToAim = null;
	private HitResult blockToAim = null;
	
	public Aimbot() {
		super(Text.translatable("hamhacks.module.aimbot"), Text.translatable("hamhacks.module.aimbot.tooltip"), Category.COMBAT, new Keybind(0));
	}
	
	@Override
	public void addSettings() {
		speed = new FloatSetting(Text.translatable("hamhacks.module.aimbot.speed"), 10.0f, 0.1f, 100f);
		fov = new FloatSetting(Text.translatable("hamhacks.module.aimbot.fov"), 90.0f, 0.1f, 360f);
		targetEntities = new BoolSetting(Text.translatable("hamhacks.module.aimbot.targetEntities"), true) {
			@Override
			protected void valueChanged() {
				super.valueChanged();
				updateSettings();
				updateScreenIfOpen();
			}
		};
		keepAiming = new BoolSetting(Text.translatable("hamhacks.module.aimbot.keepAiming"), false);
		targetPlayers = new BoolSetting(Text.translatable("hamhacks.module.aimbot.targetPlayers"), true);
		targetPassive = new BoolSetting(Text.translatable("hamhacks.module.aimbot.targetPassive"), false);
		targetHostile = new BoolSetting(Text.translatable("hamhacks.module.aimbot.targetHostile"), false);
		targetBlocks = new BoolSetting(Text.translatable("hamhacks.module.aimbot.targetBlocks"), false);
		addSetting(speed);
		addSetting(fov);
		addSetting(targetEntities);
		addSetting(keepAiming);
		addSetting(targetPlayers);
		addSetting(targetPassive);
		addSetting(targetHostile);
		addSetting(targetBlocks);
		updateSettings();
	}
	
	private void updateSettings() {
		hideSetting(keepAiming);
		hideSetting(targetPlayers);
		hideSetting(targetPassive);
		hideSetting(targetHostile);
		if(targetEntities.getValue()) {
			showSetting(targetHostile, shownSettings.indexOf(targetEntities) + 1);
			showSetting(targetPassive, shownSettings.indexOf(targetEntities) + 1);
			showSetting(targetPlayers, shownSettings.indexOf(targetEntities) + 1);
			showSetting(keepAiming, shownSettings.indexOf(targetEntities) + 1);
		}
	}
	
	@EventListener
	public void onTick(EventTick e) {
		if(mc.world == null) {
			return;
		}
		if(keepAiming.getValue()) {
			if(entityToAim != null && !MouseUtil.mouseMoved()) {
				entityToAim.getPos().floorAlongAxes(EnumSet.allOf(Direction.Axis.class)).add(0.5, 0.5, 0.5);
				float[] rotation = getRotationsNeeded(entityToAim, null, fov.getValue(), fov.getValue(), speed.getValue(), speed.getValue());
				
				mc.player.setPitch(rotation[1]);
				mc.player.setYaw(rotation[0]);
			} else if(MouseUtil.mouseMoved()) {
				if((mc.targetedEntity instanceof PlayerEntity && targetPlayers.getValue()) || (mc.targetedEntity instanceof PassiveEntity && targetPassive.getValue()) || (mc.targetedEntity instanceof HostileEntity && targetHostile.getValue())) {
					entityToAim = mc.targetedEntity;
				}
			}
		} else {
			Entity entity = getClosestEntityToCrosshair(Lists.newCopyOnWriteArrayList(mc.world.getEntities()).stream().filter(ent -> ent.getPos().distanceTo(mc.player.getPos()) < 6 && ent != mc.player/* && ent instanceof PlayerEntity*/).collect(Collectors.toList()));
			if(entity != null && targetEntities.getValue()) {
				if((entity instanceof PlayerEntity && targetPlayers.getValue()) || (entity instanceof PassiveEntity && targetPassive.getValue()) || (entity instanceof HostileEntity && targetHostile.getValue())) {
					float[] rotation = getRotationsNeeded(entity, null, fov.getValue(), fov.getValue(), speed.getValue(), speed.getValue());
					
					mc.player.setPitch(rotation[1]);
					mc.player.setYaw(rotation[0]);
				}
			}
		}
		if(targetBlocks.getValue()) {
			HitResult pos = blockToAim;
			if(pos != null && !MouseUtil.mouseMoved()) {
				pos.getPos().floorAlongAxes(EnumSet.allOf(Direction.Axis.class)).add(0.5, 0.5, 0.5);
				float[] rotation = getRotationsNeeded(null, pos.getPos(), 360, 360, speed.getValue(), speed.getValue());
				
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
	
	public float[] getRotationsNeeded(Entity target, Vec3d blockPos, float fovX, float fovY, float stepX, float stepY) {
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
		
		boolean inFovX = MathHelper.abs(MathHelper.wrapDegrees(yaw - mc.player.headYaw)) <= fovX;
		boolean inFovY = MathHelper.abs(MathHelper.wrapDegrees(pitch - mc.player.renderPitch)) <= fovY;
		
		if(inFovX && inFovY) {
			float yawFinal, pitchFinal;
			yawFinal = ((MathHelper.wrapDegrees(yaw - mc.player.headYaw)) * stepX) / 100;
			pitchFinal = ((MathHelper.wrapDegrees(pitch - mc.player.renderPitch)) * stepY) / 100;
			
			return new float[]{mc.player.headYaw + yawFinal, mc.player.renderPitch + pitchFinal};
		} else {
			return new float[]{mc.player.headYaw, mc.player.renderPitch};
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
				mc.player.renderPitch,
				mc.player.headYaw
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
