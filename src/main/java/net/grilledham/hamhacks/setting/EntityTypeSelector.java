package net.grilledham.hamhacks.setting;

import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.gui.element.impl.EntityTypeSelectorElement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class EntityTypeSelector extends SettingContainer<EntityType<?>, Boolean> {
	
	public final Predicate<EntityType<?>> filter;
	
	public EntityTypeSelector(String name, ShouldShow shouldShow, EntityType<?>... defaultEntities) {
		this(name, shouldShow, (type) -> true, defaultEntities);
	}
	
	public EntityTypeSelector(String name, ShouldShow shouldShow, Predicate<EntityType<?>> filter, EntityType<?>... defaultEntities) {
		super(name, shouldShow);
		
		this.filter = filter;
		
		List<EntityType<?>> defaults = new ArrayList<>();
		Collections.addAll(defaults, defaultEntities);
		for(EntityType<?> type : Registries.ENTITY_TYPE) {
			if(!filter.test(type)) {
				continue;
			}
			value.put(type, new BoolSetting(type.getTranslationKey(), defaults.contains(type), () -> true) {
				@Override
				public void onChange() {
					EntityTypeSelector.this.onChange();
				}
			});
		}
	}
	
	@Override
	public GuiElement getElement(float x, float y, double scale) {
		return new EntityTypeSelectorElement(x, y, scale, this);
	}
}
