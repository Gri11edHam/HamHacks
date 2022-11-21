package net.grilledham.hamhacks.modules.misc;

import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.BoolSetting;
import net.minecraft.text.Text;

public class AntiBan extends Module {
	
	public final BoolSetting joinEnforcedServers = new BoolSetting("hamhacks.module.antiBan.joinEnforcedServers", true, () -> true);
	
	public boolean hasConnected = false;
	
	private static final AntiBan INSTANCE = new AntiBan();
	
	public AntiBan() {
		super(Text.translatable("hamhacks.module.antiBan"), Category.MISC, new Keybind(0));
		setEnabled(true);
		GENERAL_CATEGORY.add(joinEnforcedServers);
	}
	
	public static AntiBan getInstance() {
		return INSTANCE;
	}
}
