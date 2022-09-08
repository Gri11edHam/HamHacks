package net.grilledham.hamhacks.modules.render;

import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.util.Color;
import net.grilledham.hamhacks.util.setting.BoolSetting;
import net.grilledham.hamhacks.util.setting.ColorSetting;
import net.grilledham.hamhacks.util.setting.NumberSetting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class HUD extends Module {
	
	@BoolSetting(name = "hamhacks.module.hud.showLogo", defaultValue = true)
	public boolean showLogo = true;
	
	@BoolSetting(name = "hamhacks.module.hud.showFps", defaultValue = true)
	public boolean showFPS = true;
	
	@BoolSetting(name = "hamhacks.module.hud.showPing", defaultValue = true)
	public boolean showPing = true;
	
	@BoolSetting(name = "hamhacks.module.hud.showTps", defaultValue = true)
	public boolean showTPS = true;
	
	@BoolSetting(name = "hamhacks.module.hud.showTimeSinceLastTick", defaultValue = true)
	public boolean showTimeSinceLastTick = true;
	
	@BoolSetting(name = "hamhacks.module.hud.showModules", defaultValue = true)
	public boolean showModules = true;
	
	@BoolSetting(name = "hamhacks.module.hud.animate", defaultValue = true)
	public boolean animate = true;
	
	@NumberSetting(
			name = "hamhacks.module.hud.heldItemScale",
			defaultValue = 1,
			min = 0.1f,
			max = 2
	)
	public float heldItemScale = 1;
	
	@NumberSetting(
			name = "hamhacks.module.hud.shieldHeight",
			min = -0.5f,
			max = 0.5f
	)
	public float shieldHeightModifier = 0;
	
	@NumberSetting(
			name = "hamhacks.module.hud.fireHeight",
			min = -0.5f,
			max = 0.5f
	)
	public float fireHeightModifier = 0;
	
	@NumberSetting(
			name = "hamhacks.module.hud.overlayTransparency",
			defaultValue = 1,
			min = 0,
			max = 1
	)
	public float overlayTransparency = 1;
	
	@BoolSetting(name = "hamhacks.module.hud.modelBobbingOnly")
	public boolean modelBobbingOnly = false;
	
	@BoolSetting(name = "hamhacks.module.hud.noHurtCam")
	public boolean noHurtCam = false;
	
	@ColorSetting(name = "hamhacks.module.hud.accentColor")
	public Color accentColor = new Color(1, 1, 1, 1, true);
	
	@ColorSetting(name = "hamhacks.module.hud.backgroundColor")
	public Color bgColor = new Color(0, 0, 0, 0.5f);
	
	@ColorSetting(name = "hamhacks.module.hud.textColor")
	public Color textColor = new Color(1, 1, 1, 1, true);
	
	private static HUD INSTANCE;
	
	public HUD() {
		super(Text.translatable("hamhacks.module.hud"), Category.RENDER, new Keybind(0));
		setEnabled(true);
		showModule = false;
		INSTANCE = this;
	}
	
	public static HUD getInstance() {
		return INSTANCE;
	}
	
	@Override
	public String getHUDText() {
		return super.getHUDText() + " \u00a77" + ModuleManager.getModules().stream().filter(Module::isEnabled).toList().size() + "|" + ModuleManager.getModules().size();
	}
	
	public void applyHandTransform(LivingEntity entity, ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		if(isEnabled()) {
			if(entity == mc.getCameraEntity() && mc.options.getPerspective().isFirstPerson()) {
				matrices.scale(HUD.getInstance().heldItemScale, HUD.getInstance().heldItemScale, HUD.getInstance().heldItemScale);
				if(stack.getItem() == Items.SHIELD) {
					matrices.translate(0, shieldHeightModifier, 0);
				}
			}
		}
	}
	
	public void applyFireTransform(MatrixStack matrices) {
		if(isEnabled()) {
			matrices.translate(0, fireHeightModifier, 0);
		}
	}
	
	public float getOverlayTransparency(float original) {
		return isEnabled() ? (overlayTransparency * original) : original;
	}
}
