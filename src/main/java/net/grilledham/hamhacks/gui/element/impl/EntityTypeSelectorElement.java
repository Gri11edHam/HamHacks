package net.grilledham.hamhacks.gui.element.impl;

import net.grilledham.hamhacks.gui.screen.impl.EntityTypeSelectorScreen;
import net.grilledham.hamhacks.setting.EntityTypeSelector;
import org.lwjgl.glfw.GLFW;

public class EntityTypeSelectorElement extends SettingContainerElement {
	
	public EntityTypeSelectorElement(float x, float y, double scale, EntityTypeSelector setting) {
		super(x, y, scale, setting);
	}
	
	@Override
	public boolean release(double mx, double my, float scrollX, float scrollY, int button) {
		float x = this.x + scrollX;
		float y = this.y + scrollY;
		if(mx >= x + width - 50 && mx < x + width && my >= y && my < y + height) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				mc.setScreen(new EntityTypeSelectorScreen(mc.currentScreen, scale, (EntityTypeSelector)setting));
			} else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				setting.reset();
			}
		}
		return false;
	}
}
