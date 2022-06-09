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
import net.minecraft.text.Text;

public class HUD extends Module {
	
	public BoolSetting showLogo;
	public BoolSetting showFPS;
	public BoolSetting showPing;
	public BoolSetting showTPS;
	public BoolSetting showTimeSinceLastTick;
	public BoolSetting showModules;
	
	public FloatSetting heldItemScale;
	public FloatSetting shieldHeightModifier;
	public FloatSetting fireHeightModifier;
	public FloatSetting overlayTransparency;
	public BoolSetting modelBobbingOnly;
	public BoolSetting noHurtCam;
	
	public ColorSetting accentColor;
	public ColorSetting bgColor;
	public ColorSetting textColor;
	
	private static HUD INSTANCE;
	
	public HUD() {
		super(Text.translatable("module.hamhacks.hud"), Text.translatable("module.hamhacks.hud.tooltip"), Category.RENDER, new Keybind(0));
		setEnabled(true);
		INSTANCE = this;
	}
	
	public static HUD getInstance() {
		return INSTANCE;
	}
	
	@Override
	public void addSettings() {
		super.addSettings();
		showLogo = new BoolSetting(Text.translatable("setting.hud.showlogo"), true);
		showFPS = new BoolSetting(Text.translatable("setting.hud.showfps"), true);
		showPing = new BoolSetting(Text.translatable("setting.hud.showping"), true);
		showTPS = new BoolSetting(Text.translatable("setting.hud.showtps"), true);
		showTimeSinceLastTick = new BoolSetting(Text.translatable("setting.hud.showtimesincelasttick"), true);
		showModules = new BoolSetting(Text.translatable("setting.hud.showmodules"), true);
		
		heldItemScale = new FloatSetting(Text.translatable("setting.hud.helditemscale"), 1f, 0.1f, 2f);
		shieldHeightModifier = new FloatSetting(Text.translatable("setting.hud.shieldheight"), 0f, -0.5f, 0.5f);
		fireHeightModifier = new FloatSetting(Text.translatable("setting.hud.fireheight"), 0f, -0.5f, 0.5f);
		overlayTransparency = new FloatSetting(Text.translatable("setting.hud.overlaytransparency"), 1f, 0f, 1f);
		modelBobbingOnly = new BoolSetting(Text.translatable("setting.hud.modelbobbingonly"), false);
		noHurtCam = new BoolSetting(Text.translatable("setting.hud.nohurtcam"), false);
		
		accentColor = new ColorSetting(Text.translatable("setting.hud.accentcolor"), 1, 1, 1, 1, true);
		bgColor = new ColorSetting(Text.translatable("setting.hud.backgroundcolor"), 1, 0, 0, 0.5f, false);
		textColor = new ColorSetting(Text.translatable("setting.hud.textcolor"), 1, 1, 1, 1, true);
		
		addSetting(showLogo);
		addSetting(showFPS);
		addSetting(showPing);
		addSetting(showTPS);
		addSetting(showTimeSinceLastTick);
		addSetting(showModules);
		
		addSetting(heldItemScale);
		addSetting(shieldHeightModifier);
		addSetting(fireHeightModifier);
		addSetting(overlayTransparency);
		addSetting(modelBobbingOnly);
		addSetting(noHurtCam);
		
		addSetting(accentColor);
		addSetting(bgColor);
		addSetting(textColor);
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
