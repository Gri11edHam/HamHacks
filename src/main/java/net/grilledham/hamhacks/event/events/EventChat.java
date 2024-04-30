package net.grilledham.hamhacks.event.events;

import net.grilledham.hamhacks.event.EventCancelable;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;

public class EventChat extends EventCancelable {
	
	public static class EventChatReceived extends EventChat {
		
		public Text message;
		public MessageSignatureData signature;
		public MessageIndicator indicator;
		
		public EventChatReceived(Text message, MessageSignatureData signature, MessageIndicator indicator) {
			this.message = message;
			this.signature = signature;
			this.indicator = indicator;
		}
	}
	
	public static class EventChatSent extends EventChat {
		
		public String message;
		
		public EventChatSent(String message) {
			this.message = message;
		}
	}
}
