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
		})/*.suggests((ctx, b) -> {
			List<Suggestion> suggestions = new ArrayList<>();
			for(Module module : ModuleManager.getModules()) {
				suggestions.add(new Suggestion(StringRange.at(ctx.getRange().getEnd() + 1), "\"" + module.getName() + "\""));
			}
			CompletableFuture<Suggestions> toReturn = new CompletableFuture<>();
			toReturn.complete(new Suggestions(StringRange.at(ctx.getRange().getEnd() + 1), suggestions));
			return toReturn;
		})*/);
	}
}
