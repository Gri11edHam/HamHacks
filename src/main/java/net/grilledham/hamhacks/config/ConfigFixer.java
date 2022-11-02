package net.grilledham.hamhacks.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.setting.KeySetting;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.grilledham.hamhacks.setting.SettingHelper;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class ConfigFixer {
	
	private static final String TRANSLATION_KEY_UPDATE_1_6;
	
	static {
		TRANSLATION_KEY_UPDATE_1_6 = """
				{
					"module.hamhacks.fly": "hamhacks.module.fly",
					"module.hamhacks.fly.tooltip": "hamhacks.module.fly.tooltip",
					"setting.fly.speed": "hamhacks.module.fly.speed",
					"setting.fly.speed.tooltip": "hamhacks.module.fly.speed.tooltip",
					"setting.fly.mode": "hamhacks.module.fly.mode",
					"setting.fly.mode.tooltip": "hamhacks.module.fly.mode.tooltip",
					"setting.fly.mode.default": "hamhacks.module.fly.mode.default",
					"setting.fly.mode.vanilla": "hamhacks.module.fly.mode.vanilla",
					"setting.fly.mode.jetpack": "hamhacks.module.fly.mode.jetpack",
					"setting.fly.smoothmovement": "hamhacks.module.fly.smoothMovement",
					"setting.fly.smoothmovement.tooltip": "hamhacks.module.fly.smoothMovement.tooltip",
					"setting.fly.jetpackspeed": "hamhacks.module.fly.jetpackSpeed",
					"setting.fly.jetpackspeed.tooltip": "hamhacks.module.fly.jetpackSpeed.tooltip",
					"setting.fly.autoland": "hamhacks.module.fly.autoLand",
					"setting.fly.autoland.tooltip": "hamhacks.module.fly.autoLand.tooltip",
				 
					"module.hamhacks.trap": "hamhacks.module.trap",
					"module.hamhacks.trap.tooltip": "hamhacks.module.trap.tooltip",
				 
					"module.hamhacks.encase": "hamhacks.module.encase",
					"module.hamhacks.encase.tooltip": "hamhacks.module.encase.tooltip",
				 
					"module.hamhacks.crystalaura": "hamhacks.module.crystalAura",
					"module.hamhacks.crystalaura.tooltip": "hamhacks.module.crystalAura.tooltip",
				 
					"module.hamhacks.scrollclick": "hamhacks.module.scrollClick",
					"module.hamhacks.scrollclick.tooltip": "hamhacks.module.scrollClick.tooltip",
				 
					"module.hamhacks.nofall": "hamhacks.module.noFall",
					"module.hamhacks.nofall.tooltip": "hamhacks.module.noFall.tooltip",
					"setting.nofall.mode": "hamhacks.module.noFall.mode",
					"setting.nofall.mode.tooltip": "hamhacks.module.noFall.mode.tooltip",
					"setting.nofall.mode.packet": "hamhacks.module.noFall.mode.packet",
					"setting.nofall.mode.momentum": "hamhacks.module.noFall.mode.momentum",
				 
					"module.hamhacks.aimbot": "hamhacks.module.aimbot",
					"module.hamhacks.aimbot.tooltip": "hamhacks.module.aimbot.tooltip",
					"setting.aimbot.speed": "hamhacks.module.aimbot.speed",
					"setting.aimbot.speed.tooltip": "hamhacks.module.aimbot.speed.tooltip",
					"setting.aimbot.fov": "hamhacks.module.aimbot.fov",
					"setting.aimbot.fov.tooltip": "hamhacks.module.aimbot.fov.tooltip",
					"setting.aimbot.targetentities": "hamhacks.module.aimbot.targetEntities",
					"setting.aimbot.keepaiming": "hamhacks.module.aimbot.keepAiming",
					"setting.aimbot.keepaiming.tooltip": "hamhacks.module.aimbot.keepAiming.tooltip",
					"setting.aimbot.targetplayers": "hamhacks.module.aimbot.targetPlayers",
					"setting.aimbot.targetpassive": "hamhacks.module.aimbot.targetPassive",
					"setting.aimbot.targethostile": "hamhacks.module.aimbot.targetHostile",
					"setting.aimbot.targetblocks": "hamhacks.module.aimbot.targetBlocks",
				 
					"module.hamhacks.boatfly": "hamhacks.module.boatFly",
					"module.hamhacks.boatfly.tooltip": "hamhacks.module.boatFly.tooltip",
				 
					"module.hamhacks.autoelytra": "hamhacks.module.autoElytra",
					"module.hamhacks.autoelytra.tooltip": "hamhacks.module.autoElytra.tooltip",
				 
					"module.hamhacks.speed": "hamhacks.module.speed",
					"module.hamhacks.speed.tooltip": "hamhacks.module.speed.tooltip",
					"setting.speed.speed": "hamhacks.module.speed.speed",
					"setting.speed.autojump": "hamhacks.module.speed.autoJump",
					"setting.speed.autojump.tooltip": "hamhacks.module.speed.autoJump.tooltip",
					"setting.speed.inairmultiplier": "hamhacks.module.speed.inAirMultiplier",
					"setting.speed.inairmultiplier.tooltip": "hamhacks.module.speed.inAirMultiplier.tooltip",
					"setting.speed.onicemultiplier": "hamhacks.module.speed.onIceMultiplier",
					"setting.speed.onicemultiplier.tooltip": "hamhacks.module.speed.onIceMultiplier.tooltip",
					"setting.speed.intunnelmultiplier": "hamhacks.module.speed.inTunnelMultiplier",
					"setting.speed.intunnelmultiplier.tooltip": "hamhacks.module.speed.inTunnelMultiplier.tooltip",
					"setting.speed.inwatermultiplier": "hamhacks.module.speed.inWaterMultiplier",
					"setting.speed.inwatermultiplier.tooltip": "hamhacks.module.speed.inWaterMultiplier.tooltip",
					"setting.speed.disablewithelytra": "hamhacks.module.speed.disableWithElytra",
				 
					"module.hamhacks.jesus": "hamhacks.module.jesus",
					"module.hamhacks.jesus.tooltip": "hamhacks.module.jesus.tooltip",
				 
					"module.hamhacks.killaura": "hamhacks.module.killAura",
					"module.hamhacks.killaura.tooltip": "hamhacks.module.killAura.tooltip",
					"setting.killaura.range": "hamhacks.module.killAura.range",
					"setting.killaura.targetplayers": "hamhacks.module.killAura.targetPlayers",
					"setting.killaura.targetpassive": "hamhacks.module.killAura.targetPassive",
					"setting.killaura.targethostile": "hamhacks.module.killAura.targetHostile",
				 
					"module.hamhacks.reach": "hamhacks.module.reach",
					"module.hamhacks.reach.tooltip": "hamhacks.module.reach.tooltip",
					"setting.reach.entityrange": "hamhacks.module.reach.entityRange",
					"setting.reach.blockrange": "hamhacks.module.reach.blockRange",
				 
					"module.hamhacks.sprint": "hamhacks.module.sprint",
					"module.hamhacks.sprint.tooltip": "hamhacks.module.sprint.tooltip",
				 
					"module.hamhacks.step": "hamhacks.module.step",
					"module.hamhacks.step.tooltip": "hamhacks.module.step.tooltip",
					"setting.step.height": "hamhacks.module.step.height",
				 
					"module.hamhacks.clickgui": "hamhacks.module.clickGui",
					"module.hamhacks.clickgui.tooltip": "hamhacks.module.clickGui.tooltip",
					"setting.clickgui.accentcolor": "hamhacks.module.clickGui.accentColor",
					"setting.clickgui.backgroundcolor": "hamhacks.module.clickGui.backgroundColor",
					"setting.clickgui.backgroundcolorhovered": "hamhacks.module.clickGui.backgroundColorHovered",
					"setting.clickgui.textcolor": "hamhacks.module.clickGui.textColor",
					"setting.clickgui.scale": "hamhacks.module.clickGui.scale",
				 
					"module.hamhacks.hud": "hamhacks.module.hud",
					"module.hamhacks.hud.tooltip": "hamhacks.module.hud.tooltip",
					"setting.hud.showlogo": "hamhacks.module.hud.showLogo",
					"setting.hud.showfps": "hamhacks.module.hud.showFps",
					"setting.hud.showping": "hamhacks.module.hud.showPing",
					"setting.hud.showtps": "hamhacks.module.hud.showTps",
					"setting.hud.showtimesincelasttick": "hamhacks.module.hud.showTimeSinceLastTick",
					"setting.hud.showmodules": "hamhacks.module.hud.showModules",
					"setting.hud.helditemscale": "hamhacks.module.hud.heldItemScale",
					"setting.hud.shieldheight": "hamhacks.module.hud.shieldHeight",
					"setting.hud.fireheight": "hamhacks.module.hud.fireHeight",
					"setting.hud.overlaytransparency": "hamhacks.module.hud.overlayTransparency",
					"setting.hud.modelbobbingonly": "hamhacks.module.hud.modelBobbingOnly",
					"setting.hud.nohurtcam": "hamhacks.module.hud.noHurtCam",
					"setting.hud.accentcolor": "hamhacks.module.hud.accentColor",
					"setting.hud.backgroundcolor": "hamhacks.module.hud.backgroundColor",
					"setting.hud.textcolor": "hamhacks.module.hud.textColor",
				 
					"module.hamhacks.tracers": "hamhacks.module.tracers",
					"module.hamhacks.tracers.tooltip": "hamhacks.module.tracers.tooltip",
					"setting.tracers.traceplayers": "hamhacks.module.tracers.tracePlayers",
					"setting.tracers.playercolorclose": "hamhacks.module.tracers.playerColorClose",
					"setting.tracers.playercolorfar": "hamhacks.module.tracers.playerColorFar",
					"setting.tracers.tracehostile": "hamhacks.module.tracers.traceHostile",
					"setting.tracers.hostilecolorclose": "hamhacks.module.tracers.hostileColorClose",
					"setting.tracers.hostilecolorfar": "hamhacks.module.tracers.hostileColorFar",
					"setting.tracers.tracepassive": "hamhacks.module.tracers.tracePassive",
					"setting.tracers.passivecolorclose": "hamhacks.module.tracers.passiveColorClose",
					"setting.tracers.passivecolorfar": "hamhacks.module.tracers.passiveColorFar",
				 
					"module.hamhacks.command": "hamhacks.module.command",
				 
					"module.hamhacks.instantkillbow": "hamhacks.module.instantKillBow",
					"module.hamhacks.instantkillbow.tooltip": "hamhacks.module.instantKillBow.tooltip",
					"setting.instantkillbow.iterations": "hamhacks.module.instantKillBow.iterations",
					"setting.instantkillbow.iterations.tooltip": "hamhacks.module.instantKillBow.iterations.tooltip",
				 
					"module.hamhacks.fullbright": "hamhacks.module.fullBright",
					"module.hamhacks.fullbright.tooltip": "hamhacks.module.fullBright.tooltip",
					"setting.fullbright.brightness": "hamhacks.module.fullBright.brightness",
					"setting.fullbright.smoothtransition": "hamhacks.module.fullBright.smoothTransition",
					"setting.fullbright.smoothtransition.tooltip": "hamhacks.module.fullBright.smoothTransition.tooltip",
				 
					"setting.generic.enabled": "hamhacks.module.generic.enabled",
					"setting.generic.showmodule": "hamhacks.module.generic.showModule",
					"setting.generic.keybind": "hamhacks.module.generic.keybind",
				 
					"setting.colorsettingpart.chroma": "hamhacks.setting.colorSettingPart.chroma",
				 
					"category.hamhacks.movement": "hamhacks.category.movement",
					"category.hamhacks.combat": "hamhacks.category.combat",
					"category.hamhacks.render": "hamhacks.category.render",
					"category.hamhacks.player": "hamhacks.category.player",
					"category.hamhacks.world": "hamhacks.category.world",
					"category.hamhacks.misc": "hamhacks.category.misc",
				 
					"menu.hamhacks.options": "hamhacks.menu.options",
					"menu.hamhacks.clickgui": "hamhacks.menu.clickGui",
					"menu.hamhacks.clickgui.module": "hamhacks.menu.clickGui.module",
					"menu.hamhacks.changelog": "hamhacks.menu.changelog",
					"menu.hamhacks.fullchangelog": "hamhacks.menu.fullChangelog",
					"menu.hamhacks.bindmodule": "hamhacks.menu.bindModule",
					"menu.hamhacks.update": "hamhacks.menu.update"
				}
				""";
	}
	
	public static void fixConfig(JsonObject obj, int configVersion) {
		switch(configVersion) {
			case 0:
				for(Module m : ModuleManager.getModules()) {
					JsonObject settings = obj.getAsJsonObject("modules").getAsJsonObject(m.getName()).getAsJsonObject("settings");
					for(Field f : SettingHelper.getNumberSettings(m)) {
						String name = f.getAnnotation(NumberSetting.class).name();
						settings.addProperty(name, settings.get(name).getAsJsonObject().get("value").getAsFloat());
					}
				}
			case 1:
				JsonObject newKeys = JsonParser.parseString(TRANSLATION_KEY_UPDATE_1_6).getAsJsonObject();
				JsonObject modules = obj.get("modules").getAsJsonObject();
				Set<String> oldKeys = new HashSet<>(modules.keySet());
				for(String oldKey : oldKeys) {
					JsonObject settings = modules.get(oldKey).getAsJsonObject().get("settings").getAsJsonObject();
					Set<String> oldSKeys = new HashSet<>(settings.keySet());
					for(String oldSKey : oldSKeys) {
						if(settings.get(oldSKey).isJsonPrimitive()) {
							String oldValue = settings.get(oldSKey).getAsString();
							if(newKeys.has(oldValue)) {
								settings.addProperty(oldSKey, newKeys.get(oldValue).getAsString());
							}
						}
						if(newKeys.has(oldSKey)) {
							String newSKey = newKeys.get(oldSKey).getAsString();
							settings.add(newSKey, settings.get(oldSKey));
							settings.remove(oldSKey);
						}
					}
					if(newKeys.has(oldKey)) {
						String newKey = newKeys.get(oldKey).getAsString();
						modules.add(newKey, modules.get(oldKey));
						modules.remove(oldKey);
					}
				}
			case 2:
				for(Module m : ModuleManager.getModules()) {
					JsonObject settings = obj.getAsJsonObject("modules").getAsJsonObject(m.getName()).getAsJsonObject("settings");
					boolean oldForceDisabled = settings.get("hamhacks.module.generic.internal.forceDisabled").getAsBoolean();
					settings.addProperty("hamhacks.module.generic.internal.forceDisabled", oldForceDisabled ? 1f : 0f);
				}
			case 3:
				for(Module m : ModuleManager.getModules()) {
					JsonObject settings = obj.getAsJsonObject("modules").getAsJsonObject(m.getName()).getAsJsonObject("settings");
					for(Field f : SettingHelper.getKeySettings(m)) {
						String name = f.getAnnotation(KeySetting.class).name();
						float code = settings.get(name).getAsFloat();
						JsonArray arr = new JsonArray();
						arr.add(code);
						settings.add(name, arr);
					}
				}
			// Add more switch cases for new config changes
		}
	}
}
