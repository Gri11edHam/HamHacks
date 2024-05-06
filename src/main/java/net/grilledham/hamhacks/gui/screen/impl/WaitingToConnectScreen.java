package net.grilledham.hamhacks.gui.screen.impl;

import net.grilledham.hamhacks.gui.element.impl.ButtonElement;
import net.grilledham.hamhacks.gui.screen.GuiScreen;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.CookieStorage;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.HashMap;

public class WaitingToConnectScreen extends GuiScreen {
	
	private long lastPingTime = 0;
	
	private ServerInfo serverInfo;
	
	private String lastText = "...";
	
	private MultiplayerServerListPinger pinger = new MultiplayerServerListPinger();
	
	public WaitingToConnectScreen(Screen last, String serverIP) {
		super(Text.translatable("hamhacks.menu.waitingToConnect"), last, PageManager.getPage(ClickGUI.class).scale.get());
		serverInfo = new ServerInfo("", serverIP, ServerInfo.ServerType.OTHER);
	}
	
	@Override
	protected void init() {
		elements.add(new ButtonElement("Cancel", width / 2f - 50, height - 40, 100, 20, scale, this::close));
	}
	
	@Override
	public void render(DrawContext ctx, int mx, int my, float tickDelta) {
		MatrixStack stack = ctx.getMatrices();
		
		super.render(ctx, mx, my, tickDelta);
		
		if(System.currentTimeMillis() - lastPingTime > 1000) {
			pingServer();
		}
		
		stack.push();
		float scaleFactor = (float)(scale / client.getWindow().getScaleFactor());
		stack.scale(scaleFactor, scaleFactor, scaleFactor);
		if(serverInfo.label != null) {
			RenderUtil.drawString(ctx, serverInfo.label, width / 2f - textRenderer.getWidth(serverInfo.label) / 2f, height / 2f - 30, -1, true);
		} else {
			RenderUtil.drawString(ctx, "...", width / 2f - RenderUtil.getStringWidth("...") / 2f, height / 2f - 30, -1, true);
		}
		if(serverInfo.playerCountLabel != null) {
			RenderUtil.drawString(ctx, serverInfo.playerCountLabel, width / 2f - textRenderer.getWidth(serverInfo.playerCountLabel) / 2f, height / 2f - 20, -1, true);
		} else {
			RenderUtil.drawString(ctx, ".../...", width / 2f - RenderUtil.getStringWidth(".../...") / 2f, height / 2f - 20, -1, true);
		}
		if(serverInfo.version != null) {
			RenderUtil.drawString(ctx, serverInfo.version, width / 2f - textRenderer.getWidth(serverInfo.version) / 2f, height / 2f - 10, -1, true);
		} else {
			RenderUtil.drawString(ctx, "x.x.x", width / 2f - RenderUtil.getStringWidth("x.x.x") / 2f, height / 2f - 10, -1, true);
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
				serverInfo = new ServerInfo(serverInfo.name, serverInfo.address, serverInfo.isLocal() ? ServerInfo.ServerType.LAN : serverInfo.isRealm() ? ServerInfo.ServerType.REALM : ServerInfo.ServerType.OTHER);
				ConnectScreen.connect(this, client, ServerAddress.parse(serverInfo.address), serverInfo, false, new CookieStorage(new HashMap<>()));
			}
		} catch(Exception ignored) {}
	}
	
	private void pingServer() {
		lastPingTime = System.currentTimeMillis();
		try {
			pinger.add(serverInfo, () -> {}, () -> {});
		} catch(Exception e) {
			serverInfo.label = Text.literal(e.toString());
		}
	}
	
	@Override
	public void removed() {
		super.removed();
		pinger.cancel();
	}
}
