package net.grilledham.hamhacks.mixin;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.grilledham.hamhacks.command.CommandManager;
import net.grilledham.hamhacks.event.events.EventMotion;
import net.grilledham.hamhacks.modules.misc.AntiBanModule;
import net.grilledham.hamhacks.modules.misc.CommandModule;
import net.grilledham.hamhacks.util.ChatUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.network.message.*;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {
	
	@Inject(method = "sendMovementPackets", at = @At("HEAD"), cancellable = true)
	public void preMoveEvent(CallbackInfo ci) {
		EventMotion event = new EventMotion(EventMotion.Type.PRE);
		event.call();
		if(event.canceled) {
			ci.cancel();
		}
	}
	
	@Inject(method = "sendMovementPackets", at = @At("TAIL"), cancellable = true)
	public void postMoveEvent(CallbackInfo ci) {
		EventMotion event = new EventMotion(EventMotion.Type.POST);
		event.call();
		if(event.canceled) {
			ci.cancel();
		}
	}
	
	@Inject(method = "sendChatMessage(Ljava/lang/String;Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
	public void sendMessage(String message, Text preview, CallbackInfo ci) {
		String prefix = CommandModule.getInstance().getKey().getName();
		boolean previewIsCommand = preview != null && preview.getString().startsWith(prefix);
		if(message.startsWith(prefix) || previewIsCommand) {
			try {
				if(previewIsCommand) {
					CommandManager.dispatch(preview.getString().substring(prefix.length()));
				} else {
					CommandManager.dispatch(message.substring(prefix.length()));
				}
			} catch(CommandSyntaxException e) {
				ChatUtil.error(Text.of(e.getMessage()));
			}
			ci.cancel();
		}
	}
	
	@Inject(method = "signChatMessage", at = @At("HEAD"), cancellable = true)
	private void cancelSignMessage(MessageMetadata metadata, DecoratedContents content, LastSeenMessageList lastSeenMessages, CallbackInfoReturnable<MessageSignatureData> cir) {
		if(AntiBanModule.getInstance().isEnabled() && !AntiBanModule.getInstance().hasConnected) {
			cir.setReturnValue(MessageSignatureData.EMPTY);
		} else {
			if(AntiBanModule.getInstance().isEnabled()) {
			}
		}
	}
	
	@Inject(method = "signArguments", at = @At("HEAD"), cancellable = true)
	private void cancelSignMessage(MessageMetadata signer, ParseResults<CommandSource> parseResults, Text preview, LastSeenMessageList lastSeenMessages, CallbackInfoReturnable<ArgumentSignatureDataMap> cir) {
		if(AntiBanModule.getInstance().isEnabled() && !AntiBanModule.getInstance().hasConnected) {
			cir.setReturnValue(ArgumentSignatureDataMap.EMPTY);
		} else {
			if(AntiBanModule.getInstance().isEnabled()) {
			}
		}
	}
}
