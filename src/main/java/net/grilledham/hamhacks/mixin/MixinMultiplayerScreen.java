package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.gui.screen.impl.WaitingToConnectScreen;
import net.grilledham.hamhacks.mixininterface.IMultiplayerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public abstract class MixinMultiplayerScreen extends Screen implements IMultiplayerScreen {
	
	@Shadow private ServerInfo selectedEntry;
	@Shadow protected MultiplayerServerListWidget serverListWidget;
	
	@Unique
	private TextFieldWidget serverTextField;
	@Unique
	private ButtonWidget waitToConnectButton;
	
	private MixinMultiplayerScreen(Text title) {
		super(title);
	}
	
	@Inject(method = "init", at = @At("TAIL"))
	public void init(CallbackInfo ci) {
		serverTextField = new TextFieldWidget(textRenderer, width - 104, height - 55, 100, 20, Text.empty());
		addDrawableChild(serverTextField);
		waitToConnectButton = ButtonWidget.builder(Text.literal("Try to Join"), (button) -> client.setScreen(new WaitingToConnectScreen(this, serverTextField.getText()))).dimensions(width - 104, height - 30, 100, 20).build();
		addDrawableChild(waitToConnectButton);
	}
	
	@Override
	public void hamHacks$reconnect() {
		if(selectedEntry != null) {
			this.connect(this.selectedEntry);
		} else {
			MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelectedOrNull();
			if (entry instanceof MultiplayerServerListWidget.ServerEntry) {
				this.connect(((MultiplayerServerListWidget.ServerEntry)entry).getServer());
			} else if (entry instanceof MultiplayerServerListWidget.LanServerEntry) {
				LanServerInfo lanServerInfo = ((MultiplayerServerListWidget.LanServerEntry)entry).getLanServerEntry();
				this.connect(new ServerInfo(lanServerInfo.getMotd(), lanServerInfo.getAddressPort(), ServerInfo.ServerType.LAN));
			}
		}
	}
	
	@Shadow protected abstract void connect(ServerInfo entry);
}
