package net.grilledham.hamhacks.modules.render;

import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.Setting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class HUD extends Module {
	
	public Setting showLogo;
	public Setting showFPS;
	public Setting showModules;
	
	public Setting heldItemScale;
	public Setting shieldHeightModifier;
	public Setting fireHeightModifier;
	public Setting overlayTransparency;
	public Setting modelBobbingOnly;
	public Setting noHurtCam;
	
	public Setting barColor;
	public Setting bgColor;
	public Setting textColor;
	
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
		showLogo = new Setting("Show Logo", true);
		showFPS = new Setting("Show FPS", true);
		showModules = new Setting("Show Enabled Modules", true);
		
		heldItemScale = new Setting("Held Item Scale", 1f, 0.1f, 2f);
		shieldHeightModifier = new Setting("Shield Height", 0f, -0.5f, 0.5f);
		fireHeightModifier = new Setting("Fire Height", 0f, -0.5f, 0.5f);
		overlayTransparency = new Setting("Overlay Transparency", 1f, 0f, 1f);
		modelBobbingOnly = new Setting("Model Bobbing Only", false);
		noHurtCam = new Setting("No Hurt Cam", false);
		
		barColor = new Setting("Bar Color", 0xff00a400);
		barColor.setChroma(true);
		bgColor = new Setting("Background Color", 0x80000000);
		textColor = new Setting("Text Color", 0xffffffff);
		textColor.setChroma(true);
		
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
				matrices.scale(HUD.getInstance().heldItemScale.getFloat(), HUD.getInstance().heldItemScale.getFloat(), HUD.getInstance().heldItemScale.getFloat());
				if(stack.getItem() == Items.SHIELD) {
					matrices.translate(0, shieldHeightModifier.getFloat(), 0);
				}
			}
		}
	}
	
	public void applyFireTransform(MatrixStack matrices) {
		if(isEnabled()) {
			matrices.translate(0, fireHeightModifier.getFloat(), 0);
		}
	}
	
	public float getOverlayTransparency(float original) {
		return isEnabled() ? (overlayTransparency.getFloat() * original) : original;
	}
}
