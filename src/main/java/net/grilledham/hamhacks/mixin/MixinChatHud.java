package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.event.events.EventChat;
import net.grilledham.hamhacks.mixininterface.IChat;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.misc.Chat;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ChatHud.class)
public abstract class MixinChatHud implements IChat {
	
	@Shadow @Final private List<ChatHudLine.Visible> visibleMessages;
	
	@Shadow @Final private List<ChatHudLine> messages;
	
	@Shadow public abstract void addMessage(Text message, @Nullable MessageSignatureData signatureData, @Nullable MessageIndicator indicator);
	
	@Unique
	private int lineIndex;
	@Unique
	private MessageIndicator indicator;
	
	@Unique
	private boolean calledFromMixin = false;
	
	@Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("HEAD"), cancellable = true)
	public void addMessage(Text message, MessageSignatureData signature, MessageIndicator indicator, CallbackInfo ci) {
		if(!calledFromMixin) {
			EventChat.EventChatReceived event = new EventChat.EventChatReceived(message, signature, indicator);
			event.call();
			ci.cancel();
			if(!event.canceled) {
				calledFromMixin = true;
				addMessage(event.message, event.signature, event.indicator);
			}
		} else {
			calledFromMixin = false;
		}
	}
	
	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"))
	public int getN(int index) {
		lineIndex = index;
		return index;
	}
	
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V", ordinal = 0))
	public void modifyBGColor(DrawContext instance, int x1, int y1, int x2, int y2, int color) {
		if(ModuleManager.getModule(Chat.class).isEnabled() && ModuleManager.getModule(Chat.class).highlightUsername.get()) {
			ChatHudLine.Visible line = visibleMessages.get(lineIndex);
			if(ModuleManager.getModule(Chat.class).shouldColorLine(line)) {
				int newRGB = ModuleManager.getModule(Chat.class).highlightUsernameColor.get().getRGB() - 0xff000000;
				int newAlpha = color >> 24;
				newAlpha = (int)((float)newAlpha * ModuleManager.getModule(Chat.class).highlightUsernameColor.get().getAlpha());
				newAlpha = newAlpha << 24;
				color = newAlpha + newRGB;
			}
		}
		instance.fill(x1, y1, x2, y2, color);
	}
	
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHudLine$Visible;indicator()Lnet/minecraft/client/gui/hud/MessageIndicator;"))
	public MessageIndicator getIndicator(ChatHudLine.Visible instance) {
		indicator = instance.indicator();
		return indicator;
	}
	
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V", ordinal = 1))
	public void removeIndicator(DrawContext instance, int x1, int y1, int x2, int y2, int c) {
		if(indicator == null) {
			return;
		}
		Chat chat = ModuleManager.getModule(Chat.class);
		if(chat.isEnabled()) {
			if(indicator == MessageIndicator.system()) {
				if(chat.hideSystemStatus.get()) {
					return;
				}
			} else if(indicator.icon() == MessageIndicator.Icon.CHAT_MODIFIED) {
				if(chat.hideModifiedStatus.get()) {
					return;
				}
			} else if(indicator == MessageIndicator.notSecure()) {
				if(chat.hideUnsignedStatus.get()) {
					return;
				}
			} else if(chat.hideOtherStatus.get()) {
				return;
			}
		}
		instance.fill(x1, y1, x2, y2, c);
	}
	
	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/MessageIndicator;icon()Lnet/minecraft/client/gui/hud/MessageIndicator$Icon;", ordinal = 0))
	public MessageIndicator.Icon removeIcon(MessageIndicator indicator) {
		Chat chat = ModuleManager.getModule(Chat.class);
		if(chat.isEnabled()) {
			if(indicator.icon() == MessageIndicator.Icon.CHAT_MODIFIED) {
				if(chat.hideModifiedStatusIcon.get()) {
					return null;
				}
			} else if(indicator == MessageIndicator.notSecure()) {
				if(chat.hideUnsignedStatusIcon.get()) {
					return null;
				}
			} else if(indicator != MessageIndicator.system()) {
				if(chat.hideOtherStatusIcon.get()) {
					return null;
				}
			}
		}
		return indicator.icon();
	}
	
	@Inject(method = "isXInsideIndicatorIcon", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
	public void removeIndicatorTooltip(double x, ChatHudLine.Visible line, MessageIndicator indicator, CallbackInfoReturnable<Boolean> cir) {
		Chat chat = ModuleManager.getModule(Chat.class);
		if(chat.isEnabled()) {
			if(indicator == MessageIndicator.system()) {
				if(chat.hideSystemStatus.get()) {
					cir.setReturnValue(false);
				}
			} else if(indicator.icon() == MessageIndicator.Icon.CHAT_MODIFIED) {
				if(chat.hideModifiedStatus.get()) {
					cir.setReturnValue(false);
				}
			} else if(indicator == MessageIndicator.notSecure()) {
				if(chat.hideUnsignedStatus.get()) {
					cir.setReturnValue(false);
				}
			} else if(chat.hideOtherStatus.get()) {
				cir.setReturnValue(false);
			}
		}
	}
	
	@Inject(method = "isXInsideIndicatorIcon", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
	public void removeIconTooltip(double x, ChatHudLine.Visible line, MessageIndicator indicator, CallbackInfoReturnable<Boolean> cir) {
		Chat chat = ModuleManager.getModule(Chat.class);
		if(chat.isEnabled()) {
			if(indicator.icon() == MessageIndicator.Icon.CHAT_MODIFIED) {
				if(chat.hideModifiedStatusIcon.get()) {
					cir.setReturnValue(false);
				}
			} else if(indicator == MessageIndicator.notSecure()) {
				if(chat.hideUnsignedStatusIcon.get()) {
					cir.setReturnValue(false);
				}
			} else if(indicator != MessageIndicator.system()) {
				if(chat.hideOtherStatusIcon.get()) {
					cir.setReturnValue(false);
				}
			}
		}
	}
	
	@Override
	public List<ChatHudLine> hamHacks$getMessages() {
		return messages;
	}
	
	@Override
	public List<ChatHudLine.Visible> hamHacks$getVisibleMessages() {
		return visibleMessages;
	}
}
