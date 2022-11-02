package net.grilledham.hamhacks.modules.misc;

import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.setting.*;
import net.grilledham.hamhacks.util.Color;
import net.minecraft.text.Text;

public class TestModule extends Module {
	
	@BoolSetting(name = "setting.test.testbool")
	public boolean testBool = false;
	
	@ColorSetting(name = "setting.test.testcolor", dependsOn = "testBool")
	public Color testColor = Color.getWhite();
	
	@NumberSetting(
			name = "setting.test.testfloat",
			defaultValue = 0.5f,
			min = 0,
			max = 1,
			dependsOn = "testBool"
	)
	public float testFloat = 0.5f;
	
	@NumberSetting(
			name = "setting.test.testint",
			defaultValue = 100,
			min = 0,
			max = 200,
			step = 1,
			dependsOn = "testBool"
	)
	public float testInt = 100;
	
	@KeySetting(name = "setting.test.testkey", dependsOn = "testBool")
	public Keybind testKey = new Keybind(0);
	
	@ListSetting(name = "setting.test.testlist", dependsOn = "testBool")
	public StringList testList = new StringList("Test1", "Test2");
	
	@SelectionSetting(name = "setting.test.testselector", dependsOn = "testBool", options = {"setting.test.testselector.test1", "setting.test.testselector.test2", "setting.test.testselector.test3"})
	public int testSelector = 0;
	
	@StringSetting(
			name = "setting.test.teststring",
			defaultValue = "Test",
			dependsOn = "testBool"
	)
	public String testString = "Test";
	
	public TestModule() {
		super(Text.translatable("module.hamhacks.test"), Category.MISC, new Keybind(0));
	}
}
