package net.grilledham.hamhacks.gui.screen.impl;

import net.grilledham.hamhacks.gui.element.impl.ButtonElement;
import net.grilledham.hamhacks.gui.screen.GuiScreen;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.net.UnknownHostException;

public class WaitingToConnectScreen extends GuiScreen {
	
	private long lastPingTime = 0;
	
	private ServerInfo serverInfo;
	
	private String lastText = "...";
	
	private MultiplayerServerListPinger pinger = new MultiplayerServerListPinger();
	
	public WaitingToConnectScreen(Screen last, String serverIP) {
		super(Text.translatable("hamhacks.menu.waitingToConnect"), last, PageManager.getPage(ClickGUI.class).scale.get());
		serverInfo = new ServerInfo("", serverIP, false);
	}
	
	@Override
	protected void init() {
		elements.add(new ButtonElement("Cancel", width / 2f - 50, height - 40, 100, 20, scale, this::close));
	}
	
	@Override
	public void render(MatrixStack stack, int mx, int my, float tickDelta) {
		renderBackground(stack);
		
		super.render(stack, mx, my, tickDelta);
		
		if(System.currentTimeMillis() - lastPingTime > 1000) {
			pingServer();
		}
		
		stack.push();
		float scaleFactor = (float)(scale / client.getWindow().getScaleFactor());
		stack.scale(scaleFactor, scaleFactor, scaleFactor);
		if(serverInfo.label != null) {
			textRenderer.drawWithShadow(stack, serverInfo.label, width / 2f - textRenderer.getWidth(serverInfo.label) / 2f, height / 2f - 30, -1);
		} else {
			textRenderer.drawWithShadow(stack, "...", width / 2f - textRenderer.getWidth("...") / 2f, height / 2f - 30, -1);
		}
		if(serverInfo.playerCountLabel != null) {
			textRenderer.drawWithShadow(stack, serverInfo.playerCountLabel, width / 2f - textRenderer.getWidth(serverInfo.playerCountLabel) / 2f, height / 2f - 20, -1);
		} else {
			textRenderer.drawWithShadow(stack, ".../...", width / 2f - textRenderer.getWidth(".../...") / 2f, height / 2f - 20, -1);
		}
		if(serverInfo.version != null) {
			textRenderer.drawWithShadow(stack, serverInfo.version, width / 2f - textRenderer.getWidth(serverInfo.version) / 2f, height / 2f - 10, -1);
		} else {
			textRenderer.drawWithShadow(stack, "x.x.x", width / 2f - textRenderer.getWidth("x.x.x") / 2f, height / 2f - 10, -1);
		}
		stack.pop();
	}
	
	@Override
	public void tick() {
		super.tick();
		pinger.tick();
		
		try {
			String playerCount = serverInfo.playerCountLabel.getString();
			String[] splitCount = playerCount.split("/");
			int onlinePlayers = Integer.parseInt(splitCount[0]);
			int maxPlayers = Integer.parseInt(splitCount[1]);
			if(onlinePlayers < maxPlayers) {
				serverInfo = new ServerInfo(serverInfo.name, serverInfo.address, serverInfo.isLocal());
				ConnectScreen.connect(this, client, ServerAddress.parse(serverInfo.address), serverInfo);
			}
		} catch(Exception ignored) {}
	}
	
	private void pingServer() {
		lastPingTime = System.currentTimeMillis();
		try {
			pinger.add(serverInfo, () -> {});
		} catch(UnknownHostException e) {
			serverInfo.label = Text.literal("Unknown Host");
		}
	}
	
	@Override
	public void removed() {
		super.removed();
		pinger.cancel();
	}
}
