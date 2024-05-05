package net.grilledham.hamhacks.page.pages;

import net.grilledham.hamhacks.page.Page;
import net.grilledham.hamhacks.setting.SelectionSetting;
import net.grilledham.hamhacks.setting.SettingCategory;
import net.minecraft.text.Text;

public class Updates extends Page {
	
	private final SettingCategory GENERAL_CATEGORY = new SettingCategory("hamhacks.page.updates.category.general");
	
	public final SelectionSetting branch = new SelectionSetting("hamhacks.page.updates.branch", 0, () -> true, "hamhacks.page.updates.branch.release", "hamhacks.page.updates.branch.dev");
	public final SelectionSetting showChangelogButton = new SelectionSetting("hamhacks.page.updates.showChangelogButton", 1, () -> true, "hamhacks.page.updates.showChangelogButton.always", "hamhacks.page.updates.showChangelogButton.afterUpdate", "hamhacks.page.updates.showChangelogButton.never");
	
	public Updates() {
		super(Text.translatable("hamhacks.page.updates"));
		settingCategories.add(0, GENERAL_CATEGORY);
		GENERAL_CATEGORY.add(branch);
		GENERAL_CATEGORY.add(showChangelogButton);
	}
}
