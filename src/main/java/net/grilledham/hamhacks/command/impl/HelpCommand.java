package net.grilledham.hamhacks.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.grilledham.hamhacks.command.Command;
import net.grilledham.hamhacks.command.CommandManager;
import net.minecraft.command.CommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class HelpCommand extends Command {
	
	public HelpCommand() {
		super("help", "Displays help for commands");
	}
	
	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.executes(ctx -> {
			MutableText help = (MutableText)Text.of("");
			for(Command command : CommandManager.getCommands()) {
				MutableText commandHelp = (MutableText)Text.of("");
				commandHelp.append(((MutableText)Text.of("\n    ." + command.getName())).setStyle(Style.EMPTY.withFormatting(Formatting.DARK_RED)));
				commandHelp.append(((MutableText)Text.of(" > ")).setStyle(Style.EMPTY.withFormatting(Formatting.DARK_GRAY)));
				commandHelp.append(((MutableText)Text.of(command.getDescription())).setStyle(Style.EMPTY.withFormatting(Formatting.GRAY)));
				if(!command.getAliases().isEmpty()) {
					commandHelp.append(((MutableText)Text.of("\n        Aliases")).setStyle(Style.EMPTY.withFormatting(Formatting.DARK_RED)));
					commandHelp.append(((MutableText)Text.of(":")).setStyle(Style.EMPTY.withFormatting(Formatting.DARK_GRAY)));
					for(String alias : command.getAliases()) {
						commandHelp.append(((MutableText)Text.of("\n            - ." + alias)).setStyle(Style.EMPTY.withFormatting(Formatting.GRAY)));
					}
				}
				help.append(commandHelp);
			}
			sendMsg(help);
			return SINGLE_SUCCESS;
		}).then(argument("command", StringArgumentType.string()).executes(ctx -> {
			Command command = CommandManager.getCommand(ctx.getArgument("command", String.class));
			if(command == null) {
				error(Text.of("\"" + ctx.getArgument("command", String.class) + "\" does not exist"));
			} else {
				MutableText info = (MutableText)Text.of("");
				info.append(((MutableText)Text.of("." + command.getName())).setStyle(Style.EMPTY.withFormatting(Formatting.DARK_RED)));
				info.append(((MutableText)Text.of(" > ")).setStyle(Style.EMPTY.withFormatting(Formatting.DARK_GRAY)));
				info.append(((MutableText)Text.of(command.getDescription())).setStyle(Style.EMPTY.withFormatting(Formatting.GRAY)));
				sendMsg(info);
			}
			return SINGLE_SUCCESS;
		})/*.suggests((ctx, b) -> {
			List<Suggestion> suggestions = new ArrayList<>();
			for(Command command : CommandManager.getCommands()) {
				suggestions.add(new Suggestion(StringRange.at(ctx.getRange().getEnd() + 1), command.getName()));
			}
			CompletableFuture<Suggestions> toReturn = new CompletableFuture<>();
			toReturn.complete(new Suggestions(StringRange.at(ctx.getRange().getEnd() + 1), suggestions));
			return toReturn;
		})*/);
	}
}
