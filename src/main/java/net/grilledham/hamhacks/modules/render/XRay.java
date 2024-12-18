package net.grilledham.hamhacks.modules.render;

import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.BlockTypeSelector;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class XRay extends Module {
	
	public BlockTypeSelector visibleBlocks = new BlockTypeSelector(
			"hamhacks.module.xray.visibleBlocks",
			() -> true,
			Blocks.COAL_ORE,
			Blocks.DEEPSLATE_COAL_ORE,
			Blocks.COAL_BLOCK,
			Blocks.IRON_ORE,
			Blocks.DEEPSLATE_IRON_ORE,
			Blocks.RAW_IRON_BLOCK,
			Blocks.IRON_BLOCK,
			Blocks.GOLD_ORE,
			Blocks.DEEPSLATE_GOLD_ORE,
			Blocks.RAW_GOLD_BLOCK,
			Blocks.GOLD_BLOCK,
			Blocks.LAPIS_ORE,
			Blocks.DEEPSLATE_LAPIS_ORE,
			Blocks.LAPIS_BLOCK,
			Blocks.REDSTONE_ORE,
			Blocks.DEEPSLATE_REDSTONE_ORE,
			Blocks.REDSTONE_BLOCK,
			Blocks.DIAMOND_ORE,
			Blocks.DEEPSLATE_DIAMOND_ORE,
			Blocks.DIAMOND_BLOCK,
			Blocks.EMERALD_ORE,
			Blocks.DEEPSLATE_EMERALD_ORE,
			Blocks.EMERALD_BLOCK,
			Blocks.COPPER_ORE,
			Blocks.DEEPSLATE_COPPER_ORE,
			Blocks.RAW_COPPER_BLOCK,
			Blocks.NETHER_GOLD_ORE,
			Blocks.NETHER_QUARTZ_ORE,
			Blocks.ANCIENT_DEBRIS,
			Blocks.NETHERITE_BLOCK,
			Blocks.BEDROCK,
			Blocks.WATER,
			Blocks.LAVA,
			Blocks.END_PORTAL_FRAME,
			Blocks.END_PORTAL,
			Blocks.NETHER_PORTAL,
			Blocks.OBSIDIAN,
			Blocks.CRYING_OBSIDIAN
	) {
		@Override
		public void onChange() {
			if(isEnabled()) {
				mc.worldRenderer.reload();
			}
		}
	};
	
	public XRay() {
		super(Text.translatable("hamhacks.module.xray"), Category.RENDER, new Keybind(0));
		GENERAL_CATEGORY.add(visibleBlocks);
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		mc.worldRenderer.reload();
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		mc.worldRenderer.reload();
	}
	
	public boolean shouldDrawSide(BlockState selfState, BlockState otherState, Direction facing, boolean returnValue) {
		if(!returnValue && visibleBlocks.get(selfState.getBlock())) {
			return otherState.getCullingFace(facing.getOpposite()) != VoxelShapes.fullCube() || otherState.getBlock() != selfState.getBlock();
		}
		return returnValue;
	}
	
	public boolean shouldCullSide(BlockState selfState, BlockView view, BlockPos pos, Direction facing, boolean returnValue) {
		if(visibleBlocks.get(selfState.getBlock())) {
			return returnValue;
		}
		return true;
	}
}
