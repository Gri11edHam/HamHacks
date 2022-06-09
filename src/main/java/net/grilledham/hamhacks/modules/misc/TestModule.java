package net.grilledham.hamhacks.modules.misc;

import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.util.setting.settings.*;
import net.minecraft.text.Text;

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
		super(Text.translatable("module.hamhacks.test"), Text.translatable("module.hamhacks.test.tooltip"), Category.MISC, new Keybind(0));
	}
	
	@Override
	public void addSettings() {
		super.addSettings();
		testBool = new BoolSetting(Text.translatable("setting.test.testbool"), false) {
			@Override
			protected void valueChanged() {
				super.valueChanged();
				updateSettings();
				updateScreenIfOpen();
			}
		};
		testColor = new ColorSetting(Text.translatable("setting.test.testcolor"), 1, 1, 1, 1, false);
		testFloat = new FloatSetting(Text.translatable("setting.test.testfloat"), 0.5f, 0f, 1f);
		testInt = new IntSetting(Text.translatable("setting.test.testint"), 100, 0, 200);
		testKey = new KeySetting(Text.translatable("setting.test.testkey"), new Keybind(0));
		testList = new ListSetting(Text.translatable("setting.test.testlist"), "Test1", "Test2");
		testSelector = new SelectionSetting(Text.translatable("setting.test.testselector"), Text.translatable("setting.test.testselector.test1"), Text.translatable("setting.test.testselector.test1"), Text.translatable("setting.test.testselector.test2"), Text.translatable("setting.test.testselector.test3"));
		testString = new StringSetting(Text.translatable("setting.test.teststring"), "Test");
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
