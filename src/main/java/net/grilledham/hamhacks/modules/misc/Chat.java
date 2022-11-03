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

public class Chat extends Module {
	
	@BoolSetting(name = "hamhacks.module.chat.pingOnMention", category = "hamhacks.module.chat.category.username")
	public boolean pingOnMention = false;
	
	@BoolSetting(name = "hamhacks.module.chat.highlightUsername", category = "hamhacks.module.chat.category.username")
	public boolean highlightUsername = false;
	
	@ColorSetting(
			name = "hamhacks.module.chat.highlightUsernameColor", category = "hamhacks.module.chat.category.username",
			dependsOn = "highlightUsername"
	)
	public Color highlightUsernameColor = Color.getYellow();
	
	@BoolSetting(name = "hamhacks.module.chat.stackSpam", category = "hamhacks.module.chat.category.spam", defaultValue = true)
	public boolean stackSpam = true;
	
	@BoolSetting(name = "hamhacks.module.chat.consecutiveOnly", category = "hamhacks.module.chat.category.spam", dependsOn = "stackSpam")
	public boolean consecutiveOnly;
	
	@NumberSetting(name = "hamhacks.module.chat.spamTime", category = "hamhacks.module.chat.category.spam",
			defaultValue = 30,
			min = 5,
			max = 60,
			step = 1,
			forceStep = false,
			dependsOn = {"stackSpam", "!consecutiveOnly"}
	)
	public float spamTime = 30;
	
	
	@BoolSetting(name = "hamhacks.module.chat.hideSystemStatus", category = "hamhacks.module.chat.category.status")
	public boolean hideSystemStatus = false;
	
	@BoolSetting(name = "hamhacks.module.chat.hideModifiedStatus", category = "hamhacks.module.chat.category.status")
	public boolean hideModifiedStatus = false;
	
	@BoolSetting(name = "hamhacks.module.chat.hideModifiedStatusIcon", category = "hamhacks.module.chat.category.status")
	public boolean hideModifiedStatusIcon = false;
	
	@BoolSetting(name = "hamhacks.module.chat.hideUnsignedStatus", category = "hamhacks.module.chat.category.status")
	public boolean hideUnsignedStatus = false;
	
	@BoolSetting(name = "hamhacks.module.chat.hideUnsignedStatusIcon", category = "hamhacks.module.chat.category.status")
	public boolean hideUnsignedStatusIcon = false;
	
	@BoolSetting(name = "hamhacks.module.chat.hideOtherStatus", category = "hamhacks.module.chat.category.status")
	public boolean hideOtherStatus = false;
	
	@BoolSetting(name = "hamhacks.module.chat.hideOtherStatusIcon", category = "hamhacks.module.chat.category.status")
	public boolean hideOtherStatusIcon = false;
	
	private final List<String> sentMessages = new ArrayList<>();
	
	private final Map<String, Long> lastSent = new HashMap<>();
	private final Map<String, Integer> timesSent = new HashMap<>();
	private final Map<String, Text> lastTexts = new HashMap<>();
	
	private String lastMessage = "";
	private int times = 1;
	private Text lastText = Text.literal("");
	
	public Chat() {
		super(Text.translatable("hamhacks.module.chat"), Category.MISC, new Keybind(0));
	}
	
	public boolean shouldColorLine(ChatHudLine.Visible line) {
		StringBuilder sb = new StringBuilder();
		line.content().accept((index, style, codePoint) -> {
			sb.append((char)codePoint);
			return true;
		});
		String username = (ModuleManager.getModule(NameHider.class).isEnabled() ? ModuleManager.getModule(NameHider.class).fakeName : MinecraftClient.getInstance().getSession().getUsername());
		return sb.toString().contains(username) && !sentMessages.contains(sb.toString());
	}
	
	@EventListener
	public void sendChat(EventChat.EventChatSent e) {
		String username = (ModuleManager.getModule(NameHider.class).isEnabled() ? ModuleManager.getModule(NameHider.class).fakeName : MinecraftClient.getInstance().getSession().getUsername());
		sentMessages.add(e.preview == null ? "<" + username + "> " + e.message : e.preview.getString());
	}
	
	@EventListener
	public void receiveChat(EventChat.EventChatReceived e) {
		if(pingOnMention && e.message.contains(mc.player.getName()) && (sentMessages == null || !sentMessages.contains(e.message.getString()))) {
			mc.getSoundManager().play(new PositionedSoundInstance(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP.getId(), SoundCategory.VOICE, 1, 1, new Random() {
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
		if(stackSpam) {
			String message = ChatUtil.unformat(e.message.getString());
			if(consecutiveOnly) {
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
					if(System.currentTimeMillis() - t > spamTime * 1000) {
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
		List<ChatHudLine> messages = ((IChat)chat).getMessages();
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
			((IChat)chat).getMessages().remove(index);
			
			List<OrderedText> list = ChatMessages.breakRenderedChatMessageLines(originalMessage, MathHelper.floor((double)mc.inGameHud.getChatHud().getWidth() / mc.inGameHud.getChatHud().getChatScale()), mc.textRenderer);
			int lines = list.size();
			
			for (int i = 0; i < lines; i++) {
				((IChat)chat).getVisibleMessages().remove(index);
			}
		}
	}
}
