package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.render.XRay;
import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SideShapeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlockState.class)
public abstract class MixinAbstractBlockState {
	
	@Shadow public abstract Block getBlock();
	
	@Inject(method = "isSideInvisible", at = @At("HEAD"), cancellable = true)
	public void isSideInvisible(BlockState state, Direction direction, CallbackInfoReturnable<Boolean> cir) {
		XRay xRay = ModuleManager.getModule(XRay.class);
		if(xRay.isEnabled()) {
			cir.setReturnValue(!xRay.visibleBlocks.get(getBlock()));
		}
	}
	
	@Inject(method = "isSideSolid", at = @At("HEAD"), cancellable = true)
	public void isSideSolid(BlockView world, BlockPos pos, Direction direction, SideShapeType shapeType, CallbackInfoReturnable<Boolean> cir) {
		XRay xRay = ModuleManager.getModule(XRay.class);
		if(xRay.isEnabled()) {
			cir.setReturnValue(xRay.visibleBlocks.get(getBlock()));
		}
	}
	
	@Inject(method = "getCullingFace", at = @At("HEAD"), cancellable = true)
	public void getCullingFace(BlockView world, BlockPos pos, Direction direction, CallbackInfoReturnable<VoxelShape> cir) {
		XRay xRay = ModuleManager.getModule(XRay.class);
		if(xRay.isEnabled()) {
			if(xRay.visibleBlocks.get(getBlock())) {
				cir.setReturnValue(VoxelShapes.fullCube());
			} else {
				cir.setReturnValue(VoxelShapes.empty());
			}
		}
	}
	
	@Inject(method = "getAmbientOcclusionLightLevel", at = @At("HEAD"), cancellable = true)
	public void getAmbientOcclusionLightLevel(BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
		XRay xRay = ModuleManager.getModule(XRay.class);
		if(xRay.isEnabled()) {
			if(xRay.visibleBlocks.get(getBlock())) {
				cir.setReturnValue(1F);
			}
		}
	}
	
	@Inject(method = "getLuminance", at = @At("HEAD"), cancellable = true)
	public void getLuminance(CallbackInfoReturnable<Integer> cir) {
		XRay xRay = ModuleManager.getModule(XRay.class);
		if(xRay.isEnabled()) {
			if(xRay.visibleBlocks.get(getBlock())) {
				cir.setReturnValue(12);
			}
		}
	}
}
