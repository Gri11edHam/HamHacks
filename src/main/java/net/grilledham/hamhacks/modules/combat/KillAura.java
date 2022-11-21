package net.grilledham.hamhacks.modules.combat;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventMotion;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.BoolSetting;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.grilledham.hamhacks.util.RotationHack;
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
	
	private final NumberSetting range = new NumberSetting("hamhacks.module.killAura.range", 3, () -> true, 1, 8);
	
	private final BoolSetting targetPlayers = new BoolSetting("hamhacks.module.killAura.targetPlayers", true, () -> true);
	
	private final BoolSetting targetPassive = new BoolSetting("hamhacks.module.killAura.targetPassive", false, () -> true);
	
	private final BoolSetting targetHostile = new BoolSetting("hamhacks.module.killAura.targetHostile", false, () -> true);
	
	private LivingEntity target;
	
	public KillAura() {
		super(Text.translatable("hamhacks.module.killAura"), Category.COMBAT, new Keybind(GLFW.GLFW_KEY_R));
		GENERAL_CATEGORY.add(range);
		GENERAL_CATEGORY.add(targetPlayers);
		GENERAL_CATEGORY.add(targetPassive);
		GENERAL_CATEGORY.add(targetHostile);
	}
	
	@Override
	public String getHUDText() {
		return super.getHUDText() + " \u00a77" + String.format("%.2f", range.get());
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
					.filter(entity -> (entity instanceof PlayerEntity && targetPlayers.get()) || (entity instanceof HostileEntity && targetHostile.get()) || (entity instanceof PassiveEntity && targetPassive.get()));
			
			List<LivingEntity> entities = stream.toList();
			if(!entities.isEmpty()) {
				LivingEntity closest = entities.get(0);
				for(LivingEntity entity : entities) {
					if(mc.player.distanceTo(entity) < mc.player.distanceTo(closest)) {
						closest = entity;
					}
				}
				if(mc.player.distanceTo(closest) <= range.get()) {
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
