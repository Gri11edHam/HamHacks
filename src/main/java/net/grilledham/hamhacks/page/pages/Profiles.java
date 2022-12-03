package net.grilledham.hamhacks.page.pages;

import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.gui.element.impl.ButtonElement;
import net.grilledham.hamhacks.gui.element.impl.SelectionSettingElement;
import net.grilledham.hamhacks.gui.screen.GuiScreen;
import net.grilledham.hamhacks.gui.screen.impl.AddProfileScreen;
import net.grilledham.hamhacks.page.Page;
import net.grilledham.hamhacks.profile.ProfileManager;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class Profiles extends Page {
	
	public Profiles() {
		super(Text.translatable("hamhacks.page.profiles"));
	}
	
	@Override
	public List<GuiElement> getGuiElements(double scale) {
		List<GuiElement> settingElements = new ArrayList<>();
		settingElements.add(new ButtonElement("Add Profile", 0, 0, 150, 19, scale, () -> mc.setScreen(new AddProfileScreen(mc.currentScreen, scale))));
		settingElements.add(new ButtonElement("Remove Selected Profile", 0, 0, 150, 19, scale, () -> {
			ProfileManager.removeProfile(ProfileManager.getSelectedProfile());
			((GuiScreen)mc.currentScreen).markDirty();
		}));
		settingElements.add(new SelectionSettingElement(0, 0, scale, () -> "Profile", () -> "", () -> true,
				ProfileManager::indexOfSelected,
				ProfileManager::selectProfile,
				() -> {},
				() -> {
					String[] options = new String[ProfileManager.getProfiles().size()];
					for(int i = 0; i < ProfileManager.getProfiles().size(); i++) {
						options[i] = ProfileManager.getProfiles().get(i).name();
					}
					return options;
				}));
		return settingElements;
	}
}
