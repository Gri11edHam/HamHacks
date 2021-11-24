package net.grilledham.hamhacks.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.grilledham.hamhacks.gui.IngameGui;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.combat.Aimbot;
import net.grilledham.hamhacks.modules.combat.CrystalAura;
import net.grilledham.hamhacks.modules.combat.ScrollClicker;
import net.grilledham.hamhacks.modules.movement.*;
import net.grilledham.hamhacks.modules.render.ClickGUI;
import net.grilledham.hamhacks.modules.player.AutoElytra;
import net.grilledham.hamhacks.modules.player.Encase;
import net.grilledham.hamhacks.modules.player.NoFall;
import net.grilledham.hamhacks.modules.player.Trap;
import net.grilledham.hamhacks.modules.render.HUD;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class HamHacksClient implements ClientModInitializer {
	
	public static final String MOD_ID = "hamhacks";
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	@Override
	public void onInitializeClient() {
	}
	
	public static void init() {
		registerModules();
		Module.Category.init();
		HamHacksConfig.initializeConfig();
		IngameGui.register();
		Runtime.getRuntime().addShutdownHook(new Thread(HamHacksConfig::save));
	}
	
	private static void registerModules() {
		ModuleManager.register(new Fly());
		ModuleManager.register(new Trap());
		ModuleManager.register(new Encase());
		ModuleManager.register(new CrystalAura());
		ModuleManager.register(new ScrollClicker());
		ModuleManager.register(new NoFall());
		ModuleManager.register(new Aimbot());
		ModuleManager.register(new BoatFly());
		ModuleManager.register(new AutoElytra());
		ModuleManager.register(new Speed());
		ModuleManager.register(new Jesus());
		ModuleManager.register(new ClickGUI());
		ModuleManager.register(new HUD());
		ModuleManager.register(new Sprint());
	}
}
