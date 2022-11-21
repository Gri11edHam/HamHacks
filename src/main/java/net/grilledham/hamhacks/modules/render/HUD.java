package net.grilledham.hamhacks.modules.render;

import net.grilledham.hamhacks.animation.Animation;
import net.grilledham.hamhacks.animation.AnimationBuilder;
import net.grilledham.hamhacks.animation.AnimationType;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.setting.BoolSetting;
import net.grilledham.hamhacks.setting.ColorSetting;
import net.grilledham.hamhacks.setting.SettingCategory;
import net.grilledham.hamhacks.setting.StringSetting;
import net.grilledham.hamhacks.util.ChatUtil;
import net.grilledham.hamhacks.util.Color;
import net.grilledham.hamhacks.util.ConnectionUtil;
import net.grilledham.hamhacks.util.RenderUtil;
import net.grilledham.hamhacks.util.math.DirectionHelper;
import net.grilledham.hamhacks.util.math.Vec3;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HUD extends Module {
	
	private final SettingCategory APPEARANCE_CATEGORY = new SettingCategory("hamhacks.module.hud.category.appearance");
	
	private final BoolSetting animate = new BoolSetting("hamhacks.module.hud.animate", true, () -> true);
	
	private final ColorSetting accentColor = new ColorSetting("hamhacks.module.hud.accentColor", new Color(1, 1, 1, 1, true), () -> true);
	
	private final ColorSetting bgColor = new ColorSetting("hamhacks.module.hud.backgroundColor", new Color(0x80000000), () -> true);
	
	private final ColorSetting textColor = new ColorSetting("hamhacks.module.hud.textColor", Color.getWhite(), () -> true);
	
	private final SettingCategory ELEMENTS_CATEGORY = new SettingCategory("hamhacks.module.hud.category.elements");
	
	private final BoolSetting showLogo = new BoolSetting("hamhacks.module.hud.showLogo", true, () -> true);
	
	private final StringSetting logoText = new StringSetting("hamhacks.module.hud.logoText", "", showLogo::get, "&4&l&oHamHacks");
	
	private final BoolSetting showFPS = new BoolSetting("hamhacks.module.hud.showFps", true, () -> true);
	
	private final BoolSetting showPing = new BoolSetting("hamhacks.module.hud.showPing", true, () -> true);
	
	private final BoolSetting showTPS = new BoolSetting("hamhacks.module.hud.showTps", true, () -> true);
	
	private final BoolSetting showTimeSinceLastTick = new BoolSetting("hamhacks.module.hud.showTimeSinceLastTick", true, () -> true);
	
	private final BoolSetting showModules = new BoolSetting("hamhacks.module.hud.showModules", true, () -> true);
	
	private final BoolSetting showCoordinates = new BoolSetting("hamhacks.module.hud.showCoordinates", true, () -> true);
	
	private final BoolSetting showDirection = new BoolSetting("hamhacks.module.hud.showDirection", true, () -> true);
	
	private final BoolSetting directionYawPitch = new BoolSetting("hamhacks.module.hud.directionYawPitch", false, showDirection::get);
	
//	@NumberSetting( // TODO: Move to module
//			name = "hamhacks.module.hud.heldItemScale",
//			defaultValue = 1,
//			min = 0.1f,
//			max = 2, category = "hamhacks.module.hud.category.other"
//	)
//	public float heldItemScale = 1;
	
//	@NumberSetting( // TODO: Move to module + fireHeight + overlayTransparency
//			name = "hamhacks.module.hud.shieldHeight",
//			min = -0.5f,
//			max = 0.5f, category = "hamhacks.module.hud.category.other"
//	)
//	public float shieldHeightModifier = 0;
//
//	@NumberSetting(
//			name = "hamhacks.module.hud.fireHeight",
//			min = -0.5f,
//			max = 0.5f, category = "hamhacks.module.hud.category.other"
//	)
//	public float fireHeightModifier = 0;
//
//	@NumberSetting(
//			name = "hamhacks.module.hud.overlayTransparency",
//			defaultValue = 1,
//			min = 0,
//			max = 1, category = "hamhacks.module.hud.category.other"
//	)
//	public float overlayTransparency = 1;
	
//	@BoolSetting(name = "hamhacks.module.hud.modelBobbingOnly", category = "hamhacks.module.hud.category.other") // TODO: Move to module
//	public boolean modelBobbingOnly = false;
	
//	@BoolSetting(name = "hamhacks.module.hud.noHurtCam", category = "hamhacks.module.hud.category.other") // TODO: Move to module
//	public boolean noHurtCam = false;
	
	public HUD() {
		super(Text.translatable("hamhacks.module.hud"), Category.RENDER, new Keybind(0));
		setEnabled(true);
		showModule.set(true);
		settingCategories.add(0, APPEARANCE_CATEGORY);
		APPEARANCE_CATEGORY.add(animate);
		APPEARANCE_CATEGORY.add(accentColor);
		APPEARANCE_CATEGORY.add(bgColor);
		APPEARANCE_CATEGORY.add(textColor);
		settingCategories.add(1, ELEMENTS_CATEGORY);
		ELEMENTS_CATEGORY.add(showLogo);
		ELEMENTS_CATEGORY.add(logoText);
		ELEMENTS_CATEGORY.add(showFPS);
		ELEMENTS_CATEGORY.add(showPing);
		ELEMENTS_CATEGORY.add(showTPS);
		ELEMENTS_CATEGORY.add(showTimeSinceLastTick);
		ELEMENTS_CATEGORY.add(showModules);
		ELEMENTS_CATEGORY.add(showCoordinates);
		ELEMENTS_CATEGORY.add(showDirection);
		ELEMENTS_CATEGORY.add(directionYawPitch);
	}
	
	@Override
	public String getHUDText() {
		return super.getHUDText() + " \u00a77" + ModuleManager.getModules().stream().filter(Module::isEnabled).toList().size() + "|" + ModuleManager.getModules().size();
	}
	
	private final List<Animation> animations = new ArrayList<>();
	
	public float leftHeight = 0;
	public float rightHeight = 0;
	
	@Override
	public void onEnable() {
		// don't register
	}
	
	@Override
	public void onDisable() {
		// don't unregister
	}
	
	public void render(MatrixStack matrices, float tickDelta, TextRenderer textRenderer) {
		if(MinecraftClient.getInstance().options.debugEnabled) {
			return;
		}
		
		matrices.push();
		
		float[] textC = textColor.get().getHSB();
		
		int j = 0;
		int i = 0;
		float yAdd = 0;
		Animation animation = getAnimation(j++);
		if(animate.get()) {
			animation.set(showLogo.get() && isEnabled());
		} else {
			animation.setAbsolute(showLogo.get() && isEnabled());
		}
		if(animation.get() > 0) {
			float finalTextHue;
			if(textColor.get().getChroma()) {
				finalTextHue = (textC[0] - (i * 0.025f)) % 1f;
			} else {
				finalTextHue = textC[0];
			}
			int textColor = Color.toRGB(finalTextHue, textC[1], textC[2], textC[3]);
			String text = logoText.get().equals("") ? "§4§l§oHamHacks" : ChatUtil.format(logoText.get());
			float textX = 2;
			float textY = 2;
			matrices.push();
			matrices.translate(textX, textY, 0);
			matrices.scale(2, 2, 1);
			matrices.translate(-textX, -textY, 0);
			textRenderer.drawWithShadow(matrices, text, textX - (int)(textRenderer.getWidth(text) * (1 - animation.get())), textY, textColor);
			matrices.pop();
			yAdd += ((textRenderer.fontHeight * 2) + 4) * animation.get();
			i++;
		}
		animation = getAnimation(j++);
		if(animate.get()) {
			animation.set(showFPS.get() && isEnabled());
		} else {
			animation.setAbsolute(showFPS.get() && isEnabled());
		}
		if(animation.get() > 0) {
			String fps = MinecraftClient.getInstance().fpsDebugString;
			fps = fps.split(" ")[0] + " " + fps.split(" ")[1];
			yAdd += drawLeftAligned(matrices, textRenderer, fps, i, yAdd, animation);
			i++;
		}
		animation = getAnimation(j++);
		if(animate.get()) {
			animation.set(showPing.get() && isEnabled());
		} else {
			animation.setAbsolute(showPing.get() && isEnabled());
		}
		if(animation.get() > 0) {
			String ping = "0 ms";
			if(MinecraftClient.getInstance().player != null) {
				PlayerListEntry playerListEntry = MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry(MinecraftClient.getInstance().player.getUuid());
				if(playerListEntry != null) {
					int latency = playerListEntry.getLatency();
					ServerInfo serverInfo = ConnectionUtil.getServerInfo();
					if(serverInfo != null) {
						ping = (latency <= 0 ? serverInfo.ping : latency) + " ms";
					} else {
						ping = latency + " ms";
					}
				}
			}
			yAdd += drawLeftAligned(matrices, textRenderer, ping, i, yAdd, animation);
			i++;
		}
		animation = getAnimation(j++);
		if(animate.get()) {
			animation.set(showTPS.get() && isEnabled());
		} else {
			animation.setAbsolute(showTPS.get() && isEnabled());
		}
		if(animation.get() > 0) {
			String tps = String.format("%.2f tps", ConnectionUtil.getTPS());
			yAdd += drawLeftAligned(matrices, textRenderer, tps, i, yAdd, animation);
			i++;
		}
		animation = getAnimation(j++);
		if(animate.get()) {
			animation.set(showTimeSinceLastTick.get() && isEnabled());
		} else {
			animation.setAbsolute(showTimeSinceLastTick.get() && isEnabled());
		}
		if(animation.get() > 0) {
			float timeSinceLastTick = ConnectionUtil.getTimeSinceLastTick() / 1000f;
			if(timeSinceLastTick >= 2) {
				String timeSinceLastTickString = String.format("Seconds Since Last Tick: %.2f", timeSinceLastTick);
				yAdd += drawLeftAligned(matrices, textRenderer, timeSinceLastTickString, i, yAdd, animation);
				i++;
			}
		}
		leftHeight = yAdd;
		
		yAdd = 0;
		int k = j;
		Map<Module, Animation> moduleAnimations = new HashMap<>();
		for(Module m : ModuleManager.getModules()) {
			animation = getAnimation(k++);
			if(animate.get()) {
				animation.set(m.isEnabled() && m.shouldShowModule() && showModules.get() && isEnabled());
			} else {
				animation.setAbsolute(m.isEnabled() && m.shouldShowModule() && showModules.get() && isEnabled());
			}
			moduleAnimations.put(m, animation);
		}
		for(Module m : ModuleManager.getModules().stream().sorted((a, b) -> Integer.compare(MinecraftClient.getInstance().textRenderer.getWidth(b.getHUDText()), MinecraftClient.getInstance().textRenderer.getWidth(a.getHUDText()))).toList()) {
			animation = moduleAnimations.get(m);
			j++;
			if(animation.get() > 0) {
				yAdd += drawRightAligned(matrices, textRenderer, m.getHUDText(), i, yAdd, animation);
				i++;
			}
		}
		rightHeight = yAdd;
		
		yAdd = 0;
		animation = getAnimation(j++);
		if(animate.get()) {
			animation.set((showCoordinates.get() || showDirection.get()) && isEnabled());
		} else {
			animation.setAbsolute((showCoordinates.get() || showDirection.get()) && isEnabled());
		}
		if(animation.get() > 0 && MinecraftClient.getInstance().player != null) {
			Freecam freecam = ModuleManager.getModule(Freecam.class);
			Vec3 pos = freecam.isEnabled() ? new Vec3(freecam.pos) : new Vec3(MinecraftClient.getInstance().player.getPos());
			float yaw = freecam.isEnabled() ? freecam.yaw : MinecraftClient.getInstance().player.getYaw();
			float pitch = freecam.isEnabled() ? freecam.pitch : MinecraftClient.getInstance().player.getPitch();
			String coords = "";
			if(showCoordinates.get()) {
				coords += String.format("Coords: %.2f, %.2f, %.2f ", pos.getX(), pos.getY(), pos.getZ());
			}
			if(showDirection.get()) {
				if(!coords.equals("")) {
					coords += "| ";
				} else {
					coords += "Facing: ";
				}
				if(directionYawPitch.get()) {
					coords += String.format("%.2f, %.2f ", yaw, pitch);
				} else {
					coords += String.format("%s ", DirectionHelper.fromRotation(yaw));
				}
			}
			coords = coords.trim();
			yAdd += drawCoords(matrices, textRenderer, coords, i, yAdd, animation);
			i++;
		}
		
		matrices.pop();
		
		animations.forEach(Animation::update);
	}
	
	private float drawLeftAligned(MatrixStack matrices, TextRenderer fontRenderer, String text, int i, float yAdd, Animation animation) {
		float[] barC = accentColor.get().getHSB();
		float[] bgC = bgColor.get().getHSB();
		float[] textC = textColor.get().getHSB();
		float finalBarHue;
		if(accentColor.get().getChroma()) {
			finalBarHue = (barC[0] - (i * 0.025f)) % 1f;
		} else {
			finalBarHue = barC[0];
		}
		float finalBGHue;
		if(bgColor.get().getChroma()) {
			finalBGHue = (bgC[0] - (i * 0.025f)) % 1f;
		} else {
			finalBGHue = bgC[0];
		}
		float finalTextHue;
		if(textColor.get().getChroma()) {
			finalTextHue = (textC[0] - (i * 0.025f)) % 1f;
		} else {
			finalTextHue = textC[0];
		}
		int barColor = Color.toRGB(finalBarHue, barC[1], barC[2], barC[3]);
		int bgColor = Color.toRGB(finalBGHue, bgC[1], bgC[2], bgC[3]);
		int textColor = Color.toRGB(finalTextHue, textC[1], textC[2], textC[3]);
		float textX = (float)(2 - ((fontRenderer.getWidth(text) + 7) * (1 - animation.get())));
		float textY = yAdd + 2;
		RenderUtil.preRender();
		RenderUtil.drawRect(matrices, textX - 2, textY - 2, fontRenderer.getWidth(text) + 4, fontRenderer.fontHeight + 2, bgColor);
		RenderUtil.drawRect(matrices, textX + fontRenderer.getWidth(text) + 2,  textY - 2, 3, fontRenderer.fontHeight + 2, barColor);
		RenderUtil.postRender();
		fontRenderer.drawWithShadow(matrices, text, textX, textY, textColor);
		return (float)((fontRenderer.fontHeight + 2) * animation.get());
	}
	
	private float drawRightAligned(MatrixStack matrices, TextRenderer fontRenderer, String text, int i, float yAdd, Animation animation) {
		float[] barC = accentColor.get().getHSB();
		float[] bgC = bgColor.get().getHSB();
		float[] textC = textColor.get().getHSB();
		float finalBarHue;
		if(accentColor.get().getChroma()) {
			finalBarHue = (barC[0] - (i * 0.025f)) % 1f;
		} else {
			finalBarHue = barC[0];
		}
		float finalBGHue;
		if(bgColor.get().getChroma()) {
			finalBGHue = (bgC[0] - (i * 0.025f)) % 1f;
		} else {
			finalBGHue = bgC[0];
		}
		float finalTextHue;
		if(textColor.get().getChroma()) {
			finalTextHue = (textC[0] - (i * 0.025f)) % 1f;
		} else {
			finalTextHue = textC[0];
		}
		int barColor = Color.toRGB(finalBarHue, barC[1], barC[2], barC[3]);
		int bgColor = Color.toRGB(finalBGHue, bgC[1], bgC[2], bgC[3]);
		int textColor = Color.toRGB(finalTextHue, textC[1], textC[2], textC[3]);
		float textX = MinecraftClient.getInstance().getWindow().getScaledWidth() - fontRenderer.getWidth(text) - 2 + (float)((fontRenderer.getWidth(text) + 7 ) * (1 - animation.get()));
		float textY = yAdd + 2;
		RenderUtil.preRender();
		RenderUtil.drawRect(matrices, textX - 2, textY - 2, fontRenderer.getWidth(text) + 4, fontRenderer.fontHeight + 2, bgColor);
		RenderUtil.drawRect(matrices, textX - 5, textY - 2, 3, fontRenderer.fontHeight + 2, barColor);
		RenderUtil.postRender();
		fontRenderer.drawWithShadow(matrices, text, textX, textY, textColor);
		return (float)((fontRenderer.fontHeight + 2) * animation.get());
	}
	
	private float drawCoords(MatrixStack matrices, TextRenderer fontRenderer, String text, int i, float yAdd, Animation animation) {
		float[] textC = textColor.get().getHSB();
		float finalTextHue;
		if(textColor.get().getChroma()) {
			finalTextHue = (textC[0] - (i * 0.025f)) % 1f;
		} else {
			finalTextHue = textC[0];
		}
		int textColor = Color.toRGB(finalTextHue, textC[1], textC[2], textC[3]);
		float textX = (float)(2 - ((fontRenderer.getWidth(text) + 7) * (1 - animation.get())));
		float textY = MinecraftClient.getInstance().getWindow().getScaledHeight() - yAdd - (fontRenderer.fontHeight + 2);
		fontRenderer.drawWithShadow(matrices, text, textX, textY, textColor);
		return -(float)((fontRenderer.fontHeight + 2) * animation.get());
	}
	
	private Animation getAnimation(int i) {
		while(animations.size() <= i) {
			animations.add(i, AnimationBuilder.create(AnimationType.IN_OUT_QUAD, 0.25).build());
		}
		return animations.get(i);
	}
	
	public void applyHandTransform(LivingEntity entity, ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		if(isEnabled()) {
			if(entity == mc.getCameraEntity() && mc.options.getPerspective().isFirstPerson()) {
//				matrices.scale(ModuleManager.getModule(HUD.class).heldItemScale, ModuleManager.getModule(HUD.class).heldItemScale, ModuleManager.getModule(HUD.class).heldItemScale);
//				if(stack.getItem() == Items.SHIELD) {
//					matrices.translate(0, shieldHeightModifier, 0);
//				} // TODO: Move to module
			}
		}
	}
	
	public void applyFireTransform(MatrixStack matrices) {
		if(isEnabled()) {
//			matrices.translate(0, fireHeightModifier, 0); // TODO: Move to module
		}
	}
	
	public float getOverlayTransparency(float original) {
//		return isEnabled() ? (overlayTransparency * original) : original; // TODO: Move to module
		return  original;
	}
}
