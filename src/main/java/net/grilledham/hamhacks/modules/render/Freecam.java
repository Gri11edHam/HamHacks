package net.grilledham.hamhacks.modules.render;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventKey;
import net.grilledham.hamhacks.event.events.EventLookDirection;
import net.grilledham.hamhacks.event.events.EventPacket;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnS2CPacket;
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
	
	private float lastDx;
	private float lastDy;
	private float lastDz;
	
	private Perspective perspective;
	
	@NumberSetting(
			name = "hamhacks.module.freecam.speed",
			defaultValue = 1,
			min = 0,
			max = 10
	)
	public float speed = 1;
	
	private static Freecam INSTANCE;
	
	public Freecam() {
		super(Text.translatable("hamhacks.module.freecam"), Category.RENDER, new Keybind(0));
		INSTANCE = this;
	}
	
	public static Freecam getInstance() {
		return INSTANCE;
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		
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
		mc.options.sneakKey.setPressed(false);
		mc.options.setPerspective(perspective);
	}
	
	@EventListener
	public void onJoin(EventPacket.EventPacketReceived e) {
		if(e.packet instanceof PlayerSpawnS2CPacket) {
			if(((PlayerSpawnS2CPacket)e.packet).getPlayerUuid() == mc.cameraEntity.getUuid()) {
				toggle();
			}
		} else if(e.packet instanceof PlayerRespawnS2CPacket) {
			toggle();
		}
	}
	
	@EventListener
	public void onTick(EventTick e) {
		if(!ClickGUI.getInstance().moveInScreen(mc.currentScreen)) {
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
		float dx = (float)(distanceForward * Math.cos(Math.toRadians(yaw + 90)));
		float dy = distanceVertical;
		float dz = (float)(distanceForward * Math.sin(Math.toRadians(yaw + 90)));
		dx += (float)(distanceStrafe * Math.cos(Math.toRadians(yaw)));
		dz += (float)(distanceStrafe * Math.sin(Math.toRadians(yaw)));
		dx = lastDx + (dx / 10f);
		dy = lastDy + (dy / 10f);
		dz = lastDz + (dz / 10f);
		dx *= speed;
		dy *= speed;
		dz *= speed;
		if(dx > speed) {
			dx = speed;
		} else if(dx < -speed) {
			dx = -speed;
		}
		if(dy > speed) {
			dy = speed;
		} else if(dy < -speed) {
			dy = -speed;
		}
		if(dz > speed) {
			dz = speed;
		} else if(dz < -speed) {
			dz = -speed;
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
		if(!ClickGUI.getInstance().moveInScreen(mc.currentScreen) || GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_F3) == GLFW.GLFW_PRESS) {
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
