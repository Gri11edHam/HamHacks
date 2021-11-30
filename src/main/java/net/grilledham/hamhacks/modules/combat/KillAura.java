package net.grilledham.hamhacks.modules.combat;

import net.grilledham.hamhacks.event.Event;
import net.grilledham.hamhacks.event.EventMotion;
import net.grilledham.hamhacks.event.EventTick;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.Setting;
import net.grilledham.hamhacks.util.RotationHack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class KillAura extends Module {
	
	private Setting reach;
	private Setting attackPlayers;
	private Setting attackPassive;
	private Setting attackHostile;
	
	private LivingEntity target;
	
	public KillAura() {
		super("Kill Aura", Category.COMBAT, new Keybind(GLFW.GLFW_KEY_R));
	}
	
	@Override
	public void addSettings() {
		super.addSettings();
		reach = new Setting("Reach", 3f, 1f, 8f);
		attackPlayers = new Setting("Attack Players", true);
		attackPassive = new Setting("Attack Passive Mobs", false);
		attackHostile = new Setting("Attack Hostile Mobs", false);
		settings.add(reach);
		settings.add(attackPlayers);
		settings.add(attackPassive);
		settings.add(attackHostile);
	}
	
	@Override
	public boolean onEvent(Event e) {
		boolean superReturn = super.onEvent(e);
		if(superReturn) {
			if(e instanceof EventTick) {
				if(mc.player.getAttackCooldownProgress(0.5f) > 0.9f) {
					Stream<LivingEntity> stream = mc.world.getEntitiesByType(new TypeFilter<Entity, LivingEntity>() {
								@Nullable
								@Override
								public LivingEntity downcast(Entity entity) {
									return (LivingEntity)entity;
								}
								
								@Override
								public Class<? extends Entity> getBaseClass() {
									return LivingEntity.class;
								}
							}, new Box(mc.player.getBlockPos().add(-64, -64, -64), mc.player.getBlockPos().add(64, 64, 64)), Objects::nonNull).stream()
							.filter(entity -> !entity.isRemoved() && entity.isAlive())
							.filter(entity -> entity != mc.player)
							.filter(entity -> Math.abs(entity.getY() - mc.player.getY()) <= 1e6)
							.filter(entity -> (entity instanceof PlayerEntity && attackPlayers.getBool()) || (entity instanceof HostileEntity && attackHostile.getBool()) || (entity instanceof PassiveEntity && attackPassive.getBool()));
					
					List<LivingEntity> entities = stream.toList();
					if(!entities.isEmpty()) {
						LivingEntity closest = entities.get(0);
						for(LivingEntity entity : entities) {
							if(mc.player.distanceTo(entity) < mc.player.distanceTo(closest)) {
								closest = entity;
							}
						}
						if(mc.player.distanceTo(closest) <= reach.getFloat()) {
							target = closest;
						} else {
							target = null;
						}
					} else {
						target = null;
					}
					if(target != null) {
						RotationHack.faceVectorPacket(target.getPos());
					}
				}
			} else if(e instanceof EventMotion) {
				if(((EventMotion)e).type == EventMotion.Type.POST) {
					if(target == null) {
						return true;
					}
					
					mc.interactionManager.attackEntity(mc.player, target);
					mc.player.swingHand(Hand.MAIN_HAND);
					target = null;
				}
			}
		}
		return superReturn;
	}
}
