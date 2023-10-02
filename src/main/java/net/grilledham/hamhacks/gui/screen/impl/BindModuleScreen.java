package net.grilledham.hamhacks.gui.screen.impl;

import net.grilledham.hamhacks.command.CommandManager;
import net.grilledham.hamhacks.gui.screen.GuiScreen;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.grilledham.hamhacks.util.ChatUtil;
import net.grilledham.hamhacks.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;

public class BindModuleScreen extends GuiScreen {
	
	private final Module module;
	
	private boolean isFirstClick = true;
	
	public BindModuleScreen(Module module) {
		super(Text.translatable("hamhacks.menu.bindModule"), null, PageManager.getPage(ClickGUI.class).scale.get());
		this.module = module;
		module.getKey().setKey(0);
		alwaysDrawBackground = true;
	}
	
	@Override
	public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
		super.render(ctx, mouseX, mouseY, delta);
		String text = module.getKey().getName().equals("None") ? "Listening..." : module.getKey().getName() + "...";
		RenderUtil.drawString(ctx, text, width / 2f - RenderUtil.getStringWidth(text) / 2f, height / 2f - RenderUtil.getFontHeight() / 2f, -1, true);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		int code = button - Keybind.MOUSE_SHIFT;
		int[] codes = module.getKey().getKeyCombo();
		if(codes.length == 1 && codes[0] == 0) {
			module.getKey().setKey(code);
		} else {
			boolean containsKey = false;
			for(int i : codes) {
				if(i == code) {
					containsKey = true;
					break;
				}
			}
			if(!containsKey) {
				codes = Arrays.copyOf(codes, codes.length + 1);
				codes[codes.length - 1] = code;
			}
			module.getKey().setKey(codes);
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return super.mouseReleased(mouseX, mouseY, button);
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if(keyCode == GLFW.GLFW_KEY_ESCAPE) {
			close();
			info(Text.of("Bound " + module.getName() + " to " + module.getKey().getName()));
			return true;
		} else {
			int[] codes = module.getKey().getKeyCombo();
			if(codes.length == 1 && codes[0] == 0) {
				module.getKey().setKey(keyCode);
			} else {
				boolean containsKey = false;
				for(int i : codes) {
					if(i == keyCode) {
						containsKey = true;
						break;
					}
				}
				if(!containsKey) {
					codes = Arrays.copyOf(codes, codes.length + 1);
					codes[codes.length - 1] = keyCode;
				}
				module.getKey().setKey(codes);
			}
		}
		return super.keyReleased(keyCode, scanCode, modifiers);
	}
	
	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		if(isFirstClick) {
			isFirstClick = false;
			return super.keyReleased(keyCode, scanCode, modifiers);
		}
		return super.keyReleased(keyCode, scanCode, modifiers);
	}
	
	public void info(Text message, Object... args) {
		MutableText prefix = (MutableText)Text.of("[" + CommandManager.getCommand("bind").getTitle() + "]");
		prefix.setStyle(Style.EMPTY.withFormatting(Formatting.DARK_RED));
		MutableText separator = (MutableText)Text.of(" > ");
		separator.setStyle(Style.EMPTY.withFormatting(Formatting.DARK_GRAY));
		ChatUtil.info(prefix.append(separator), message, args);
	}
}
