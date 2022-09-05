package net.grilledham.hamhacks.modules.misc;

import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.setting.BoolSetting;
import net.minecraft.text.Text;

public class AntiBan extends Module {
	
	private static AntiBan INSTANCE;
	
	@BoolSetting(name = "hamhacks.module.antiBan.joinEnforcedServers", defaultValue = true)
	public boolean joinEnforcedServers = true;
	
	public boolean hasConnected = false;
	
	public AntiBan() {
		super(Text.translatable("hamhacks.module.antiBan"), Category.MISC, new Keybind(0));
		setEnabled(true);
		INSTANCE = this;
	}
	
	public static AntiBan getInstance() {
		return INSTANCE;
	}
}
