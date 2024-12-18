package net.grilledham.hamhacks.modules.misc;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventChat;
import net.grilledham.hamhacks.mixininterface.IChat;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.setting.BoolSetting;
import net.grilledham.hamhacks.setting.ColorSetting;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.grilledham.hamhacks.setting.SettingCategory;
import net.grilledham.hamhacks.util.ChatUtil;
import net.grilledham.hamhacks.util.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Chat extends Module {
	
	private final SettingCategory USERNAME_CATEGORY = new SettingCategory("hamhacks.module.chat.category.username");
	
	private final BoolSetting pingOnMention = new BoolSetting("hamhacks.module.chat.pingOnMention", false, () -> true);
	
	public final BoolSetting highlightUsername = new BoolSetting("hamhacks.module.chat.highlightUsername", false, () -> true);
	
	public final ColorSetting highlightUsernameColor = new ColorSetting("hamhacks.module.chat.highlightUsernameColor", Color.getYellow(), highlightUsername::get);
	
	private final SettingCategory SPAM_CATEGORY = new SettingCategory("hamhacks.module.chat.category.spam");
	
	private final BoolSetting stackSpam = new BoolSetting("hamhacks.module.chat.stackSpam", true, () -> true);
	
	private final BoolSetting consecutiveOnly = new BoolSetting("hamhacks.module.chat.consecutiveOnly", false, stackSpam::get);
	
	private final NumberSetting spamTime = new NumberSetting("hamhacks.module.chat.spamTime", 30, () -> stackSpam.get() && !consecutiveOnly.get(), 5, 60, 1, false);
	
	
	private final SettingCategory STATUS_CATEGORY = new SettingCategory("hamhacks.module.chat.category.status");
	
	public final BoolSetting hideSystemStatus = new BoolSetting("hamhacks.module.chat.hideSystemStatus", false, () -> true);
	
	public final BoolSetting hideModifiedStatus = new BoolSetting("hamhacks.module.chat.hideModifiedStatus", false, () -> true);
	
	public final BoolSetting hideModifiedStatusIcon = new BoolSetting("hamhacks.module.chat.hideModifiedStatusIcon", false, () -> true);
	
	public final BoolSetting hideUnsignedStatus = new BoolSetting("hamhacks.module.chat.hideUnsignedStatus", false, () -> true);
	
	public final BoolSetting hideUnsignedStatusIcon = new BoolSetting("hamhacks.module.chat.hideUnsignedStatusIcon", false, () -> true);
	
	public final BoolSetting hideOtherStatus = new BoolSetting("hamhacks.module.chat.hideOtherStatus", false, () -> true);
	
	public final BoolSetting hideOtherStatusIcon = new BoolSetting("hamhacks.module.chat.hideOtherStatusIcon", false, () -> true);
	
	private final List<String> sentMessages = new ArrayList<>();
	
	private final Map<String, Long> lastSent = new HashMap<>();
	private final Map<String, Integer> timesSent = new HashMap<>();
	private final Map<String, Text> lastTexts = new HashMap<>();
	
	private String lastMessage = "";
	private int times = 1;
	private Text lastText = Text.literal("");
	
	public Chat() {
		super(Text.translatable("hamhacks.module.chat"), Category.MISC, new Keybind(0));
		settingCategories.add(0, USERNAME_CATEGORY);
		USERNAME_CATEGORY.add(pingOnMention);
		USERNAME_CATEGORY.add(highlightUsername);
		USERNAME_CATEGORY.add(highlightUsernameColor);
		settingCategories.add(1, SPAM_CATEGORY);
		SPAM_CATEGORY.add(stackSpam);
		SPAM_CATEGORY.add(consecutiveOnly);
		SPAM_CATEGORY.add(spamTime);
		settingCategories.add(2, STATUS_CATEGORY);
		STATUS_CATEGORY.add(hideSystemStatus);
		STATUS_CATEGORY.add(hideModifiedStatus);
		STATUS_CATEGORY.add(hideModifiedStatusIcon);
		STATUS_CATEGORY.add(hideUnsignedStatus);
		STATUS_CATEGORY.add(hideUnsignedStatusIcon);
		STATUS_CATEGORY.add(hideOtherStatus);
		STATUS_CATEGORY.add(hideOtherStatusIcon);
	}
	
	public boolean shouldColorLine(ChatHudLine.Visible line) {
		StringBuilder sb = new StringBuilder();
		line.content().accept((index, style, codePoint) -> {
			sb.append((char)codePoint);
			return true;
		});
		String username = (ModuleManager.getModule(NameHider.class).isEnabled() ? ModuleManager.getModule(NameHider.class).fakeName.get() : MinecraftClient.getInstance().getSession().getUsername());
		Matcher m = Pattern.compile("^(.*)( \\([0-9]+\\))$").matcher(sb.toString());
		String s = m.matches() ? m.group(1) : sb.toString();
		return sb.toString().contains(username) && !sentMessages.contains(s);
	}
	
	@EventListener
	public void sendChat(EventChat.EventChatSent e) {
		String username = (ModuleManager.getModule(NameHider.class).isEnabled() ? ModuleManager.getModule(NameHider.class).fakeName.get() : MinecraftClient.getInstance().getSession().getUsername());
		sentMessages.add("<" + username + "> " + e.message);
	}
	
	@EventListener
	public void receiveChat(EventChat.EventChatReceived e) {
		Matcher matcher = Pattern.compile("^(.*)( \\([0-9]+\\))$").matcher(e.message.getString());
		String msg = matcher.matches() ? matcher.group(1) : e.message.getString();
		if(pingOnMention.get() && msg.contains(MinecraftClient.getInstance().getSession().getUsername())
				&& (sentMessages == null || !sentMessages.contains(
						ModuleManager.getModule(NameHider.class).isEnabled()
								? msg.replaceFirst(MinecraftClient.getInstance().getSession().getUsername(), ModuleManager.getModule(NameHider.class).fakeName.get())
								: msg
		))) {
			mc.getSoundManager().play(new PositionedSoundInstance(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP.id(), SoundCategory.VOICE, 1, 1, new Random() {
				@Override
				public Random split() {
					return this;
				}
				
				@Override
				public RandomSplitter nextSplitter() {
					return null;
				}
				
				@Override
				public void setSeed(long seed) {
				
				}
				
				@Override
				public int nextInt() {
					return 0;
				}
				
				@Override
				public int nextInt(int bound) {
					return 0;
				}
				
				@Override
				public long nextLong() {
					return 0;
				}
				
				@Override
				public boolean nextBoolean() {
					return false;
				}
				
				@Override
				public float nextFloat() {
					return 0;
				}
				
				@Override
				public double nextDouble() {
					return 0;
				}
				
				@Override
				public double nextGaussian() {
					return 0;
				}
			}, false, 0, SoundInstance.AttenuationType.LINEAR, 0, 0, 0, true));
		}
		if(stackSpam.get()) {
			String message = ChatUtil.unformat(e.message.getString());
			if(consecutiveOnly.get()) {
				if(lastMessage.equals(message)) {
					removeLastMessage(null);
					e.message = e.message.copy().append(Text.literal(" (" + ++times + ")").formatted(Formatting.GRAY));
				} else {
					times = 1;
				}
				lastMessage = message;
				// don't want to waste memory
				lastSent.clear();
				timesSent.clear();
				lastTexts.clear();
			} else {
				if(lastSent.containsKey(message)) {
					int times = timesSent.getOrDefault(message, 1);
					removeLastMessage(message);
					e.message = e.message.copy().append(Text.literal(" (" + ++times + ")").formatted(Formatting.GRAY));
					timesSent.put(message, times);
				}
				lastSent.put(message, System.currentTimeMillis());
				// remove if we haven't seen the message for some time
				List<String> toRemove = new ArrayList<>();
				lastSent.forEach((m, t) -> {
					if(System.currentTimeMillis() - t > spamTime.get() * 1000) {
						toRemove.add(m);
					}
				});
				for(String s : toRemove) {
					lastSent.remove(s);
					timesSent.remove(s);
					lastTexts.remove(s);
				}
			}
			lastText = e.message.copy();
			lastTexts.put(message, lastText);
		}
	}
	
	private void removeLastMessage(String msg) {
		ChatHud chat = mc.inGameHud.getChatHud();
		List<ChatHudLine> messages = ((IChat)chat).hamHacks$getMessages();
		int index = -1;
		MutableText originalMessage = null;
		for(ChatHudLine message : messages) {
			MutableText m = message.content().copy();
			String oldMessage = m.getString();
			String newMessage = msg == null ? lastText.getString() : lastTexts.get(msg).getString();
			if(oldMessage.equals(newMessage)) {
				originalMessage = m.copy();
				index = messages.indexOf(message);
				break;
			}
		}
		if(index >= 0) {
			((IChat)chat).hamHacks$getMessages().remove(index);
			
			List<OrderedText> list = ChatMessages.breakRenderedChatMessageLines(originalMessage, MathHelper.floor((double)mc.inGameHud.getChatHud().getWidth() / mc.inGameHud.getChatHud().getChatScale()), mc.textRenderer);
			int lines = list.size();
			
			for (int i = 0; i < lines; i++) {
				((IChat)chat).hamHacks$getVisibleMessages().remove(index);
			}
		}
	}
}
