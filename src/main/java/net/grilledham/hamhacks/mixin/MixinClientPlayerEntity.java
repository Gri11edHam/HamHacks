package net.grilledham.hamhacks.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.grilledham.hamhacks.command.CommandManager;
import net.grilledham.hamhacks.event.EventManager;
import net.grilledham.hamhacks.event.events.EventChat;
import net.grilledham.hamhacks.event.events.EventMotion;
import net.grilledham.hamhacks.mixininterface.IClientEntityPlayer;
import net.grilledham.hamhacks.modules.misc.AntiBanModule;
import net.grilledham.hamhacks.modules.misc.CommandModule;
import net.grilledham.hamhacks.util.ChatUtil;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandSource;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.message.*;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity implements IClientEntityPlayer {
	
	@Shadow protected abstract boolean isWalking();
	
	public MixinClientPlayerEntity(ClientWorld world, GameProfile profile, @Nullable PlayerPublicKey publicKey) {
		super(world, profile, publicKey);
	}
	
	@Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
	public void preMoveEvent(CallbackInfo ci) {
		EventMotion event = new EventMotion(EventMotion.Type.PRE);
		event.call();
		if(event.canceled) {
			ci.cancel();
		}
	}
	
	@Inject(method = "tickMovement", at = @At("TAIL"), cancellable = true)
	public void postMoveEvent(CallbackInfo ci) {
		EventMotion event = new EventMotion(EventMotion.Type.POST);
		event.call();
		if(event.canceled) {
			ci.cancel();
		}
	}
	
	@Inject(method = "sendChatMessage(Ljava/lang/String;Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
	public void sendMessage(String message, Text preview, CallbackInfo ci) {
		EventChat.EventChatSent event = new EventChat.EventChatSent(message, preview);
		EventManager.call(event);
		if(event.canceled) {
			ci.cancel();
			return;
		}
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
		}
	}
	
	@Inject(method = "signArguments", at = @At("HEAD"), cancellable = true)
	private void cancelSignMessage(MessageMetadata signer, ParseResults<CommandSource> parseResults, Text preview, LastSeenMessageList lastSeenMessages, CallbackInfoReturnable<ArgumentSignatureDataMap> cir) {
		if(AntiBanModule.getInstance().isEnabled() && !AntiBanModule.getInstance().hasConnected) {
			cir.setReturnValue(ArgumentSignatureDataMap.EMPTY);
		}
	}
	
	@Override
	public boolean walking() {
		return isWalking();
	}
}
