package net.grilledham.hamhacks.modules.misc;

import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class Commands extends Module {
	
	public Commands() {
		super(Text.translatable("hamhacks.module.command"), Category.MISC, new Keybind(GLFW.GLFW_KEY_PERIOD));
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		toggle();
		mc.setScreen(new ChatScreen(key.getName()));
	}
}
