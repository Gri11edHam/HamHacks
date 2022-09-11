package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.mixininterface.IMultiplayerScreen;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.misc.AntiBan;
import net.grilledham.hamhacks.modules.render.Notifications;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.WarningScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(DisconnectedScreen.class)
public abstract class MixinDisconnectedScreen extends Screen {
	
	@Shadow private int reasonHeight;
	
	@Shadow @Final private Screen parent;
	private boolean enforceSecureChat = false;
	
	private MixinDisconnectedScreen(Text title) {
		super(title);
	}
	
	@ModifyVariable(method = "<init>", at = @At("LOAD"), index = 3, argsOnly = true)
	private Text modifyReason(Text reason) {
		if(reason.getContent() instanceof TranslatableTextContent) {
			String key = (((TranslatableTextContent)reason.getContent())).getKey();
			if(ModuleManager.getModule(AntiBan.class).isEnabled() && (key.equals("multiplayer.disconnect.missing_public_key") || key.equals("multiplayer.disconnect.invalid_public_key") || key.equals("multiplayer.disconnect.invalid_public_key_signature"))) {
				enforceSecureChat = true;
			}
		}
		if(enforceSecureChat) {
			ModuleManager.getModule(AntiBan.class).hasConnected = true;
			if(ModuleManager.getModule(AntiBan.class).joinEnforcedServers) {
				((IMultiplayerScreen)parent).reconnect();
				Notifications.notify(ModuleManager.getModule(AntiBan.class).getName(), "Connecting to unsafe server. To stop automatic connection to unsafe servers, disable Join Enforced Servers.");
			} else {
				client.setScreen(new WarningScreen(Text.translatable("hamhacks.menu.securedServerWarning"), Text.translatable("hamhacks.menu.securedServerWarning"), Text.translatable("hamhacks.menu.securedServerWarning")) {
					@Override
					protected void initButtons(int yOffset) {
						this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, 100 + yOffset, 150, 20, ScreenTexts.CANCEL, (button) -> {
							this.client.setScreen(new MultiplayerScreen(new TitleScreen()));
						}));
						this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, 100 + yOffset, 150, 20, ScreenTexts.PROCEED, (button) -> {
							if(checkbox != null && checkbox.isChecked()) {
								ModuleManager.getModule(AntiBan.class).joinEnforcedServers = true;
							}
							((IMultiplayerScreen)parent).reconnect();
						}));
					}
				});
			}
		}
		return reason;
	}
}
