package net.grilledham.hamhacks.modules.misc;

import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.setting.settings.BoolSetting;
import net.minecraft.text.Text;

public class AntiBanModule extends Module {
	
	private static AntiBanModule INSTANCE;
	
	public BoolSetting joinEnforcedServers;
	
	public boolean hasConnected = false;
	
	public AntiBanModule() {
		super(Text.translatable("hamhacks.module.antiBan"), Category.MISC, new Keybind(0));
		INSTANCE = this;
	}
	
	@Override
	public void addSettings() {
		super.addSettings();
		joinEnforcedServers = new BoolSetting(Text.translatable("hamhacks.module.antiBan.joinEnforcedServers"), true);
	}
	
	public static AntiBanModule getInstance() {
		return INSTANCE;
	}
}
