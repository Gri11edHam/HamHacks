package net.grilledham.hamhacks.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.grilledham.hamhacks.command.Command;
import net.grilledham.hamhacks.gui.screen.impl.BindModuleScreen;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.util.ChatUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class BindCommand extends Command {
	
	public BindCommand() {
		super("bind", "Binds a key to a module");
	}
	
	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.then(argument("module", StringArgumentType.string()).executes(ctx -> {
			Module module = ModuleManager.getModule(ctx.getArgument("module", String.class));
			if(module == null) {
				error(Text.of("\"" + ctx.getArgument("module", String.class) + "\" does not exist"));
			} else {
				ChatUtil.openFromChat(new BindModuleScreen(module));
			}
			return SINGLE_SUCCESS;
		}).suggests((ctx, b) -> {
			String moduleName;
			String remaining;
			for(Module module : ModuleManager.getModules()) {
				moduleName = module.getName().toLowerCase();
				remaining = b.getRemainingLowerCase();
				if(moduleName.startsWith(remaining) || ("\"" + moduleName + "\"").startsWith(remaining)) {
					b.suggest(moduleName.contains(" ") ? ("\"" + module.getName() + "\"") : module.getName());
				}
			}
			return b.buildFuture();
		}));
	}
}
