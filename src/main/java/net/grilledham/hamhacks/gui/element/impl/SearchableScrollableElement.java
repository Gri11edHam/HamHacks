package net.grilledham.hamhacks.gui.element.impl;

import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.setting.Setting;
import net.grilledham.hamhacks.setting.StringSetting;
import net.minecraft.client.util.math.MatrixStack;

public class SearchableScrollableElement extends ScrollableElement {
	
	private boolean dirty = true;
	
	private final StringSetting searchArea = new StringSetting("hamhacks.element.searchableScrollableElement.search", "", () -> true) {
		@Override
		public void onChange() {
			dirty = true;
		}
	};
	
	private StringSettingElement searchAreaElement;
	
	public SearchableScrollableElement(float x, float y, float width, float maxHeight, double scale) {
		super(x, y, width, maxHeight, scale);
		addElement(searchAreaElement = (StringSettingElement)searchArea.getElement(x, y, scale));
	}
	
	@Override
	public void clearElements() {
		super.clearElements();
		addElement(searchAreaElement = (StringSettingElement)searchArea.getElement(x, y, scale));
	}
	
	@Override
	public void render(MatrixStack stack, int mx, int my, float scrollX, float scrollY, float partialTicks) {
		if(!searchArea.get().equals("") && dirty) {
			dirty = false;
			for(GuiElement element : getElements()) {
				if(element != searchAreaElement && element instanceof SettingElement<?>) {
					Setting<?> setting = ((SettingElement<?>)element).getSetting();
					boolean enabled = setting.getName().toLowerCase().contains(searchArea.get().toLowerCase())
							|| setting.getConfigName().toLowerCase().contains(searchArea.get().toLowerCase());
					setEnabled(element, enabled);
				}
			}
		}
		super.render(stack, mx, my, scrollX, scrollY, partialTicks);
	}
	
	public boolean shouldUpdateVisibility() {
		return searchArea.get().equals("");
	}
}
