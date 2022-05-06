package net.grilledham.hamhacks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.grilledham.hamhacks.command.impl.BindCommand;
import net.grilledham.hamhacks.command.impl.HelpCommand;
import net.grilledham.hamhacks.command.impl.ToggleCommand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;

import java.util.*;

public class CommandManager {
	
	private static final CommandDispatcher<CommandSource> DISPATCHER = new CommandDispatcher<>();
	private static final CommandSource SOURCE = new ClientSideCommandSource(MinecraftClient.getInstance());
	private static final List<Command> commands = new ArrayList<>();
	private static final Map<Class<? extends Command>, Command> instances = new HashMap<>();
	
	public static void init() {
		addCommand(new HelpCommand());
		addCommand(new ToggleCommand());
		addCommand(new BindCommand());
		
		commands.sort(Comparator.comparing(Command::getName));
	}
	
	public static void dispatch(String message) throws CommandSyntaxException {
		dispatch(message, new ClientSideCommandSource(MinecraftClient.getInstance()));
	}
	
	public static void dispatch(String message, CommandSource source) throws CommandSyntaxException {
		ParseResults<CommandSource> results = DISPATCHER.parse(message, source);
		DISPATCHER.execute(results);
	}
	
	public static void addCommand(Command command) {
		commands.removeIf(kommand -> kommand.getName().equals(command.getName()));
		instances.values().removeIf(kommand -> kommand.getName().equals(command.getName()));
		
		command.register(DISPATCHER);
		commands.add(command);
		instances.put(command.getClass(), command);
	}
	
	public static Command getCommand(String name) {
		return commands.stream().filter(command -> command.getName().equals(name)).findFirst().orElse(null);
	}
	
	public static CommandDispatcher<CommandSource> getDispatcher() {
		return DISPATCHER;
	}
	
	public static CommandSource getSource() {
		return SOURCE;
	}
	
	public static List<Command> getCommands() {
		return commands;
	}
	
	private static final class ClientSideCommandSource extends ClientCommandSource {
		public ClientSideCommandSource(MinecraftClient client) {
			super(null, client);
		}
	}
}
