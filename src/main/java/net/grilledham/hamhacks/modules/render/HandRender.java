package net.grilledham.hamhacks.modules.render;

import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.text.Text;

public class HandRender extends Module {
	
	public final NumberSetting fovMultiplier = new NumberSetting("hamhacks.module.handRender.fovMultiplier", 1, () -> true, 0.75, 2, 0.05);
	public final NumberSetting itemScale = new NumberSetting("hamhacks.module.handRender.itemScale", 1, () -> true, 0.25, 2, 0.05);
	
	public HandRender() {
		super(Text.translatable("hamhacks.module.handRender"), Category.RENDER, new Keybind());
		GENERAL_CATEGORY.add(fovMultiplier);
		GENERAL_CATEGORY.add(itemScale);
	}
	
	public void applyItemTransform(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		if(isEnabled()) {
			if(entity == mc.getCameraEntity() && mc.options.getPerspective().isFirstPerson()) {
				float scale = itemScale.get().floatValue();
				matrices.scale(scale, scale, scale);
			}
		}
	}
}
