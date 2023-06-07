package net.grilledham.hamhacks;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.grilledham.hamhacks.gui.screen.impl.ClickGUIScreen;
import net.minecraft.client.gui.screen.Screen;

public class ModMenuApiImpl implements ModMenuApi {
	
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return (ConfigScreenFactory<Screen>)ClickGUIScreen::new;
	}
}
