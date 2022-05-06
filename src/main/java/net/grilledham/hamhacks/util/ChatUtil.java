package net.grilledham.hamhacks.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

public class ChatUtil {
	
	public static void sendMsg(Text prefix, Text message, Object... args) {
		String newMessage = message.getString();
		for(int i = 0; i < args.length; i++) {
			newMessage = newMessage.replaceAll("\\{" + i + "}", args[i].toString());
		}
		message = Text.of(newMessage);
		sendMsg(prefix, message);
	}
	
	public static void sendMsg(Text prefix, Text message, Formatting... formatting) {
		BaseText text = new LiteralText("");
		if(prefix != null) {
			text.append(prefix);
		}
		if(formatting != null) {
			((MutableText)message).setStyle(Style.EMPTY.withFormatting(formatting));
		}
		text.append(message);
		MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(text);
	}
	
	public static void info(Text message) {
		sendMsg(null, message, Formatting.GRAY);
	}
	
	public static void info(Text prefix, Text message) {
		sendMsg(prefix, message, Formatting.GRAY);
	}
	
	public static void info(Text prefix, Text message, Object... args) {
		String newMessage = message.getString();
		for(int i = 0; i < args.length; i++) {
			newMessage = newMessage.replaceAll("\\{" + i + "}", args[i].toString());
		}
		message = Text.of(newMessage);
		sendMsg(prefix, message, Formatting.GRAY);
	}
	
	public static void warning(Text message) {
		sendMsg(null, message, Formatting.YELLOW);
	}
	
	public static void warning(Text prefix, Text message) {
		sendMsg(prefix, message, Formatting.YELLOW);
	}
	
	public static void warning(Text prefix, Text message, Object... args) {
		String newMessage = message.getString();
		for(int i = 0; i < args.length; i++) {
			newMessage = newMessage.replaceAll("\\{" + i + "}", args[i].toString());
		}
		message = Text.of(newMessage);
		sendMsg(prefix, message, Formatting.YELLOW);
	}
	
	public static void error(Text message) {
		sendMsg(null, message, Formatting.RED);
	}
	
	public static void error(Text prefix, Text message) {
		sendMsg(prefix, message, Formatting.RED);
	}
	
	public static void error(Text prefix, Text message, Object... args) {
		String newMessage = message.getString();
		for(int i = 0; i < args.length; i++) {
			newMessage = newMessage.replaceAll("\\{" + i + "}", args[i].toString());
		}
		message = Text.of(newMessage);
		sendMsg(prefix, message, Formatting.RED);
	}
}
