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
import net.grilledham.hamhacks.setting.NumberSetting;
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
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HUD extends Module {
	
	@BoolSetting(name = "hamhacks.module.hud.animate", category = "hamhacks.module.hud.category.appearance", defaultValue = true)
	public boolean animate = true;
	
	@ColorSetting(name = "hamhacks.module.hud.accentColor", category = "hamhacks.module.hud.category.appearance")
	public Color accentColor = new Color(1, 1, 1, 1, true);
	
	@ColorSetting(name = "hamhacks.module.hud.backgroundColor", category = "hamhacks.module.hud.category.appearance")
	public Color bgColor = new Color(0x80000000);
	
	@ColorSetting(name = "hamhacks.module.hud.textColor", category = "hamhacks.module.hud.category.appearance")
	public Color textColor = Color.getWhite();
	
	@BoolSetting(name = "hamhacks.module.hud.showLogo", category = "hamhacks.module.hud.category.elements", defaultValue = true)
	public boolean showLogo = true;
	
	@StringSetting(name = "hamhacks.module.hud.logoText", category = "hamhacks.module.hud.category.elements", dependsOn = "showLogo", placeholder = "&4&l&oHamHacks")
	public String logoText = "";
	
	@BoolSetting(name = "hamhacks.module.hud.showFps", category = "hamhacks.module.hud.category.elements", defaultValue = true)
	public boolean showFPS = true;
	
	@BoolSetting(name = "hamhacks.module.hud.showPing", category = "hamhacks.module.hud.category.elements", defaultValue = true)
	public boolean showPing = true;
	
	@BoolSetting(name = "hamhacks.module.hud.showTps", category = "hamhacks.module.hud.category.elements", defaultValue = true)
	public boolean showTPS = true;
	
	@BoolSetting(name = "hamhacks.module.hud.showTimeSinceLastTick", category = "hamhacks.module.hud.category.elements", defaultValue = true)
	public boolean showTimeSinceLastTick = true;
	
	@BoolSetting(name = "hamhacks.module.hud.showModules", category = "hamhacks.module.hud.category.elements", defaultValue = true)
	public boolean showModules = true;
	
	@BoolSetting(name = "hamhacks.module.hud.showCoordinates", category = "hamhacks.module.hud.category.elements", defaultValue = true)
	public boolean showCoordinates = true;
	
	@BoolSetting(name = "hamhacks.module.hud.showDirection", category = "hamhacks.module.hud.category.elements", defaultValue = true)
	public boolean showDirection = true;
	
	@BoolSetting(name = "hamhacks.module.hud.directionYawPitch", category = "hamhacks.module.hud.category.elements", dependsOn = "showDirection")
	public boolean directionYawPitch = false;
	
	@NumberSetting(
			name = "hamhacks.module.hud.heldItemScale",
			defaultValue = 1,
			min = 0.1f,
			max = 2, category = "hamhacks.module.hud.category.other"
	)
	public float heldItemScale = 1;
	
	@NumberSetting(
			name = "hamhacks.module.hud.shieldHeight",
			min = -0.5f,
			max = 0.5f, category = "hamhacks.module.hud.category.other"
	)
	public float shieldHeightModifier = 0;
	
	@NumberSetting(
			name = "hamhacks.module.hud.fireHeight",
			min = -0.5f,
			max = 0.5f, category = "hamhacks.module.hud.category.other"
	)
	public float fireHeightModifier = 0;
	
	@NumberSetting(
			name = "hamhacks.module.hud.overlayTransparency",
			defaultValue = 1,
			min = 0,
			max = 1, category = "hamhacks.module.hud.category.other"
	)
	public float overlayTransparency = 1;
	
	@BoolSetting(name = "hamhacks.module.hud.modelBobbingOnly", category = "hamhacks.module.hud.category.other")
	public boolean modelBobbingOnly = false;
	
	@BoolSetting(name = "hamhacks.module.hud.noHurtCam", category = "hamhacks.module.hud.category.other")
	public boolean noHurtCam = false;
	
	public HUD() {
		super(Text.translatable("hamhacks.module.hud"), Category.RENDER, new Keybind(0));
		setEnabled(true);
		showModule = false;
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
		
		float[] textC = textColor.getHSB();
		
		int j = 0;
		int i = 0;
		float yAdd = 0;
		Animation animation = getAnimation(j++);
		if(animate) {
			animation.set(showLogo && isEnabled());
		} else {
			animation.setAbsolute(showLogo && isEnabled());
		}
		if(animation.get() > 0) {
			float finalTextHue;
			if(textColor.getChroma()) {
				finalTextHue = (textC[0] - (i * 0.025f)) % 1f;
			} else {
				finalTextHue = textC[0];
			}
			int textColor = Color.toRGB(finalTextHue, textC[1], textC[2], textC[3]);
			String text = logoText.equals("") ? "§4§l§oHamHacks" : ChatUtil.format(logoText);
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
		if(animate) {
			animation.set(showFPS && isEnabled());
		} else {
			animation.setAbsolute(showFPS && isEnabled());
		}
		if(animation.get() > 0) {
			String fps = MinecraftClient.getInstance().fpsDebugString;
			fps = fps.split(" ")[0] + " " + fps.split(" ")[1];
			yAdd += drawLeftAligned(matrices, textRenderer, fps, i, yAdd, animation);
			i++;
		}
		animation = getAnimation(j++);
		if(animate) {
			animation.set(showPing && isEnabled());
		} else {
			animation.setAbsolute(showPing && isEnabled());
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
		if(animate) {
			animation.set(showTPS && isEnabled());
		} else {
			animation.setAbsolute(showTPS && isEnabled());
		}
		if(animation.get() > 0) {
			String tps = String.format("%.2f tps", ConnectionUtil.getTPS());
			yAdd += drawLeftAligned(matrices, textRenderer, tps, i, yAdd, animation);
			i++;
		}
		animation = getAnimation(j++);
		if(animate) {
			animation.set(showTimeSinceLastTick && isEnabled());
		} else {
			animation.setAbsolute(showTimeSinceLastTick && isEnabled());
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
			if(animate) {
				animation.set(m.isEnabled() && m.shouldShowModule() && showModules && isEnabled());
			} else {
				animation.setAbsolute(m.isEnabled() && m.shouldShowModule() && showModules && isEnabled());
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
		if(animate) {
			animation.set((showCoordinates || showDirection) && isEnabled());
		} else {
			animation.setAbsolute((showCoordinates || showDirection) && isEnabled());
		}
		if(animation.get() > 0 && MinecraftClient.getInstance().player != null) {
			Vec3 pos = new Vec3(MinecraftClient.getInstance().player.getPos());
			float yaw = MinecraftClient.getInstance().player.getYaw();
			float pitch = MinecraftClient.getInstance().player.getPitch();
			String coords = "";
			if(showCoordinates) {
				coords += String.format("Coords: %.2f, %.2f, %.2f ", pos.getX(), pos.getY(), pos.getZ());
			}
			if(showDirection) {
				if(!coords.equals("")) {
					coords += "| ";
				} else {
					coords += "Facing: ";
				}
				if(directionYawPitch) {
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
		float[] barC = accentColor.getHSB();
		float[] bgC = bgColor.getHSB();
		float[] textC = textColor.getHSB();
		float finalBarHue;
		if(accentColor.getChroma()) {
			finalBarHue = (barC[0] - (i * 0.025f)) % 1f;
		} else {
			finalBarHue = barC[0];
		}
		float finalBGHue;
		if(bgColor.getChroma()) {
			finalBGHue = (bgC[0] - (i * 0.025f)) % 1f;
		} else {
			finalBGHue = bgC[0];
		}
		float finalTextHue;
		if(textColor.getChroma()) {
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
		float[] barC = accentColor.getHSB();
		float[] bgC = bgColor.getHSB();
		float[] textC = textColor.getHSB();
		float finalBarHue;
		if(accentColor.getChroma()) {
			finalBarHue = (barC[0] - (i * 0.025f)) % 1f;
		} else {
			finalBarHue = barC[0];
		}
		float finalBGHue;
		if(bgColor.getChroma()) {
			finalBGHue = (bgC[0] - (i * 0.025f)) % 1f;
		} else {
			finalBGHue = bgC[0];
		}
		float finalTextHue;
		if(textColor.getChroma()) {
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
		float[] textC = textColor.getHSB();
		float finalTextHue;
		if(textColor.getChroma()) {
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
				matrices.scale(ModuleManager.getModule(HUD.class).heldItemScale, ModuleManager.getModule(HUD.class).heldItemScale, ModuleManager.getModule(HUD.class).heldItemScale);
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
