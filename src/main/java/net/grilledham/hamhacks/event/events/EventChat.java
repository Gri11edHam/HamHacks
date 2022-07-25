package net.grilledham.hamhacks.event.events;

import net.grilledham.hamhacks.event.EventCancelable;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;

public class EventChat extends EventCancelable {
	
	public static class EventChatReceived extends EventChat {
		
		public Text message;
		public MessageSignatureData signature;
		public int ticks;
		public MessageIndicator indicator;
		public boolean refresh;
		
		public EventChatReceived(Text message, MessageSignatureData signature, int ticks, MessageIndicator indicator, boolean refresh) {
			this.message = message;
			this.signature = signature;
			this.ticks = ticks;
			this.indicator = indicator;
			this.refresh = refresh;
		}
	}
	
	public static class EventChatSent extends EventChat {
		
		public String message;
		public Text preview;
		
		public EventChatSent(String message, Text preview) {
			this.message = message;
			this.preview = preview;
		}
	}
}
