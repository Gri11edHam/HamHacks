package net.grilledham.hamhacks.setting;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockTypeSelector extends SettingContainer<Block, Boolean> {
	
	public BlockTypeSelector(String name, ShouldShow shouldShow, Block... defaultBlocks) {
		super(name, shouldShow);
		List<Block> defaults = new ArrayList<>();
		Collections.addAll(defaults, defaultBlocks);
		for(Block block : Registries.BLOCK) {
			value.put(block, new BoolSetting(block.getTranslationKey(), defaults.contains(block), () -> true) {
				@Override
				public void onChange() {
					BlockTypeSelector.this.onChange();
				}
			});
		}
	}
}
