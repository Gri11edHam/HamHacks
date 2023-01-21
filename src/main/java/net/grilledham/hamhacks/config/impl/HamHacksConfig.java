package net.grilledham.hamhacks.config.impl;

import net.grilledham.hamhacks.HamHacksClient;
import net.grilledham.hamhacks.command.CommandManager;
import net.grilledham.hamhacks.command.impl.BaritoneCommand;
import net.grilledham.hamhacks.command.impl.BindCommand;
import net.grilledham.hamhacks.command.impl.HelpCommand;
import net.grilledham.hamhacks.command.impl.ToggleCommand;
import net.grilledham.hamhacks.config.Config;
import net.grilledham.hamhacks.config.ConfigFixer;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.combat.*;
import net.grilledham.hamhacks.modules.misc.Chat;
import net.grilledham.hamhacks.modules.misc.M1337;
import net.grilledham.hamhacks.modules.misc.NameHider;
import net.grilledham.hamhacks.modules.misc.NoTelemetry;
import net.grilledham.hamhacks.modules.movement.*;
import net.grilledham.hamhacks.modules.player.*;
import net.grilledham.hamhacks.modules.render.*;
import net.grilledham.hamhacks.modules.world.Timer;
import net.grilledham.hamhacks.notification.Notifications;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.Baritone;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.grilledham.hamhacks.page.pages.Commands;
import net.grilledham.hamhacks.page.pages.Profiles;

public class HamHacksConfig extends Config {
	
	public HamHacksConfig() {
		super(HamHacksClient.MOD_ID, "../config.json", 0, ConfigFixer.DEFAULT);
	}
	
	@Override
	public void init() {
		PageManager.register(modId, new ClickGUI());
		PageManager.register(modId, new Notifications());
		PageManager.register(modId, new Commands());
		PageManager.register(modId, new Baritone());
		PageManager.register(modId, new Profiles());
		
		ModuleManager.register(modId, new Fly());
		ModuleManager.register(modId, new Trap());
		ModuleManager.register(modId, new Encase());
		ModuleManager.register(modId, new CrystalAura());
		ModuleManager.register(modId, new ScrollClicker());
		ModuleManager.register(modId, new NoFall());
		ModuleManager.register(modId, new Aimbot());
		ModuleManager.register(modId, new BoatFly());
		ModuleManager.register(modId, new AutoElytra());
		ModuleManager.register(modId, new Speed());
		ModuleManager.register(modId, new Jesus());
		ModuleManager.register(modId, new HUD());
		ModuleManager.register(modId, new Sprint());
		ModuleManager.register(modId, new Step());
		ModuleManager.register(modId, new Tracers());
		ModuleManager.register(modId, new KillAura());
		ModuleManager.register(modId, new Reach());
		ModuleManager.register(modId, new InstantKillBow());
		ModuleManager.register(modId, new Fullbright());
		ModuleManager.register(modId, new Chat());
		ModuleManager.register(modId, new NameHider());
		ModuleManager.register(modId, new Timer());
		ModuleManager.register(modId, new Zoom());
		ModuleManager.register(modId, new NoTelemetry());
		ModuleManager.register(modId, new Freecam());
		ModuleManager.register(modId, new ESP());
		ModuleManager.register(modId, new Nametags());
		ModuleManager.register(modId, new AutoTotem());
		ModuleManager.register(modId, new Teleport());
		ModuleManager.register(modId, new PacketFly());
		ModuleManager.register(modId, new XRay());
		ModuleManager.register(modId, new M1337());
		ModuleManager.register(modId, new HandRender());
		ModuleManager.register(modId, new Overlays());
		ModuleManager.register(modId, new Bob());
		ModuleManager.register(modId, new InfiniteReach());
		ModuleManager.register(modId, new Velocity());
		
		CommandManager.addCommand(new HelpCommand());
		CommandManager.addCommand(new ToggleCommand());
		CommandManager.addCommand(new BindCommand());
		CommandManager.addCommand(new BaritoneCommand());

//		ModuleManager.register(new TestModule()); // For testing
	}
	
	@Override
	protected void firstTime() {
		HamHacksClient.firstTime = true;
	}
}
