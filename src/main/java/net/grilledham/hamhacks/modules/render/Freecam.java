package net.grilledham.hamhacks.modules.render;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventKey;
import net.grilledham.hamhacks.event.events.EventLookDirection;
import net.grilledham.hamhacks.event.events.EventPacket;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.notification.Notifications;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class Freecam extends Module {
	
	public Vec3d pos = new Vec3d(0, 0, 0);
	public Vec3d prevPos = new Vec3d(0, 0, 0);
	
	public float pitch = 0;
	public float yaw = 0;
	
	public float prevPitch = 0;
	public float prevYaw = 0;
	
	private boolean forward;
	private boolean back;
	private boolean right;
	private boolean left;
	private boolean up;
	private boolean down;
	
	private double lastDx;
	private double lastDy;
	private double lastDz;
	
	private Perspective perspective = Perspective.FIRST_PERSON;
	
	private final NumberSetting speed = new NumberSetting("hamhacks.module.freecam.speed", 1, () -> true, 0, 10);
	
	public Freecam() {
		super(Text.translatable("hamhacks.module.freecam"), Category.RENDER, new Keybind(0));
		GENERAL_CATEGORY.add(speed);
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		
		if(mc.world == null || mc.options.getPerspective() == null) {
			return;
		}
		
		setUp();
		
		forward = false;
		back = false;
		left = false;
		right = false;
		up = false;
		down = false;
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		
		if(mc.world == null || mc.options.getPerspective() == null) {
			return;
		}
		
		mc.options.sneakKey.setPressed(false);
		mc.options.setPerspective(perspective);
	}
	
	@EventListener
	public void onJoin(EventPacket.EventPacketReceived e) {
		if(e.packet instanceof GameJoinS2CPacket) {
			setEnabled(false);
			Notifications.notify(getName(), "Disabled due to join");
		} else if(e.packet instanceof PlayerRespawnS2CPacket) {
			setEnabled(false);
			Notifications.notify(getName(), "Disabled due to death");
		}
	}
	
	@EventListener
	public void onTick(EventTick e) {
		if(mc.world == null || mc.options.getPerspective() == null) {
			return;
		}
		
		if(!PageManager.getPage(ClickGUI.class).moveInScreen(mc.currentScreen)) {
			forward = false;
			back = false;
			left = false;
			right = false;
			up = false;
			down = false;
		}
		
		if(!mc.options.getPerspective().isFirstPerson()) {
			mc.options.setPerspective(Perspective.FIRST_PERSON);
		}
		
		float distanceForward = 0;
		float distanceStrafe = 0;
		float distanceVertical = 0;
		if(forward) {
			distanceForward += 1;
		}
		if(back) {
			distanceForward -= 1;
		}
		if(left) {
			distanceStrafe += 1;
		}
		if(right) {
			distanceStrafe -= 1;
		}
		if(up) {
			distanceVertical += 1;
		}
		if(down) {
			distanceVertical -= 1;
		}
		distanceForward *= speed.get();
		distanceStrafe *= speed.get();
		distanceVertical *= speed.get();
		double dx = (float)(distanceForward * Math.cos(Math.toRadians(yaw + 90)));
		double dy = distanceVertical;
		double dz = (float)(distanceForward * Math.sin(Math.toRadians(yaw + 90)));
		dx += (float)(distanceStrafe * Math.cos(Math.toRadians(yaw)));
		dz += (float)(distanceStrafe * Math.sin(Math.toRadians(yaw)));
		dx = lastDx + (dx / 10f);
		dy = lastDy + (dy / 10f);
		dz = lastDz + (dz / 10f);
		if(dx > speed.get()) {
			dx = speed.get();
		} else if(dx < -speed.get()) {
			dx = -speed.get();
		}
		if(dy > speed.get()) {
			dy = speed.get();
		} else if(dy < -speed.get()) {
			dy = -speed.get();
		}
		if(dz > speed.get()) {
			dz = speed.get();
		} else if(dz < -speed.get()) {
			dz = -speed.get();
		}
		if(distanceVertical == 0) {
			dy = 0;
		}
		if(distanceForward == 0 && distanceStrafe == 0) {
			dx = 0;
			dz = 0;
		}
		lastDx = dx * (9 / 10f);
		lastDy = dy * (9 / 10f);
		lastDz = dz * (9 / 10f);
		
		prevPos = pos;
		pos = pos.add(dx, dy, dz);
	}
	
	@EventListener
	public void onKey(EventKey e) {
		if(!PageManager.getPage(ClickGUI.class).moveInScreen(mc.currentScreen) || GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_F3) == GLFW.GLFW_PRESS) {
			return;
		}
		
		if(mc.options.forwardKey.matchesKey(e.key, e.scancode)) {
			forward = isPressed(e.key);
			e.canceled = true;
		}
		if(mc.options.backKey.matchesKey(e.key, e.scancode)) {
			back = isPressed(e.key);
			e.canceled = true;
		}
		if(mc.options.leftKey.matchesKey(e.key, e.scancode)) {
			left = isPressed(e.key);
			e.canceled = true;
		}
		if(mc.options.rightKey.matchesKey(e.key, e.scancode)) {
			right = isPressed(e.key);
			e.canceled = true;
		}
		if(mc.options.jumpKey.matchesKey(e.key, e.scancode)) {
			up = isPressed(e.key);
			e.canceled = true;
		}
		if(mc.options.sneakKey.matchesKey(e.key, e.scancode)) {
			down = isPressed(e.key);
			e.canceled = true;
		}
	}
	
	@EventListener
	public void onChangeLookDirection(EventLookDirection e) {
		prevYaw = yaw;
		prevPitch = pitch;
		
		yaw += e.dx * 0.15;
		pitch += e.dy * 0.15;
		
		pitch = MathHelper.clamp(pitch, -90, 90);
		
		e.canceled = true;
	}
	
	private boolean isPressed(int key) {
		return GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), key) == GLFW.GLFW_PRESS;
	}
	
	public Vec3d getPos(float partialTicks) {
		return prevPos.lerp(pos, partialTicks);
	}
	
	public float getYaw(float partialTicks) {
		return MathHelper.lerp(partialTicks, prevYaw, yaw);
	}
	
	public float getPitch(float partialTicks) {
		return MathHelper.lerp(partialTicks, prevPitch, pitch);
	}
	
	private void setUp() {
		mc.options.forwardKey.setPressed(false);
		mc.options.backKey.setPressed(false);
		mc.options.leftKey.setPressed(false);
		mc.options.rightKey.setPressed(false);
		mc.options.jumpKey.setPressed(false);
		
		pos = mc.cameraEntity.getPos();
		prevPos = mc.cameraEntity.getPos();
		
		pitch = mc.cameraEntity.getPitch();
		prevPitch = pitch;
		yaw = mc.cameraEntity.getYaw();
		prevYaw = yaw;
		
		perspective = mc.options.getPerspective();
	}
}
