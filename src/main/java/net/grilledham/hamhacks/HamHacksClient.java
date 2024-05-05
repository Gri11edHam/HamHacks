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
import net.grilledham.hamhacks.font.FontManager;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.render.HUD;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.grilledham.hamhacks.profile.ProfileManager;
import net.grilledham.hamhacks.util.*;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;

import java.util.Comparator;

@Environment(EnvType.CLIENT)
public class HamHacksClient implements ClientModInitializer {
	
	public static final String MOD_ID = "hamhacks";
	public static final Version VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).map(modContainer -> new Version(modContainer.getMetadata().getVersion().getFriendlyString())).orElseGet(() -> new Version("0-dev"));
	
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	public static final int CONFIG_VERSION = 5;
	
	public static final Logger LOGGER = LogUtils.getLogger();
	
	public static boolean firstTime = false;
	public static Version seenVersion = VERSION;
	public static boolean updated = false;
	
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
		LOGGER.info("Initializing HamHacks v{}", VERSION.getVersion(0, true));
		RotationHack.init();
		PositionHack.init();
		ConnectionUtil.init();
		ChatUtil.init();
		ProfileManager.initialize();
		
		FabricLoader loader = FabricLoader.getInstance();
		for(EntrypointContainer<Config> configEntry : loader.getEntrypointContainers("hamhacksStatic", Config.class)) {
			ModMetadata meta = configEntry.getProvider().getMetadata();
			String modId = meta.getId();
			try {
				Config config = configEntry.getEntrypoint();
				ConfigManager.registerStatic(config);
			} catch(Throwable t) {
				LOGGER.error("Failed to load static config from {}", modId, t);
			}
		}
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
		FontManager.init();
		Category.init(); // set initial category positions
		ConfigManager.initialLoad(); // load configs
		RenderUtil.updateFont(PageManager.getPage(ClickGUI.class).font.get());
		
		Updater.init();
		
		ModuleManager.sortModules(Comparator.comparing(Module::getName));
		CommandManager.sortCommands(Comparator.comparing(Command::getName));
		
		MinecraftClient.getInstance().updateWindowTitle();
	}
	
	public static void reloadResources() {
		ModuleManager.getModule(HUD.class).reloadResources();
		ModuleManager.sortModules(Comparator.comparing(Module::getName));
		LOGGER.info("Resource reload complete");
	}
	
	public static void shutdown() {
		LOGGER.info("Saving configs...");
		ConfigManager.save();
		LOGGER.info("Bye for now :)");
	}
}
