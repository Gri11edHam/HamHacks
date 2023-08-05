package net.grilledham.hamhacks.mixininterface;

import net.minecraft.client.gui.hud.ChatHudLine;

import java.util.List;

public interface IChat {
	
	List<ChatHudLine> hamHacks$getMessages();
	
	List<ChatHudLine.Visible> hamHacks$getVisibleMessages();
}
