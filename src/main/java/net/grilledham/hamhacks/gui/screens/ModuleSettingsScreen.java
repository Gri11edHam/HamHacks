package net.grilledham.hamhacks.gui.screens;

import net.grilledham.hamhacks.gui.parts.GuiPart;
import net.grilledham.hamhacks.gui.parts.impl.*;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.render.ClickGUI;
import net.grilledham.hamhacks.util.RenderUtil;
import net.grilledham.hamhacks.util.setting.Setting;
import net.grilledham.hamhacks.util.setting.settings.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ModuleSettingsScreen extends Screen {
	
	private final Screen last;
	private final Module module;
	
	private GuiPart topPart;
	private ScrollablePart scrollArea;
	
	public ModuleSettingsScreen(Screen last, Module module) {
		super(Text.translatable("menu.hamhacks.clickgui.module"));
		this.last = last;
		this.module = module;
	}
	
	@Override
	protected void init() {
		super.init();
		width = (int)((width * client.getWindow().getScaleFactor()) / ClickGUI.getInstance().scale.getValue());
		height = (int)((height * client.getWindow().getScaleFactor()) / ClickGUI.getInstance().scale.getValue());
		int maxWidth = 0;
		topPart = new GuiPart(0, 0, client.textRenderer.getWidth(module.getName()) + 2, 16) {
			@Override
			public void render(MatrixStack stack, int mx, int my, float partialTicks) {
				stack.push();
				RenderUtil.preRender();
				
				int bgC = ClickGUI.getInstance().accentColor.getRGB();
				RenderUtil.drawRect(stack, x, y, width, height, bgC);
				
				mc.textRenderer.drawWithShadow(stack, module.getName(), x + width / 2f - mc.textRenderer.getWidth(module.getName()) / 2f, y + 4, ClickGUI.getInstance().textColor.getRGB());
				
				RenderUtil.postRender();
				stack.pop();
			}
		};
		List<GuiPart> settingParts = new ArrayList<>();
		GuiPart part;
		int totalHeight = 0;
		for(Setting<?> s : module.getShownSettings()) {
			if(s instanceof BoolSetting) {
				settingParts.add(part = new BoolSettingPart(0, 0, (BoolSetting)s));
			} else if(s instanceof ColorSetting) {
				settingParts.add(part = new ColorSettingPart(0, 0, (ColorSetting)s));
			} else if(s instanceof FloatSetting) {
				settingParts.add(part = new FloatSettingPart(0, 0, (FloatSetting)s));
			} else if(s instanceof IntSetting) {
				settingParts.add(part = new IntSettingPart(0, 0, (IntSetting)s));
			} else if(s instanceof KeySetting) {
				settingParts.add(part = new KeySettingPart(0, 0, (KeySetting)s));
			} else if(s instanceof ListSetting) {
				settingParts.add(part = new ListSettingPart(0, 0, (ListSetting)s));
			} else if(s instanceof SelectionSetting) {
				settingParts.add(part = new SelectionSettingPart(0, 0, (SelectionSetting)s));
			} else if(s instanceof StringSetting) {
				settingParts.add(part = new StringSettingPart(0, 0, (StringSetting)s));
			} else {
				settingParts.add(part = new GuiPart(0, 0, 0, 16) {
					@Override
					public void render(MatrixStack stack, int mx, int my, float partialTicks) {
						stack.push();
						RenderUtil.preRender();
						
						int bgC = ClickGUI.getInstance().bgColor.getRGB();
						boolean hovered = mx >= x && mx < x + width && my >= y && my < y + height;
						bgC = RenderUtil.mix(ClickGUI.getInstance().bgColorHovered.getRGB(), bgC, hovered ? 1 : 0);
						RenderUtil.drawRect(stack, x, y, width, height, bgC);
						
						mc.textRenderer.drawWithShadow(stack, "uhhhh", x + 2, y + 4, ClickGUI.getInstance().textColor.getRGB());
						
						RenderUtil.postRender();
						stack.pop();
					}
				});
			}
			if(maxWidth < part.getWidth()) {
				maxWidth = part.getWidth();
			}
			totalHeight += part.getHeight();
		}
		int yAdd = 0;
		scrollArea = new ScrollablePart(0, 0, 0, (int)(Math.min(height * (2 / 3f), totalHeight)));
		scrollArea.clearParts();
		scrollArea.moveTo(width / 2 - maxWidth / 2, (int)(height - Math.min(height * (5 / 6f), totalHeight + height * (5 / 6f))));
		scrollArea.resize(maxWidth, 0);
		topPart.moveTo(width / 2 - maxWidth / 2, (int)(height - Math.min(height * (5 / 6f), totalHeight + height * (5 / 6f))) - topPart.getHeight());
		topPart.resize(maxWidth, topPart.getHeight());
		for(GuiPart guiPart : settingParts) {
			guiPart.moveTo(width / 2 - maxWidth / 2, (int)(height - Math.min(height * (5 / 6f), totalHeight + height * (5 / 6f)) + yAdd));
			guiPart.resize(maxWidth, guiPart.getHeight());
			yAdd += guiPart.getHeight();
			scrollArea.addPart(guiPart);
		}
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		mouseX = (int)((mouseX * client.getWindow().getScaleFactor()) / ClickGUI.getInstance().scale.getValue());
		mouseY = (int)((mouseY * client.getWindow().getScaleFactor()) / ClickGUI.getInstance().scale.getValue());
		matrices.push();
		float scaleFactor = (float)(ClickGUI.getInstance().scale.getValue() / client.getWindow().getScaleFactor());
		matrices.scale(scaleFactor, scaleFactor, scaleFactor);
		
		super.render(matrices, mouseX, mouseY, delta);
		
		topPart.draw(matrices, mouseX, mouseY, delta);
		scrollArea.draw(matrices, mouseX, mouseY, delta);
		topPart.drawTop(matrices, mouseX, mouseY, delta);
		scrollArea.drawTop(matrices, mouseX, mouseY, delta);
		
		matrices.pop();
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		mouseX = (mouseX * client.getWindow().getScaleFactor()) / ClickGUI.getInstance().scale.getValue();
		mouseY = (mouseY * client.getWindow().getScaleFactor()) / ClickGUI.getInstance().scale.getValue();
		if(scrollArea.click(mouseX, mouseY, button)) {
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		mouseX = (mouseX * client.getWindow().getScaleFactor()) / ClickGUI.getInstance().scale.getValue();
		mouseY = (mouseY * client.getWindow().getScaleFactor()) / ClickGUI.getInstance().scale.getValue();
		if(scrollArea.release(mouseX, mouseY, button)) {
			return true;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		mouseX = (mouseX * client.getWindow().getScaleFactor()) / ClickGUI.getInstance().scale.getValue();
		mouseY = (mouseY * client.getWindow().getScaleFactor()) / ClickGUI.getInstance().scale.getValue();
		if(scrollArea.drag(mouseX, mouseY, button, deltaX, deltaY)) {
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if(scrollArea.type(keyCode, scanCode, modifiers)) {
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	
	@Override
	public boolean charTyped(char chr, int modifiers) {
		if(scrollArea.typeChar(chr, modifiers)) {
			return true;
		}
		return super.charTyped(chr, modifiers);
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if(scrollArea.scroll(mouseX, mouseY, amount)) {
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, amount);
	}
	
	@Override
	public void close() {
		client.setScreen(last);
	}
	
	@Override
	public boolean shouldPause() {
		return false;
	}
}
