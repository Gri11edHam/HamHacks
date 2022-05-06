package net.grilledham.hamhacks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.grilledham.hamhacks.util.ChatUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public abstract class Command {
	
	private final String name;
	private final String title;
	private final String description;
	private final List<String> aliases = new ArrayList<>();
	
	public Command(String name, String description, String... aliases) {
		this.name = name;
		this.title = Arrays.stream(name.split("-")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
		this.description = description;
		this.aliases.addAll(Arrays.asList(aliases));
	}
	
	public String getName() {
		return name;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public List<String> getAliases() {
		return aliases;
	}
	
	protected static <T> RequiredArgumentBuilder<CommandSource, T> argument(String name, ArgumentType<T> type) {
		return RequiredArgumentBuilder.argument(name, type);
	}
	
	protected static LiteralArgumentBuilder<CommandSource> literal(String name) {
		return LiteralArgumentBuilder.literal(name);
	}
	
	public void register(CommandDispatcher<CommandSource> dispatcher) {
		register(dispatcher, name);
		for(String alias : aliases) {
			register(dispatcher, alias);
		}
	}
	
	public void register(CommandDispatcher<CommandSource> dispatcher, String name) {
		LiteralArgumentBuilder<CommandSource> builder = LiteralArgumentBuilder.literal(name);
		builder.then(literal("help").executes(ctx -> {
			info(Text.of(description));
			return SINGLE_SUCCESS;
		}));
		build(builder);
		dispatcher.register(builder);
	}
	
	public abstract void build(LiteralArgumentBuilder<CommandSource> builder); // TODO: Suggestions in commands (idk how to do this yet/kinda confusing)
	
	public void sendMsg(Text message) {
		MutableText prefix = (MutableText)Text.of("[" + title + "]");
		prefix.setStyle(Style.EMPTY.withFormatting(Formatting.DARK_RED));
		MutableText separator = (MutableText)Text.of(" > ");
		separator.setStyle(Style.EMPTY.withFormatting(Formatting.DARK_GRAY));
		ChatUtil.sendMsg(prefix.append(separator), message);
	}
	
	public void info(Text message, Object... args) {
		MutableText prefix = (MutableText)Text.of("[" + title + "]");
		prefix.setStyle(Style.EMPTY.withFormatting(Formatting.DARK_RED));
		MutableText separator = (MutableText)Text.of(" > ");
		separator.setStyle(Style.EMPTY.withFormatting(Formatting.DARK_GRAY));
		ChatUtil.info(prefix.append(separator), message, args);
	}
	
	public void warning(Text message, Object... args) {
		MutableText prefix = (MutableText)Text.of("[" + title + "]");
		prefix.setStyle(Style.EMPTY.withFormatting(Formatting.DARK_RED));
		MutableText separator = (MutableText)Text.of(" > ");
		separator.setStyle(Style.EMPTY.withFormatting(Formatting.DARK_GRAY));
		ChatUtil.warning(prefix.append(separator), message, args);
	}
	
	public void error(Text message, Object... args) {
		MutableText prefix = (MutableText)Text.of("[" + title + "]");
		prefix.setStyle(Style.EMPTY.withFormatting(Formatting.DARK_RED));
		MutableText separator = (MutableText)Text.of(" > ");
		separator.setStyle(Style.EMPTY.withFormatting(Formatting.DARK_GRAY));
		ChatUtil.error(prefix.append(separator), message, args);
	}
}
