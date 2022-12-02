package net.grilledham.hamhacks.page.pages;

import baritone.api.BaritoneAPI;
import baritone.api.Settings.Setting;
import net.grilledham.hamhacks.gui.element.GuiElement;
import net.grilledham.hamhacks.gui.element.impl.BoolSettingElement;
import net.grilledham.hamhacks.gui.element.impl.NumberSettingElement;
import net.grilledham.hamhacks.gui.element.impl.StringSettingElement;
import net.grilledham.hamhacks.page.Page;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class Baritone extends Page {
	
	public Baritone() {
		super(Text.translatable("hamhacks.page.baritone"));
	}
	
	@Override
	public List<GuiElement> getGuiElements(double scale) {
		List<GuiElement> settingElements = new ArrayList<>();
		GuiElement element;
		float maxWidth = 0;
		for(Setting<?> setting : BaritoneAPI.getSettings().allSettings) {
			if(setting.getValueClass().equals(Boolean.class)) {
				Setting<Boolean> bool = (Setting<Boolean>)setting;
				element = new BoolSettingElement(0, 0, scale, setting::getName, () -> "", () -> true, () -> bool.value, (v) -> bool.value = v, setting::reset);
			} else if(setting.getValueClass().equals(List.class)) {
				continue; // TODO: setting container
			} else if(setting.getValueClass().equals(Double.class)) {
				Setting<Double> d = (Setting<Double>)setting;
				element = new NumberSettingElement(0, 0, scale, setting::getName, () -> "", () -> true, () -> d.value, (v) -> d.value = v, setting::reset, () -> 0D, () -> 0D, () -> -1D, () -> false, false);
			} else if(setting.getValueClass().equals(Integer.class)) {
				Setting<Integer> i = (Setting<Integer>)setting;
				element = new NumberSettingElement(0, 0, scale, setting::getName, () -> "", () -> true, () -> (double)i.value, (v) -> i.value = v.intValue(), setting::reset, () -> 0D, () -> 0D, () -> 1D, () -> true, false);
			} else if(setting.getValueClass().equals(Float.class)) {
				Setting<Float> f = (Setting<Float>)setting;
				element = new NumberSettingElement(0, 0, scale, setting::getName, () -> "", () -> true, () -> (double)f.value, (v) -> f.value = v.floatValue(), setting::reset, () -> 0D, () -> 0D, () -> -1D, () -> false, false);
			} else if(setting.getValueClass().equals(Long.class)) {
				Setting<Long> l = (Setting<Long>)setting;
				element = new NumberSettingElement(0, 0, scale, setting::getName, () -> "", () -> true, () -> (double)l.value, (v) -> l.value = v.longValue(), setting::reset, () -> 0D, () -> 0D, () -> 1D, () -> true, false);
			} else if(setting.getValueClass().equals(String.class)) {
				Setting<String> s = (Setting<String>)setting;
				element = new StringSettingElement(0, 0, scale, setting::getName, () -> "", () -> true, () -> s.value, (v) -> s.value = v, setting::reset, () -> "Click to enter text");
			} else {
				continue;
			}
			settingElements.add(element);
			if(maxWidth < element.getWidth()) {
				maxWidth = element.getWidth();
			}
		}
		return settingElements;
	}
}
