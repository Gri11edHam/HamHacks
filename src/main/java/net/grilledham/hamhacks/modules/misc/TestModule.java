package net.grilledham.hamhacks.modules.misc;

import com.google.common.collect.Lists;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.*;
import net.grilledham.hamhacks.util.Color;
import net.minecraft.text.Text;

public class TestModule extends Module {
	
	public final BoolSetting testBool = new BoolSetting("test bool", false, () -> true);
	
	public final ColorSetting testColor = new ColorSetting("test color", Color.getWhite(), testBool::get);
	
	public final NumberSetting testDouble = new NumberSetting("test double", 0.5, testBool::get, 0, 1);
	
	public final NumberSetting testInt = new NumberSetting("test int", 100, testBool::get, 0, 200, 1);
	
	public final KeySetting testKey = new KeySetting("test key", new Keybind(0), testBool::get);
	
	public final ListSetting testList = new ListSetting("test list", Lists.newArrayList("test 1", "test 2", "test 3"), testBool::get);
	
	public final SelectionSetting testSelector = new SelectionSetting("test selector", 0, testBool::get, "test 1", "test 2", "test 3");
	
	public final StringSetting testString = new StringSetting("test string", "test", testBool::get, "1337");
	
	public TestModule() {
		super(Text.translatable("module.hamhacks.test"), Category.MISC, new Keybind(0));
		GENERAL_CATEGORY.add(testBool);
		GENERAL_CATEGORY.add(testColor);
		GENERAL_CATEGORY.add(testDouble);
		GENERAL_CATEGORY.add(testInt);
		GENERAL_CATEGORY.add(testKey);
		GENERAL_CATEGORY.add(testList);
		GENERAL_CATEGORY.add(testSelector);
		GENERAL_CATEGORY.add(testString);
	}
}
