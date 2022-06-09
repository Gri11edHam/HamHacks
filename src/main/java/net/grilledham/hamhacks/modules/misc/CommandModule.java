package net.grilledham.hamhacks.modules.misc;

import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class CommandModule extends Module {
	
	private static CommandModule INSTANCE;
	
	public CommandModule() {
		super(Text.translatable("module.hamhacks.command"), Category.MISC, new Keybind(GLFW.GLFW_KEY_PERIOD));
		INSTANCE = this;
	}
	
	public static CommandModule getInstance() {
		return INSTANCE;
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		toggle();
		mc.setScreen(new ChatScreen(key.getKeybind().getName()));
	}
}
