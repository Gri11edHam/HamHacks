package net.grilledham.hamhacks.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.grilledham.hamhacks.command.Command;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.GameModeArgumentType;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class GamemodeCommand extends Command {
	
	public GamemodeCommand() {
		super("gamemode", "Changes your client-side gamemode", "gm");
	}
	
	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.then(argument("mode", GameModeArgumentType.gameMode()).executes(ctx -> {
			GameMode mode = ctx.getArgument("mode", GameMode.class);
			MinecraftClient.getInstance().interactionManager.setGameMode(mode);
			sendMsg(Text.of("Changed game mode to " + mode.getTranslatableName().getString()));
			return SINGLE_SUCCESS;
		}));
	}
}
