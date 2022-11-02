package net.grilledham.hamhacks.modules.misc;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventChat;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.setting.BoolSetting;
import net.grilledham.hamhacks.setting.ColorSetting;
import net.grilledham.hamhacks.util.Color;
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
	}
}
