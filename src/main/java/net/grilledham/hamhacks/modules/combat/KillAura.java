package net.grilledham.hamhacks.modules.combat;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventMotion;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.RotationHack;
import net.grilledham.hamhacks.util.setting.settings.BoolSetting;
import net.grilledham.hamhacks.util.setting.settings.FloatSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class KillAura extends Module {
	
	private FloatSetting range;
	private BoolSetting targetPlayers;
	private BoolSetting targetPassive;
	private BoolSetting targetHostile;
	
	private LivingEntity target;
	
	public KillAura() {
		super(new TranslatableText("module.hamhacks.killaura"), new TranslatableText("module.hamhacks.killaura.tooltip"), Category.COMBAT, new Keybind(GLFW.GLFW_KEY_R));
	}
	
	@Override
	public void addSettings() {
		super.addSettings();
		range = new FloatSetting(new TranslatableText("setting.killaura.range"), 3f, 1f, 8f);
		targetPlayers = new BoolSetting(new TranslatableText("setting.killaura.targetplayers"), true);
		targetPassive = new BoolSetting(new TranslatableText("setting.killaura.targetpassive"), false);
		targetHostile = new BoolSetting(new TranslatableText("setting.killaura.targethostile"), false);
		addSetting(range);
		addSetting(targetPlayers);
		addSetting(targetPassive);
		addSetting(targetHostile);
	}
	
	@EventListener
	public void onTick(EventTick e) {
		if(mc.world == null) {
			return;
		}
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
					.filter(entity -> (entity instanceof PlayerEntity && targetPlayers.getValue()) || (entity instanceof HostileEntity && targetHostile.getValue()) || (entity instanceof PassiveEntity && targetPassive.getValue()));
			
			List<LivingEntity> entities = stream.toList();
			if(!entities.isEmpty()) {
				LivingEntity closest = entities.get(0);
				for(LivingEntity entity : entities) {
					if(mc.player.distanceTo(entity) < mc.player.distanceTo(closest)) {
						closest = entity;
					}
				}
				if(mc.player.distanceTo(closest) <= range.getValue()) {
					target = closest;
				} else {
					target = null;
				}
			} else {
				target = null;
			}
			if(target != null) {
				RotationHack.faceVectorPacket(target.getEyePos(), (int)mc.player.getAttackCooldownProgressPerTick());
			}
		}
	}
	
	@EventListener
	public void onMove(EventMotion e) {
		if(((EventMotion)e).type == EventMotion.Type.POST) {
			if(target == null) {
				return;
			}
			
			mc.interactionManager.attackEntity(mc.player, target);
			mc.player.swingHand(Hand.MAIN_HAND);
			target = null;
		}
	}
}
