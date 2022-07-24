package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.modules.misc.AntiBanModule;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisconnectedScreen.class)
public abstract class MixinDisconnectedScreen extends Screen {
	
	@Shadow private int reasonHeight;
	
	@Shadow @Final private Screen parent;
	private boolean enforceSecureChat = false;
	
	private MixinDisconnectedScreen(Text title) {
		super(title);
	}
	
	@Inject(method = "<init>", at = @At("TAIL"))
	public void init(Screen parent, Text title, Text reason, CallbackInfo ci) {
		if(AntiBanModule.getInstance().hasConnected) {
			AntiBanModule.getInstance().hasConnected = false;
		}
		if(reason.getContent() instanceof TranslatableTextContent) {
			System.out.println(((TranslatableTextContent)reason.getContent()).getKey());
			if(((TranslatableTextContent)reason.getContent()).getKey().equals("multiplayer.disconnect.invalid_public_key_signature")) {
				enforceSecureChat = true;
			}
		}
		if(enforceSecureChat) {
			AntiBanModule.getInstance().hasConnected = true;
			if(AntiBanModule.getInstance().joinEnforcedServers.getValue()) {
				((MultiplayerScreen)parent).connect();
			}
		}
	}
	
	@ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/DisconnectedScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"))
	public Element moveBackButton(Element element) {
		if(enforceSecureChat) {
			((ButtonWidget)element).y += 22;
		}
		return element;
	}
	
	@Inject(method = "init", at = @At("TAIL"))
	public void addContinueButton(CallbackInfo ci) {
		if(enforceSecureChat) {
			addDrawableChild(new ButtonWidget(this.width / 2 - 100, Math.min(this.height / 2 + this.reasonHeight / 2 + 9, this.height - 30), 200, 20, Text.translatable("hamhacks.menu.disconnected.continueAnyways"), (button) -> ((MultiplayerScreen)parent).connect()));
		}
	}
}
