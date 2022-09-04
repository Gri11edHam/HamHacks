package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.event.events.EventChat;
import net.grilledham.hamhacks.modules.misc.ChatModule;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChatHud.class)
public class MixinChatHud extends DrawableHelper {
	
	@Shadow @Final private List<ChatHudLine.Visible> visibleMessages;
	private int lineIndex;
	
	@Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", at = @At("HEAD"), cancellable = true)
	public void addMessage(Text message, MessageSignatureData signature, int ticks, MessageIndicator indicator, boolean refresh, CallbackInfo ci) {
		EventChat.EventChatReceived event = new EventChat.EventChatReceived(message, signature, ticks, indicator, refresh);
		event.call();
		if(event.canceled) {
			ci.cancel();
		}
	}
	
	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"))
	public int getN(int index) {
		lineIndex = index;
		return index;
	}
	
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V", ordinal = 0))
	public void modifyBGColor(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int color) {
		if(ChatModule.getInstance().isEnabled() && ChatModule.getInstance().highlightUsername) {
			ChatHudLine.Visible line = visibleMessages.get(lineIndex);
			if(ChatModule.getInstance().shouldColorLine(line)) {
				int newRGB = ChatModule.getInstance().highlightUsernameColor.getRGB() - 0xff000000;
				int newAlpha = color >> 24;
				newAlpha = (int)((float)newAlpha * ChatModule.getInstance().highlightUsernameColor.getAlpha());
				newAlpha = newAlpha << 24;
				color = newAlpha + newRGB;
			}
		}
		fill(matrixStack, x1, y1, x2, y2, color);
	}
	
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V", ordinal = 1))
	public void removeIndicator(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int c) {
		if(ChatModule.getInstance().isEnabled() && ChatModule.getInstance().hideSigningStatus) {
			return;
		}
		fill(matrixStack, x1, y1, x2, y2, c);
	}
	
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/MessageIndicator;icon()Lnet/minecraft/client/gui/hud/MessageIndicator$Icon;"))
	public MessageIndicator.Icon removeIcon(MessageIndicator instance) {
		if(ChatModule.getInstance().isEnabled() && ChatModule.getInstance().hideUnsignedIndicator) {
			return null;
		}
		return instance.icon();
	}
}
