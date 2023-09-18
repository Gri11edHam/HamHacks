package net.grilledham.hamhacks.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.grilledham.hamhacks.command.Command;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class ToggleCommand extends Command {
	
	public ToggleCommand() {
		super("toggle", "Toggles the given module", "t");
	}
	
	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.then(argument("module", StringArgumentType.string()).executes(ctx -> {
			Module module = ModuleManager.getModule(ctx.getArgument("module", String.class));
			if(module == null) {
				error(Text.of("\"" + ctx.getArgument("module", String.class) + "\" does not exist"));
			} else {
				module.toggle();
				info(Text.of("Toggled " + module.getName() + " " + (module.isEnabled() ? "\u00a7aOn" : "\u00a7cOff")));
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
