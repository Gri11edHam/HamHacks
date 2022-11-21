package net.grilledham.hamhacks.setting;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockTypeSelector extends SettingContainer<Block, Boolean> {
	
	public BlockTypeSelector(String name, ShouldShow shouldShow, Block... defaultBlocks) {
		super(name, shouldShow);
		List<Block> defaults = new ArrayList<>();
		Collections.addAll(defaults, defaultBlocks);
		for(Field field : Blocks.class.getFields()) {
			try {
				Block block = (Block)field.get(Blocks.class);
				value.put(block, new BoolSetting(block.getTranslationKey(), defaults.contains(block), () -> true) {
					@Override
					public void onChange() {
						BlockTypeSelector.this.onChange();
					}
				});
			} catch(IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
