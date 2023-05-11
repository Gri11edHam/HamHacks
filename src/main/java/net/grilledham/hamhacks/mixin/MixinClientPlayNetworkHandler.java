package net.grilledham.hamhacks.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.grilledham.hamhacks.command.CommandManager;
import net.grilledham.hamhacks.event.EventManager;
import net.grilledham.hamhacks.event.events.EventChat;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.Commands;
import net.grilledham.hamhacks.util.ChatUtil;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
	
	@Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
	public void sendChatMessage(String message, CallbackInfo ci) {
		EventChat.EventChatSent event = new EventChat.EventChatSent(message);
		EventManager.call(event);
		if(event.canceled) {
			ci.cancel();
			return;
		}
		String prefix = PageManager.getPage(Commands.class).getPrefix();
		boolean previewIsCommand = message.startsWith(prefix);
		if(message.startsWith(prefix) || previewIsCommand) {
			try {
				CommandManager.dispatch(message.substring(prefix.length()));
			} catch(CommandSyntaxException e) {
				ChatUtil.error(Text.of(e.getMessage()));
			}
			ci.cancel();
		}
	}
}
