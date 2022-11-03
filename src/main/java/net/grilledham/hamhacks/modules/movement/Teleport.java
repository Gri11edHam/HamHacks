package net.grilledham.hamhacks.modules.movement;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.KeySetting;
import net.grilledham.hamhacks.setting.NumberSetting;
import net.grilledham.hamhacks.util.math.Vec3;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class Teleport extends Module {
	
	@NumberSetting(
			name = "hamhacks.module.teleport.distance",
			defaultValue = 3,
			min = 0,
			max = 20,
			step = 1,
			forceStep = false
	)
	public float distance = 3;
	
	@KeySetting(name = "hamhacks.module.teleport.increaseDistance")
	public Keybind increaseDistance = new Keybind(GLFW.GLFW_KEY_RIGHT_BRACKET);
	
	@KeySetting(name = "hamhacks.module.teleport.decreaseDistance")
	public Keybind decreaseDistance = new Keybind(GLFW.GLFW_KEY_LEFT_BRACKET);
	
	@KeySetting(name = "hamhacks.module.teleport.activate")
	public Keybind activate = new Keybind(GLFW.GLFW_KEY_Z);
	
	public Teleport() {
		super(Text.translatable("hamhacks.module.teleport"), Category.MOVEMENT, new Keybind(0));
	}
	
	@Override
	public String getHUDText() {
		return super.getHUDText() + String.format(" \u00a77%.2f", distance);
	}
	
	@EventListener
	public void onTick(EventTick e) {
		NumberSetting annotation;
		try {
			annotation = getClass().getField("distance").getAnnotation(NumberSetting.class);
		} catch(NoSuchFieldException ex) {
			throw new RuntimeException(ex);
		}
		while(increaseDistance.wasPressed()) {
			if(distance >= annotation.max()) {
				distance = annotation.max();
				break;
			}
			distance++;
		}
		while(decreaseDistance.wasPressed()) {
			if(distance <= annotation.min()) {
				distance = annotation.min();
				break;
			}
			distance--;
		}
		float yaw = mc.player.getYaw();
		float pitch = mc.player.getPitch();
		float f = MathHelper.cos(-yaw * 0.017453292F - 3.1415927F);
		float g = MathHelper.sin(-yaw * 0.017453292F - 3.1415927F);
		float h = -MathHelper.cos(-pitch * 0.017453292F);
		float i = MathHelper.sin(-pitch * 0.017453292F);
		Vec3 facing = new Vec3(g * h, i, f * h);
		facing.mul(distance);
		Vec3 pos = new Vec3(mc.player.getPos());
		while(activate.wasPressed()) {
			pos.add(facing);
			mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.getX(), pos.getY(), pos.getZ(), mc.player.isOnGround()));
			mc.player.setPosition(pos.get());
			pos.set(mc.player.getPos());
		}
	}
}
