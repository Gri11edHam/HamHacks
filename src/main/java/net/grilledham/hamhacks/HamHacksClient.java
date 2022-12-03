package net.grilledham.hamhacks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.grilledham.hamhacks.command.Command;
import net.grilledham.hamhacks.command.CommandManager;
import net.grilledham.hamhacks.config.Config;
import net.grilledham.hamhacks.config.ConfigManager;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.profile.ProfileManager;
import net.grilledham.hamhacks.util.*;
import org.slf4j.Logger;

import java.util.Comparator;

@Environment(EnvType.CLIENT)
public class HamHacksClient implements ClientModInitializer {
	
	public static final String MOD_ID = "$MOD_ID";
	public static final Version VERSION = new Version("$VERSION");
	
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	public static final int CONFIG_VERSION = 5;
	
	public static final Logger LOGGER = LogUtils.getLogger();
	
	public static boolean firstTime = false;
	
	public static boolean initialized = false;
	
	@Override
	public void onInitializeClient() {
	
	}
	
	public static void init() {
		if(initialized) {
			LOGGER.error("init() called twice");
			return;
		}
		initialized = true;
		LOGGER.info("Initializing HamHacks v" + VERSION.getVersion(0, true));
		Updater.init();
		RotationHack.init();
		PositionHack.init();
		ConnectionUtil.init();
		ChatUtil.init();
		ProfileManager.init();
		
		FabricLoader loader = FabricLoader.getInstance();
		for(EntrypointContainer<Config> configEntry : loader.getEntrypointContainers("hamhacks", Config.class)) {
			ModMetadata meta = configEntry.getProvider().getMetadata();
			String modId = meta.getId();
			try {
				Config config = configEntry.getEntrypoint();
				ConfigManager.register(config);
			} catch(Throwable t) {
				LOGGER.error("Failed to load config from {}", modId, t);
			}
		}
		
		ConfigManager.init(); // registers modules/pages/etc
		Category.init(); // set initial category positions
		ConfigManager.initialLoad(); // load configs
		
		ModuleManager.sortModules(Comparator.comparing(Module::getName));
		CommandManager.sortCommands(Comparator.comparing(Command::getName));
	}
	
	public static void shutdown() {
		LOGGER.info("Saving configs...");
		ConfigManager.save();
		LOGGER.info("Saving profiles...");
		ProfileManager.save();
		LOGGER.info("Bye for now :)");
	}
}
