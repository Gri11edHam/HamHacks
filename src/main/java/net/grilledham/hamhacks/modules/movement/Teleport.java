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
	
	private final NumberSetting distance = new NumberSetting("hamhacks.module.teleport.distance", 3, () -> true, 0, 20, 1, false);
	
	private final KeySetting increaseDistance = new KeySetting("hamhacks.module.teleport.increaseDistance", new Keybind(GLFW.GLFW_KEY_RIGHT_BRACKET), () -> true);
	
	private final KeySetting decreaseDistance = new KeySetting("hamhacks.module.teleport.decreaseDistance", new Keybind(GLFW.GLFW_KEY_LEFT_BRACKET), () -> true);
	
	private final KeySetting activate = new KeySetting("hamhacks.module.teleport.activate", new Keybind(GLFW.GLFW_KEY_Z), () -> true);
	
	public Teleport() {
		super(Text.translatable("hamhacks.module.teleport"), Category.MOVEMENT, new Keybind(0));
		GENERAL_CATEGORY.add(distance);
		GENERAL_CATEGORY.add(increaseDistance);
		GENERAL_CATEGORY.add(decreaseDistance);
		GENERAL_CATEGORY.add(activate);
	}
	
	@Override
	public String getHUDText() {
		return super.getHUDText() + String.format(" \u00a77%.2f", distance.get());
	}
	
	@EventListener
	public void onTick(EventTick e) {
		if(mc.player == null) return;
		while(increaseDistance.get().wasPressed()) {
			distance.increment();
		}
		while(decreaseDistance.get().wasPressed()) {
			distance.decrement();
		}
		float yaw = mc.player.getYaw();
		float pitch = mc.player.getPitch();
		float f = MathHelper.cos(-yaw * 0.017453292F - 3.1415927F);
		float g = MathHelper.sin(-yaw * 0.017453292F - 3.1415927F);
		float h = -MathHelper.cos(-pitch * 0.017453292F);
		float i = MathHelper.sin(-pitch * 0.017453292F);
		Vec3 facing = new Vec3(g * h, i, f * h);
		facing.mul(distance.get());
		Vec3 pos = new Vec3(mc.player.getPos());
		while(activate.get().wasPressed()) {
			pos.add(facing);
			mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.getX(), pos.getY(), pos.getZ(), mc.player.isOnGround()));
			mc.player.setPosition(pos.get());
			pos.set(mc.player.getPos());
		}
	}
}
