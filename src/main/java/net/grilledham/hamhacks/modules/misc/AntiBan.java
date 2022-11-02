package net.grilledham.hamhacks.modules.misc;

import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.BoolSetting;
import net.minecraft.text.Text;

public class AntiBan extends Module {
	
	@BoolSetting(name = "hamhacks.module.antiBan.joinEnforcedServers", defaultValue = true)
	public boolean joinEnforcedServers = true;
	
	public boolean hasConnected = false;
	
	private static final AntiBan INSTANCE = new AntiBan();
	
	public AntiBan() {
		super(Text.translatable("hamhacks.module.antiBan"), Category.MISC, new Keybind(0));
		setEnabled(true);
	}
	
	public static AntiBan getInstance() {
		return INSTANCE;
	}
}
