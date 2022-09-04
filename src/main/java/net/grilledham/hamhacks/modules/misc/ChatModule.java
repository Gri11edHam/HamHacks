package net.grilledham.hamhacks.modules.misc;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventChat;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.Color;
import net.grilledham.hamhacks.util.setting.BoolSetting;
import net.grilledham.hamhacks.util.setting.ColorSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;

import java.util.ArrayList;
import java.util.List;

public class ChatModule extends Module {
	
	@BoolSetting(name = "hamhacks.module.chat.pingOnMention")
	public boolean pingOnMention = false;
	
	@BoolSetting(name = "hamhacks.module.chat.highlightUsername")
	public boolean highlightUsername = false;
	
	@ColorSetting(
			name = "hamhacks.module.chat.highlightUsernameColor",
			dependsOn = "highlightUsername"
	)
	public Color highlightUsernameColor = Color.getYellow();
	
	@BoolSetting(name = "hamhacks.module.chat.hideUnsignedIndicator")
	public boolean hideUnsignedIndicator = false;
	
	@BoolSetting(name = "hamhacks.module.chat.hideSigningStatus")
	public boolean hideSigningStatus = false;
	
	private final List<String> sentMessages = new ArrayList<>();
	
	private static ChatModule INSTANCE;
	
	public ChatModule() {
		super(Text.translatable("hamhacks.module.chat"), Category.MISC, new Keybind(0));
		INSTANCE = this;
	}
	
	public static ChatModule getInstance() {
		return INSTANCE;
	}
	
	public boolean shouldColorLine(ChatHudLine.Visible line) {
		StringBuilder sb = new StringBuilder();
		line.content().accept((index, style, codePoint) -> {
			sb.append((char)codePoint);
			return true;
		});
		String username = (NameHiderModule.getInstance().isEnabled() ? NameHiderModule.getInstance().fakeName : MinecraftClient.getInstance().getSession().getUsername());
		return sb.toString().contains(username) && !sentMessages.contains(sb.toString());
	}
	
	@EventListener
	public void sendChat(EventChat.EventChatSent e) {
		String username = (NameHiderModule.getInstance().isEnabled() ? NameHiderModule.getInstance().fakeName : MinecraftClient.getInstance().getSession().getUsername());
		sentMessages.add(e.preview == null ? "<" + username + "> " + e.message : e.preview.getString());
	}
	
	@EventListener
	public void receiveChat(EventChat.EventChatReceived e) {
		if(e.message.contains(mc.player.getName()) && (sentMessages == null || !e.message.getString().equals(sentMessages))) {
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
	}
}
