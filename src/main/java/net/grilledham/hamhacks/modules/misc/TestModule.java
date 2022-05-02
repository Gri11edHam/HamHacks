package net.grilledham.hamhacks.modules.misc;

import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.setting.settings.*;
import net.minecraft.text.TranslatableText;

public class TestModule extends Module {
	
	private BoolSetting testBool;
	private ColorSetting testColor;
	private FloatSetting testFloat;
	private IntSetting testInt;
	private KeySetting testKey;
	private ListSetting testList;
	private SelectionSetting testSelector;
	private StringSetting testString;
	
	public TestModule() {
		super(new TranslatableText("module.hamhacks.test"), Category.MISC, new Keybind(0));
	}
	
	@Override
	public void addSettings() {
		super.addSettings();
		testBool = new BoolSetting("Test Bool", false) {
			@Override
			protected void valueChanged() {
				super.valueChanged();
				updateSettings();
				updateScreenIfOpen();
			}
		};
		testColor = new ColorSetting("Test Color", 1, 1, 1, 1, false);
		testFloat = new FloatSetting("Test Float", 0.5f, 0f, 1f);
		testInt = new IntSetting("Test Int", 100, 0, 200);
		testKey = new KeySetting("Test Key", new Keybind(0));
		testList = new ListSetting("Test List", "Test1", "Test2");
		testSelector = new SelectionSetting("Test Selector", "Test1", "Test1", "Test2", "Test3");
		testString = new StringSetting("Test String", "Test");
		addSetting(testBool);
		addSetting(testColor);
		addSetting(testFloat);
		addSetting(testInt);
		addSetting(testKey);
		addSetting(testList);
		addSetting(testSelector);
		addSetting(testString);
		updateSettings();
	}
	
	private void updateSettings() {
		hideSetting(testColor);
		hideSetting(testFloat);
		hideSetting(testInt);
		hideSetting(testKey);
		hideSetting(testList);
		hideSetting(testSelector);
		hideSetting(testString);
		if(testBool.getValue()) {
			showSetting(testString, shownSettings.indexOf(testBool) + 1);
			showSetting(testSelector, shownSettings.indexOf(testBool) + 1);
			showSetting(testList, shownSettings.indexOf(testBool) + 1);
			showSetting(testKey, shownSettings.indexOf(testBool) + 1);
			showSetting(testInt, shownSettings.indexOf(testBool) + 1);
			showSetting(testFloat, shownSettings.indexOf(testBool) + 1);
			showSetting(testColor, shownSettings.indexOf(testBool) + 1);
		}
	}
}
