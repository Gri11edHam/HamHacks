package net.grilledham.hamhacks.modules.combat;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventMotion;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.EntityTypeSelector;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.grilledham.hamhacks.util.RotationHack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class KillAura extends Module {
	
	private final NumberSetting range = new NumberSetting("hamhacks.module.killAura.range", 3, () -> true, 1, 6);
	
	private final EntityTypeSelector entitySelector = new EntityTypeSelector("hamhacks.module.killAura.entitySelector", () -> true,
			type -> type != EntityType.AREA_EFFECT_CLOUD
					&& type != EntityType.ARROW
					&& type != EntityType.FALLING_BLOCK
					&& type != EntityType.FIREWORK_ROCKET
					&& type != EntityType.ITEM
					&& type != EntityType.LLAMA_SPIT
					&& type != EntityType.SPECTRAL_ARROW
					&& type != EntityType.ENDER_PEARL
					&& type != EntityType.EXPERIENCE_BOTTLE
					&& type != EntityType.TRIDENT
					&& type != EntityType.LIGHTNING_BOLT
					&& type != EntityType.FISHING_BOBBER
					&& type != EntityType.EXPERIENCE_ORB
					&& type != EntityType.EGG,
			EntityType.PLAYER);
	
	private Entity target;
	
	public KillAura() {
		super(Text.translatable("hamhacks.module.killAura"), Category.COMBAT, new Keybind(GLFW.GLFW_KEY_R));
		GENERAL_CATEGORY.add(range);
		GENERAL_CATEGORY.add(entitySelector);
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
			Stream<Entity> stream = mc.world.getEntitiesByType(TypeFilter.instanceOf(Entity.class), new Box(mc.player.getBlockPos().add(-64, -64, -64).toCenterPos(), mc.player.getBlockPos().add(64, 64, 64).toCenterPos()), Objects::nonNull).stream()
					.filter(entity -> !entity.isRemoved() && entity.isAlive())
					.filter(entity -> entity != mc.player)
					.filter(entity -> Math.abs(entity.getY() - mc.player.getY()) <= 1e6)
					.filter(Entity::canHit)
					.filter(entity -> entitySelector.get(entity.getType()));
			
			List<Entity> entities = stream.toList();
			if(!entities.isEmpty()) {
				Entity closest = entities.get(0);
				for(Entity entity : entities) {
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
