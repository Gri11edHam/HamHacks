package net.grilledham.hamhacks.modules.combat;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventMotion;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.RotationHack;
import net.grilledham.hamhacks.util.setting.BoolSetting;
import net.grilledham.hamhacks.util.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class KillAura extends Module {
	
	@NumberSetting(
			name = "hamhacks.module.killAura.range",
			defaultValue = 3,
			min = 1,
			max = 8
	)
	public float range = 3;
	
	@BoolSetting(
			name = "hamhacks.module.killAura.targetPlayers",
			defaultValue = true
	)
	public boolean targetPlayers = true;
	
	@BoolSetting(name = "hamhacks.module.killAura.targetPassive")
	public boolean targetPassive = false;
	
	@BoolSetting(name = "hamhacks.module.killAura.targetHostile")
	public boolean targetHostile = false;
	
	private LivingEntity target;
	
	public KillAura() {
		super(Text.translatable("hamhacks.module.killAura"), Category.COMBAT, new Keybind(GLFW.GLFW_KEY_R));
	}
	
	@Override
	public String getHUDText() {
		return super.getHUDText() + " \u00a77" + String.format("%.2f", range);
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
					.filter(entity -> (entity instanceof PlayerEntity && targetPlayers) || (entity instanceof HostileEntity && targetHostile) || (entity instanceof PassiveEntity && targetPassive));
			
			List<LivingEntity> entities = stream.toList();
			if(!entities.isEmpty()) {
				LivingEntity closest = entities.get(0);
				for(LivingEntity entity : entities) {
					if(mc.player.distanceTo(entity) < mc.player.distanceTo(closest)) {
						closest = entity;
					}
				}
				if(mc.player.distanceTo(closest) <= range) {
					target = closest;
				} else {
					target = null;
				}
			} else {
				target = null;
			}
		}
		if(target != null) {
			RotationHack.faceVectorPacket(target.getEyePos());
		}
	}
	
	@EventListener
	public void onMove(EventMotion e) {
		if(e.type == EventMotion.Type.POST) {
			if(target == null) {
				return;
			}
			
			mc.interactionManager.attackEntity(mc.player, target);
			mc.player.swingHand(Hand.MAIN_HAND);
			target = null;
		}
	}
}
