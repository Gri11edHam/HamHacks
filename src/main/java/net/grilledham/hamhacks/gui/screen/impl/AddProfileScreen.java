package net.grilledham.hamhacks.gui.screen.impl;

import net.grilledham.hamhacks.gui.element.impl.ButtonElement;
import net.grilledham.hamhacks.gui.element.impl.StringSettingElement;
import net.grilledham.hamhacks.gui.screen.GuiScreen;
import net.grilledham.hamhacks.profile.ProfileManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class AddProfileScreen extends GuiScreen {
	
	public AddProfileScreen(Screen last, double scale) {
		super(Text.translatable("hamhacks.menu.addProfile"), last, scale);
	}
	
	@Override
	protected void init() {
		super.init();
		StringSettingElement e;
		ButtonElement a;
		elements.add(e = new StringSettingElement(width / 2F - 53, height / 2F, scale, ""));
		elements.add(a = new ButtonElement("Add", e.getX(), e.getY() + e.getHeight(), e.getWidth(), e.getHeight(), scale, () -> {
			ProfileManager.addProfile(e.getValue());
			close();
		}));
		elements.add(new ButtonElement("Cancel", a.getX(), a.getY() + a.getHeight(), a.getWidth(), a.getHeight(), scale, this::close));
	}
}
