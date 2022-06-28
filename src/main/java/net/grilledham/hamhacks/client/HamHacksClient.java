package net.grilledham.hamhacks.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.grilledham.hamhacks.command.CommandManager;
import net.grilledham.hamhacks.gui.overlays.IngameGui;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.combat.*;
import net.grilledham.hamhacks.modules.misc.CommandModule;
import net.grilledham.hamhacks.modules.movement.*;
import net.grilledham.hamhacks.modules.player.*;
import net.grilledham.hamhacks.modules.render.ClickGUI;
import net.grilledham.hamhacks.modules.render.Fullbright;
import net.grilledham.hamhacks.modules.render.HUD;
import net.grilledham.hamhacks.modules.render.Tracers;
import net.grilledham.hamhacks.util.*;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class HamHacksClient implements ClientModInitializer {
	
	public static final String MOD_ID = "hamhacks";
	public static final Version VERSION = new Version("1.6-beta.0");
	
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	public static final int CONFIG_VERSION = 1;
	
	public static final Logger LOGGER = LogUtils.getLogger();
	
	@Override
	public void onInitializeClient() {
	}
	
	public static void init() {
		LOGGER.info("Initializing HamHacks v" + VERSION.getVersion(0, true));
		Updater.init();
		RotationHack.init();
		ConnectionUtil.init();
		ChatUtil.init();
		registerModules();
		Module.Category.init();
		CommandManager.init();
		HamHacksConfig.initializeConfig();
		IngameGui.register();
	}
	
	public static void shutdown() {
		HamHacksConfig.save();
		LOGGER.info("Bye for now :)");
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
		ModuleManager.register(new Step());
		ModuleManager.register(new Tracers());
		ModuleManager.register(new KillAura());
		ModuleManager.register(new Reach());
		ModuleManager.register(new CommandModule());
		ModuleManager.register(new InstantKillBow());
		ModuleManager.register(new Fullbright());
		
//		ModuleManager.register(new TestModule()); // For testing
	}
}
