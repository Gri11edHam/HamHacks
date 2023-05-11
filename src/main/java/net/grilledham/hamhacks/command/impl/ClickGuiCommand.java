package net.grilledham.hamhacks.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.grilledham.hamhacks.command.Command;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class ClickGuiCommand extends Command {
	
	public ClickGuiCommand() {
		super("clickgui", "Opens the Click GUI", "gui", "menu", "clickmenu");
	}
	
	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.executes(ctx -> {
			PageManager.getPage(ClickGUI.class).openMenu();
			return SINGLE_SUCCESS;
		});
	}
}
