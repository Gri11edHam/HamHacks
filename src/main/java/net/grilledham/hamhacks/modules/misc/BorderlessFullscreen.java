package net.grilledham.hamhacks.modules.misc;

import net.grilledham.hamhacks.mixininterface.IWindow;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.minecraft.text.Text;

public class BorderlessFullscreen extends Module {
	
	public BorderlessFullscreen() {
		super(Text.translatable("hamhacks.module.borderlessFullscreen"), Category.MISC, new Keybind());
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		((IWindow)(Object)mc.getWindow()).hamhacks$updateVideoMode();
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		((IWindow)(Object)mc.getWindow()).hamhacks$updateVideoMode();
	}
}
