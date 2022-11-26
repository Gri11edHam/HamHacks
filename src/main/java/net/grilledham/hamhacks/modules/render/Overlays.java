package net.grilledham.hamhacks.modules.render;

import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class Overlays extends Module {
	
	public final NumberSetting shieldHeightModifier = new NumberSetting("hamhacks.module.overlays.shieldHeight", 0, () -> true, -0.5, 0.5);

	public final NumberSetting fireHeightModifier = new NumberSetting("hamhacks.module.overlays.fireHeight", 0, () -> true, -0.5, 0.5);

	public final NumberSetting overlayTransparency = new NumberSetting("hamhacks.module.overlays.transparency", 1, () -> true, 0, 1);
	
	public Overlays() {
		super(Text.translatable("hamhacks.module.overlays"), Category.RENDER, new Keybind());
		GENERAL_CATEGORY.add(shieldHeightModifier);
		GENERAL_CATEGORY.add(fireHeightModifier);
		GENERAL_CATEGORY.add(overlayTransparency);
	}
	
	public void applyShieldTransform(LivingEntity entity, ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		if(isEnabled()) {
			if(entity == mc.getCameraEntity() && mc.options.getPerspective().isFirstPerson()) {
				if(stack.getItem() == Items.SHIELD) {
					matrices.translate(0, shieldHeightModifier.get(), 0);
				}
			}
		}
	}
	
	public void applyFireTransform(MatrixStack matrices) {
		if(isEnabled()) {
			matrices.translate(0, fireHeightModifier.get(), 0);
		}
	}
	
	public double getOverlayTransparency(double original) {
		return isEnabled() ? (overlayTransparency.get() * original) : original;
	}
}
