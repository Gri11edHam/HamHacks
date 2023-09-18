package net.grilledham.hamhacks.command.impl;

import baritone.api.BaritoneAPI;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.grilledham.hamhacks.command.Command;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class BaritoneCommand extends Command {
	
	public BaritoneCommand() {
		super("baritone", "google maps for block game", "b");
	}
	
	@Override
	public void register(CommandDispatcher<CommandSource> dispatcher, String name) {
		LiteralArgumentBuilder<CommandSource> builder = LiteralArgumentBuilder.literal(name);
		build(builder);
		dispatcher.register(builder);
	}
	
	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.then(argument("command", StringArgumentType.word()).executes((cmd) -> {
			String baritoneCmd;
			try {
				baritoneCmd = cmd.getArgument("command", String.class);
			} catch(Exception e) {
				baritoneCmd = "help";
			}
			BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute(baritoneCmd);
			return SINGLE_SUCCESS;
		}).suggests((ctx, b) -> {
			for(String s : BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().tabComplete(b.getRemaining()).toList()) { // doesn't quite work
				b.suggest(s);
			}
			return b.buildFuture();
		}));
	}
}
