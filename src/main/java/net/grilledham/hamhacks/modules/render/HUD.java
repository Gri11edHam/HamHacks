package net.grilledham.hamhacks.modules.render;

import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.setting.settings.BoolSetting;
import net.grilledham.hamhacks.util.setting.settings.ColorSetting;
import net.grilledham.hamhacks.util.setting.settings.FloatSetting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.awt.*;

public class HUD extends Module {
	
	public BoolSetting showLogo;
	public BoolSetting showFPS;
	public BoolSetting showModules;
	
	public FloatSetting heldItemScale;
	public FloatSetting shieldHeightModifier;
	public FloatSetting fireHeightModifier;
	public FloatSetting overlayTransparency;
	public BoolSetting modelBobbingOnly;
	public BoolSetting noHurtCam;
	
	public ColorSetting barColor;
	public ColorSetting bgColor;
	public ColorSetting textColor;
	
	private static HUD INSTANCE;
	
	public HUD() {
		super("HUD", Category.RENDER, new Keybind(0));
		setEnabled(true);
		INSTANCE = this;
	}
	
	public static HUD getInstance() {
		return INSTANCE;
	}
	
	@Override
	public void addSettings() {
		super.addSettings();
		showLogo = new BoolSetting("Show Logo", true);
		showFPS = new BoolSetting("Show FPS", true);
		showModules = new BoolSetting("Show Enabled Modules", true);
		
		heldItemScale = new FloatSetting("Held Item Scale", 1f, 0.1f, 2f);
		shieldHeightModifier = new FloatSetting("Shield Height", 0f, -0.5f, 0.5f);
		fireHeightModifier = new FloatSetting("Fire Height", 0f, -0.5f, 0.5f);
		overlayTransparency = new FloatSetting("Overlay Transparency", 1f, 0f, 1f);
		modelBobbingOnly = new BoolSetting("Model Bobbing Only", false);
		noHurtCam = new BoolSetting("No Hurt Cam", false);
		
		float[] barHSB = Color.RGBtoHSB(0xa4, 0, 0, new float[3]);
		barColor = new ColorSetting("Bar Color", barHSB[0], barHSB[1], barHSB[2], 1, true);
		bgColor = new ColorSetting("Background Color", 1, 0, 0, 0.5f, false);
		textColor = new ColorSetting("Text Color", 1, 1, 1, 1, true);
		
		settings.add(showLogo);
		settings.add(showFPS);
		settings.add(showModules);
		
		settings.add(heldItemScale);
		settings.add(shieldHeightModifier);
		settings.add(fireHeightModifier);
		settings.add(overlayTransparency);
		settings.add(modelBobbingOnly);
		settings.add(noHurtCam);
		
		settings.add(barColor);
		settings.add(bgColor);
		settings.add(textColor);
	}
	
	public void applyHandTransform(LivingEntity entity, ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		if(isEnabled()) {
			if(entity == mc.getCameraEntity() && mc.options.getPerspective().isFirstPerson()) {
				matrices.scale(HUD.getInstance().heldItemScale.getValue(), HUD.getInstance().heldItemScale.getValue(), HUD.getInstance().heldItemScale.getValue());
				if(stack.getItem() == Items.SHIELD) {
					matrices.translate(0, shieldHeightModifier.getValue(), 0);
				}
			}
		}
	}
	
	public void applyFireTransform(MatrixStack matrices) {
		if(isEnabled()) {
			matrices.translate(0, fireHeightModifier.getValue(), 0);
		}
	}
	
	public float getOverlayTransparency(float original) {
		return isEnabled() ? (overlayTransparency.getValue() * original) : original;
	}
}
