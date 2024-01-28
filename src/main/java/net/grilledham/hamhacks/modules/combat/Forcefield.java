package net.grilledham.hamhacks.modules.combat;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.RotationHack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Forcefield extends Module {
	
	public Forcefield() {
		super(Text.translatable("hamhacks.module.forcefield"), Category.COMBAT, new Keybind());
	}
	
	@EventListener
	public void onTick(EventTick e) {
		if(mc.world == null) {
			return;
		}
		for(HostileEntity entity : mc.world.getEntitiesByType(new TypeFilter<Entity, HostileEntity>() {
					@Nullable
					@Override
					public HostileEntity downcast(Entity entity) {
						return (HostileEntity)entity;
					}
					
					@Override
					public Class<? extends Entity> getBaseClass() {
						return HostileEntity.class;
					}
				}, new Box(mc.player.getBlockPos().add(-256, -256, -256).toCenterPos(), mc.player.getBlockPos().add(256, 256, 256).toCenterPos()), Objects::nonNull).stream()
				.filter(entity -> !entity.isRemoved() && entity.isAlive())
				.filter(entity -> Math.abs(entity.getY() - mc.player.getY()) <= 1e6).toList()) {
			if(entity.hurtTime <= 0 && entity.distanceTo(mc.player) <= 3) {
				RotationHack.faceVectorPacket(entity.getEyePos());
				
				mc.interactionManager.attackEntity(mc.player, entity);
				mc.player.swingHand(Hand.MAIN_HAND);
			}
		}
	}
}
