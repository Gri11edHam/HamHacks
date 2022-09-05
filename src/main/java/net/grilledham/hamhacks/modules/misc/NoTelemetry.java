package net.grilledham.hamhacks.modules.misc;

import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.minecraft.text.Text;

public class NoTelemetry extends Module {
	
	private static NoTelemetry INSTANCE;
	
	public NoTelemetry() {
		super(Text.translatable("hamhacks.module.noTelemetry"), Category.MISC, new Keybind(0));
		setEnabled(true);
		INSTANCE = this;
	}
	
	public static NoTelemetry getInstance() {
		return INSTANCE;
	}
}
