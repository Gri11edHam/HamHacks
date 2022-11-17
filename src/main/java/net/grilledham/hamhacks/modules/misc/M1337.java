package net.grilledham.hamhacks.modules.misc;

import baritone.api.BaritoneAPI;
import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventChat;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.Commands;
import net.grilledham.hamhacks.setting.SelectionSetting;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class M1337 extends Module {
	
	@SelectionSetting(name = "hamhacks.module.1337.mode", options = {"hamhacks.module.1337.mode.basic", "hamhacks.module.1337.mode.advanced"})
	public int mode = 0;
	
	private boolean modify = true;
	
	private final Map<Character, String[]> basic = new HashMap<>();
	private final Map<Character, String[]> advanced = new HashMap<>();
	
	private final Random r;
	
	public M1337() {
		super(Text.translatable("hamhacks.module.1337"), Category.MISC, new Keybind(0));
		r = new Random();
		
		// b451c
		basic.put('A', new String[]{"4"});
		basic.put('a', new String[]{"4"});
		basic.put('E', new String[]{"3"});
		basic.put('e', new String[]{"3"});
		basic.put('G', new String[]{"6"});
		basic.put('g', new String[]{"6"});
		basic.put('I', new String[]{"1"});
		basic.put('i', new String[]{"1"});
		basic.put('L', new String[]{"1"});
		basic.put('l', new String[]{"1"});
		basic.put('O', new String[]{"0"});
		basic.put('o', new String[]{"0"});
		basic.put('S', new String[]{"5"});
		basic.put('s', new String[]{"5"});
		basic.put('T', new String[]{"7"});
		basic.put('t', new String[]{"7"});
		basic.put('Z', new String[]{"2"});
		basic.put('z', new String[]{"2"});
		
		// @|)\/4|\|(3|]
		advanced.put('A', new String[]{"4", "@", "/\\", "/-\\", "^", "ƛ"});
		advanced.put('a', new String[]{"4", "@", "/\\", "/-\\", "^", "ƛ"});
		advanced.put('B', new String[]{"8", "|3", "|>", "13", "I3", "ß"});
		advanced.put('b', new String[]{"8", "|3", "|>", "13", "I3", "ß"});
		advanced.put('C', new String[]{"(", "[", "<", "©", "¢"});
		advanced.put('c', new String[]{"(", "[", "<", "©", "¢"});
		advanced.put('D', new String[]{"|)", "|]", "Ð", "1)"});
		advanced.put('d', new String[]{"|)", "|]", "Ð", "1)"});
		advanced.put('E', new String[]{"3", "€", "&", "£"});
		advanced.put('e', new String[]{"3", "€", "&", "£"});
		advanced.put('F', new String[]{"|=", "PH", "|\""});
		advanced.put('f', new String[]{"|=", "PH", "|\""});
		advanced.put('G', new String[]{"6", "&", "9"});
		advanced.put('g', new String[]{"6", "&", "9"});
		advanced.put('H', new String[]{"4", "|-|", "#", "}{", "]-[", "/-/", ")-("});
		advanced.put('h', new String[]{"4", "|-|", "#", "}{", "]-[", "/-/", ")-("});
		advanced.put('I', new String[]{"1", "!", "|", "]["});
		advanced.put('i', new String[]{"1", "!", "|", "]["});
		advanced.put('J', new String[]{"_|"});
		advanced.put('j', new String[]{"_|"});
		advanced.put('K', new String[]{"|<", "|{", "|(", "X"});
		advanced.put('k', new String[]{"|<", "|{", "|(", "X"});
		advanced.put('L', new String[]{"1", "|_", "£", "|"});
		advanced.put('l', new String[]{"1", "|_", "£", "|"});
		advanced.put('M', new String[]{"/\\/\\", "/v\\", "|V|", "|\\/|", "]V[", "AA", "/|\\", "^^", "|Y|"});
		advanced.put('m', new String[]{"/\\/\\", "/v\\", "|V|", "|\\/|", "]V[", "AA", "/|\\", "^^", "|Y|"});
		advanced.put('N', new String[]{"|\\|", "/\\/", "/V", "|V"});
		advanced.put('n', new String[]{"|\\|", "/\\/", "/V", "|V"});
		advanced.put('O', new String[]{"0", "()", "[]", "*", "<>"});
		advanced.put('o', new String[]{"0", "()", "[]", "*", "<>"});
		advanced.put('P', new String[]{"|*", "|>", "|D", "|?"});
		advanced.put('p', new String[]{"|*", "|>", "|D", "|?"});
		advanced.put('Q', new String[]{"0_", "0,"});
		advanced.put('q', new String[]{"0_", "0,"});
		advanced.put('R', new String[]{"2", "|2", "12", "®"});
		advanced.put('r', new String[]{"2", "|2", "12", "®"});
		advanced.put('S', new String[]{"5", "$", /*"§", doesn't work in mc chat :( */ "?", "Σ"});
		advanced.put('s', new String[]{"5", "$", "?", "Σ"});
		advanced.put('T', new String[]{"7", "+", "†", "'|'"});
		advanced.put('t', new String[]{"7", "+", "†", "'|'"});
		advanced.put('U', new String[]{"|_|", "µ", "v"});
		advanced.put('u', new String[]{"|_|", "µ", "v"});
		advanced.put('V', new String[]{"\\/", "|/", "\\|"});
		advanced.put('v', new String[]{"\\/", "|/", "\\|"});
		advanced.put('W', new String[]{"\\/\\/", "VV", "\\A/", "UU", "\\^/", "\\|/"});
		advanced.put('w', new String[]{"\\/\\/", "VV", "\\A/", "UU", "\\^/", "\\|/"});
		advanced.put('X', new String[]{"><", ")(", "}{", "%", "]["});
		advanced.put('x', new String[]{"><", ")(", "}{", "%", "]["});
		advanced.put('Y', new String[]{"`/", "9"});
		advanced.put('y', new String[]{"`/", "9"});
		advanced.put('Z', new String[]{"2", "`/_"});
		advanced.put('z', new String[]{"2", "`/_"});
	}
	
	@EventListener
	public void onChat(EventChat.EventChatSent e) {
		if(mc.player == null || e.message.startsWith(PageManager.getPage(Commands.class).prefix.getCombinedString()) || e.message.startsWith(BaritoneAPI.getSettings().prefix.value)) return;
		if(modify) {
			e.canceled = true;
			modify = false;
			mc.player.sendChatMessage(f1337(e.message), e.preview);
		} else {
			modify = true;
		}
	}
	
	private String f1337(String msg) {
		StringBuilder newMsg = new StringBuilder();
		for(int i = 0; i < msg.length(); i++) {
			String[] leetChars = switch(mode) {
				case 0 -> basic.getOrDefault(msg.charAt(i), new String[] { String.valueOf(msg.charAt(i)) });
				case 1 -> advanced.getOrDefault(msg.charAt(i), new String[] { String.valueOf(msg.charAt(i)) });
				default -> new String[] { String.valueOf(msg.charAt(i)) };
			};
			newMsg.append(leetChars[r.nextInt(leetChars.length)]);
		}
		return newMsg.toString();
	}
}
