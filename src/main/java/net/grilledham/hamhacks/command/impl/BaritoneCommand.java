package net.grilledham.hamhacks.command.impl;

import baritone.api.BaritoneAPI;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.grilledham.hamhacks.command.Command;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

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
		builder.then(argument("command", StringArgumentType.greedyString()).executes((cmd) -> {
			String baritoneCmd;
			try {
				baritoneCmd = cmd.getArgument("command", String.class);
			} catch(Exception e) {
				baritoneCmd = "help";
			}
			MinecraftClient.getInstance().player.sendChatMessage(BaritoneAPI.getSettings().prefix.value + baritoneCmd, Text.empty());
			return SINGLE_SUCCESS;
		}));
	}
}
