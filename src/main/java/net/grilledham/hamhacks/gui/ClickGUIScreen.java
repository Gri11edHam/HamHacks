package net.grilledham.hamhacks.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.ModuleManager;
import net.grilledham.hamhacks.modules.Setting;
import net.grilledham.hamhacks.modules.render.ClickGUI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClickGUIScreen extends Screen {
	
	private final Map<Module.Category, List<Module>> categories = new HashMap<>();
	
	private Module.Category clickedCategory = null;
	private boolean pressed = false;
	private int lastMouseX;
	private int lastMouseY;
	
	private static Module expandedModule;
	private static Setting expandedSetting;
	
	public ClickGUIScreen() {
		super(new TranslatableText("menu.hamhacks.clickgui"));
	}
	
	@Override
	protected void init() {
		super.init();
		for(Module.Category category : Module.Category.values()) {
			List<Module> categoryModules = ModuleManager.getModules(category);
			categories.put(category, categoryModules);
			category.setPos(category.getX(), category.getY());
			category.getBox().setScaleFactor(MinecraftClient.getInstance().options.guiScale);
			category.resize();
		}
	}
	
	@Override
	public void resize(MinecraftClient client, int width, int height) {
		super.resize(client, width, height);
		for(Module.Category category : Module.Category.values()) {
			category.getBox().setScaleFactor(MinecraftClient.getInstance().options.guiScale);
			category.resize();
		}
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
		for(Module.Category category : categories.keySet()) {
			drawCategory(matrices, mouseX, mouseY, category);
		}
		if(pressed && clickedCategory != null) {
			clickedCategory.setPos(clickedCategory.getX() + (mouseX - lastMouseX), clickedCategory.getY() + (mouseY - lastMouseY));
		}
		pressed = GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), 0) == GLFW.GLFW_PRESS;
		if(!pressed) {
			clickedCategory = null;
		}
		lastMouseX = mouseX;
		lastMouseY = mouseY;
	}
	
	private void drawCategory(MatrixStack matrices, int mouseX, int mouseY, Module.Category category) {
		int x = category.getX();
		int y = category.getY();
		int w = category.getWidth() - textRenderer.fontHeight - 2;
		int fullWidth = category.getWidth();
		int h = category.getHeight();
		DrawableHelper.fill(matrices, x, y, x + fullWidth, y + h, (int)ClickGUI.getInstance().bgColor.getColor());
		DrawableHelper.fill(matrices, x, y - 2, x + fullWidth, y, (int)ClickGUI.getInstance().barColor.getColor());
		DrawableHelper.drawStringWithShadow(matrices, textRenderer, category.getText(), x + w - textRenderer.getWidth(category.getText()) - 2, y + 2, (int)ClickGUI.getInstance().textColor.getColor());
		float translateX = x + w + 1 + textRenderer.fontHeight / 2f;
		float translateY = y + 2 + textRenderer.fontHeight / 2f;
		matrices.push();
		matrices.translate(translateX, translateY, 0);
		matrices.multiply(new Quaternion(new Vec3f(0, 0, 1), category.isExpanded() ? -90 : 0, true));
		matrices.translate(-translateX, -translateY, 0);
		DrawableHelper.drawStringWithShadow(matrices, textRenderer, "<", (int)(translateX - (textRenderer.getWidth("<") / 2f)), (int)(translateY - (textRenderer.fontHeight) / 2f), (int)ClickGUI.getInstance().textColor.getColor());
		matrices.pop();
		boolean hovered = mouseX > x && mouseX <= x + w && mouseY > y && mouseY <= y + h;
		boolean dropDownHovered = mouseX > x + w && mouseX <= x + fullWidth && mouseY > y && mouseY <= y + h;
		if(hovered) {
			DrawableHelper.fill(matrices, x, y, x + w, y + h, 0x20ffffff);
			if(pressed && clickedCategory == null) {
				clickedCategory = category;
			}
		}
		if(dropDownHovered && (clickedCategory == null || clickedCategory == category)) {
			DrawableHelper.fill(matrices, x + w, y, x + fullWidth, y + h, 0x20ffffff);
			if(GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), 0) == GLFW.GLFW_PRESS && !pressed) {
				category.expand(!category.isExpanded());
			}
		}
		
		/* Category Modules */
		if(category.isExpanded()) {
			int moduleX = x + 2;
			int moduleY = y + h;
			int moduleW = w - 2;
			int moduleFullWidth = fullWidth - 2;
			for(Module module : categories.get(category)) {
				drawModule(matrices, mouseX, mouseY, moduleX, moduleY, moduleW, moduleFullWidth, h, module);
				
				moduleY += h;
			}
		}
	}
	
	private void drawModule(MatrixStack matrices, int mouseX, int mouseY, int x, int y, int w, int fullWidth, int h, Module module) {
		DrawableHelper.fill(matrices, x, y, x + fullWidth, y + h, (int)ClickGUI.getInstance().bgColor.getColor());
		DrawableHelper.fill(matrices, x - 2, y, x, y + h, module.isEnabled() ? 0x8000a400 : 0x80a40000);
		DrawableHelper.drawStringWithShadow(matrices, textRenderer, module.getName(), x + w - textRenderer.getWidth(module.getName()) - 2, y + 2, (int)ClickGUI.getInstance().textColor.getColor());
		float moduleTranslateX = x + w + 1 + textRenderer.fontHeight / 2f;
		float moduleTranslateY = y + 2 + textRenderer.fontHeight / 2f;
		matrices.push();
		matrices.translate(moduleTranslateX, moduleTranslateY, 0);
		matrices.multiply(new Quaternion(new Vec3f(0, 0, 1), expandedModule == module ? -90 : 0, true));
		matrices.translate(-moduleTranslateX, -moduleTranslateY, 0);
		DrawableHelper.drawStringWithShadow(matrices, textRenderer, "<", (int)(moduleTranslateX - (textRenderer.getWidth("<") / 2f)), (int)(moduleTranslateY - (textRenderer.fontHeight) / 2f), (int)ClickGUI.getInstance().textColor.getColor());
		matrices.pop();
		boolean hovered = mouseX > x && mouseX <= x + w && mouseY > y && mouseY <= y + h;
		boolean dropDownHovered = mouseX > x + w && mouseX <= x + fullWidth && mouseY > y && mouseY <= y + h;
		if(hovered && (expandedModule == null || expandedModule == module)) {
			DrawableHelper.fill(matrices, x, y, x + w, y + h, 0x20ffffff);
			if(GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), 0) == GLFW.GLFW_PRESS && !pressed) {
				module.toggle();
			}
		}
		if(dropDownHovered && (expandedModule == null || expandedModule == module)) {
			DrawableHelper.fill(matrices, x + w, y, x + fullWidth, y + h, 0x20ffffff);
			if(GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), 0) == GLFW.GLFW_PRESS && !pressed) {
				if(expandedModule == module) {
					expandedModule = null;
					expandedSetting = null;
				} else {
					expandedModule = module;
				}
			}
		}
		
		/* Module Settings */
		if(expandedModule == module) {
			int settingX = x + fullWidth;
			int settingY = y;
			int settingW = 0;
			for(Setting setting : module.getSettings()) {
				settingW = Math.max(settingW, getSettingWidth(setting));
			}
			settingW += 4;
			try {
				for(Setting setting : module.getSettings()) {
					drawSetting(matrices, mouseX, mouseY, settingX, settingY, settingW, h, setting);
					
					settingY += h;
				}
			} catch(ConcurrentModificationException ignored) {}
		}
	}
	
	private void drawSetting(MatrixStack matrices, int mouseX, int mouseY, int x, int y, int w, int h, Setting setting) {
		switch(setting.getType()) {
			case INT -> drawIntSetting(matrices, mouseX, mouseY, x, y, w, h, setting);
			case LIST -> drawListSetting(matrices, mouseX, mouseY, x, y, w, h, setting);
			case COLOR -> drawColorSetting(matrices, mouseX, mouseY, x, y, w, h, setting);
			case FLOAT -> drawFloatSetting(matrices, mouseX, mouseY, x, y, w, h, setting);
			case STRING -> drawStringSetting(matrices, mouseX, mouseY, x, y, w, h, setting);
			case BOOLEAN -> drawBooleanSetting(matrices, mouseX, mouseY, x, y, w, h, setting);
			case KEYBIND -> drawKeybindSetting(matrices, mouseX, mouseY, x, y, w, h, setting);
		}
	}
	
	private void drawIntSetting(MatrixStack matrices, int mouseX, int mouseY, int x, int y, int w, int h, Setting setting) {
		DrawableHelper.fill(matrices, x, y, x + w, y + h, (int)ClickGUI.getInstance().bgColor.getColor());
		String text = setting.getInt() + " " + setting.getName();
		DrawableHelper.drawStringWithShadow(matrices, textRenderer, text, x + w - textRenderer.getWidth(text) - 4, y + 2, (int)ClickGUI.getInstance().textColor.getColor());
		boolean hovered = mouseX > x && mouseX <= x + w && mouseY > y && mouseY <= y + h;
		if(hovered) {
			int sliderX = x + w - textRenderer.getWidth(text) - 6 - 100;
			int sliderY = (int)(y + h / 2f - 1);
			int sliderW = 100;
			int sliderH = 2;
			float percentage = (setting.getInt() - setting.getMin()) / setting.getMax();
			DrawableHelper.fill(matrices, sliderX, sliderY, sliderX + sliderW, sliderY + sliderH, 0x40a4a4a4);
			DrawableHelper.fill(matrices, sliderX, sliderY, (int)(sliderX + (sliderW * percentage)), sliderY + sliderH, 0xffa4a4a4);
			DrawableHelper.fill(matrices, (int)(sliderX + (sliderW * percentage)), sliderY - 1, (int)(sliderX + (sliderW * percentage)) + 1, sliderY + sliderH + 1, 0xffffffff);
			if(GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), 0) == GLFW.GLFW_PRESS) {
				float newPercentage = (mouseX - sliderX) / (float)sliderW;
				int newVal = (int)((newPercentage * setting.getMax()) + setting.getMin());
				setting.setValue(newVal);
			}
		}
	}
	
	private void drawListSetting(MatrixStack matrices, int mouseX, int mouseY, int x, int y, int w, int h, Setting setting) {
		DrawableHelper.fill(matrices, x, y, x + w, y + h, (int)ClickGUI.getInstance().bgColor.getColor());
		String text = setting.getName() + " - " + setting.getString();
		DrawableHelper.drawStringWithShadow(matrices, textRenderer, text, x + w - textRenderer.getWidth(text) - 4, y + 2, (int)ClickGUI.getInstance().textColor.getColor());
		boolean hovered = mouseX > x && mouseX <= x + w && mouseY > y && mouseY <= y + h;
		if(hovered) {
			DrawableHelper.fill(matrices, x, y, x + w, y + h, 0x20ffffff);
			if(GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), 0) == GLFW.GLFW_PRESS && !pressed) {
				if(expandedSetting == setting) {
					expandedSetting = null;
				} else {
					expandedSetting = setting;
				}
			}
		}
		if(expandedSetting == setting) {
			int optionsX = x + w;
			int optionsY = y;
			int optionsW = 0;
			for(String s : setting.getPossibleValues()) {
				optionsW = Math.max(optionsW, textRenderer.getWidth(s) + 4);
			}
			int optionsH = h * setting.getPossibleValues().size() - 1;
			int optionW = optionsW;
			int optionH = h;
			DrawableHelper.fill(matrices, optionsX, optionsY, optionsX + optionsW, optionsY + optionsH, (int)ClickGUI.getInstance().bgColor.getColor());
			int i = 0;
			for(String s : setting.getPossibleValues()) {
				if(!s.equals(setting.getString())) {
					int optionX = optionsX;
					int optionY = optionsY + optionsH * i;
					DrawableHelper.drawStringWithShadow(matrices, textRenderer, s, optionX + optionW - textRenderer.getWidth(s) - 2, optionY + 2, (int)ClickGUI.getInstance().textColor.getColor());
					boolean optionHovered = mouseX > optionX && mouseX <= optionX + optionW && mouseY > optionY && mouseY <= optionY + optionH;
					if(optionHovered) {
						DrawableHelper.fill(matrices, optionX, optionY, optionX + optionW, optionY + optionH, 0x20ffffff);
						if(GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), 0) == GLFW.GLFW_PRESS && !pressed) {
							expandedSetting = null;
							setting.setValue(s);
						}
					}
					i++;
				}
			}
		}
	}
	
	private void drawColorSetting(MatrixStack matrices, int mouseX, int mouseY, int x, int y, int w, int h, Setting setting) {
		DrawableHelper.fill(matrices, x, y, x + w, y + h, (int)ClickGUI.getInstance().bgColor.getColor());
		DrawableHelper.drawStringWithShadow(matrices, textRenderer, setting.getName(), x + w - textRenderer.getWidth(setting.getName()) - 4 - 18, y + 2, (int)ClickGUI.getInstance().textColor.getColor());
		DrawableHelper.fill(matrices, x + w - 4 - 16, y + 2, x + w - 2, y + h - 2, (int)setting.getColor());
		boolean hovered = mouseX > x && mouseX <= x + w && mouseY > y && mouseY <= y + h;
		if(hovered) {
			DrawableHelper.fill(matrices, x, y, x + w, y + h, 0x20ffffff);
			if(GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), 0) == GLFW.GLFW_PRESS && !pressed) {
				if(expandedSetting != setting) {
					expandedSetting = setting;
				} else {
					expandedSetting = null;
				}
			}
		}
		if(expandedSetting == setting) {
			drawColorPicker(matrices, mouseX, mouseY, x + w, y, setting);
		}
	}
	
	private void drawFloatSetting(MatrixStack matrices, int mouseX, int mouseY, int x, int y, int w, int h, Setting setting) {
		DrawableHelper.fill(matrices, x, y, x + w, y + h, (int)ClickGUI.getInstance().bgColor.getColor());
		String text = String.format("%.2f", setting.getFloat()) + " " + setting.getName();
		DrawableHelper.drawStringWithShadow(matrices, textRenderer, text, x + w - textRenderer.getWidth(text) - 4, y + 2, (int)ClickGUI.getInstance().textColor.getColor());
		boolean hovered = mouseX > x && mouseX <= x + w && mouseY > y && mouseY <= y + h;
		if(hovered) {
			int sliderX = x + w - textRenderer.getWidth(text) - 6 - 100;
			int sliderY = (int)(y + h / 2f - 1);
			int sliderW = 100;
			int sliderH = 2;
			float percentage = (setting.getFloat() - setting.getMin()) / setting.getMax();
			DrawableHelper.fill(matrices, sliderX, sliderY, sliderX + sliderW, sliderY + sliderH, 0x40a4a4a4);
			DrawableHelper.fill(matrices, sliderX, sliderY, (int)(sliderX + (sliderW * percentage)), sliderY + sliderH, 0xffa4a4a4);
			DrawableHelper.fill(matrices, (int)(sliderX + (sliderW * percentage)), sliderY - 1, (int)(sliderX + (sliderW * percentage)) + 1, sliderY + sliderH + 1, 0xffffffff);
			if(GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), 0) == GLFW.GLFW_PRESS) {
				float newPercentage = (mouseX - sliderX) / (float)sliderW;
				float newVal = (newPercentage * setting.getMax()) + setting.getMin();
				setting.setValue(newVal);
			}
		}
	}
	
	private void drawStringSetting(MatrixStack matrices, int mouseX, int mouseY, int x, int y, int w, int h, Setting setting) {
		DrawableHelper.fill(matrices, x, y, x + w, y + h, (int)ClickGUI.getInstance().bgColor.getColor());
		DrawableHelper.drawStringWithShadow(matrices, textRenderer, setting.getName(), x + w - textRenderer.getWidth(setting.getName()) - 4, y + 2, (int)ClickGUI.getInstance().textColor.getColor());
		boolean hovered = mouseX > x && mouseX <= x + w && mouseY > y && mouseY <= y + h;
		if(hovered) {
			DrawableHelper.fill(matrices, x, y, x + w, y + h, 0x20ffffff);
			if(GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), 0) == GLFW.GLFW_PRESS && !pressed) {
			}
		}
	}
	
	private void drawBooleanSetting(MatrixStack matrices, int mouseX, int mouseY, int x, int y, int w, int h, Setting setting) {
		DrawableHelper.fill(matrices, x + 2, y, x + w, y + h, (int)ClickGUI.getInstance().bgColor.getColor());
		DrawableHelper.fill(matrices, x, y, x + 2, y + h, setting.getBool() ? 0x8000a400 : 0x80a40000);
		DrawableHelper.drawStringWithShadow(matrices, textRenderer, setting.getName(), x + w - textRenderer.getWidth(setting.getName()) - 4, y + 2, (int)ClickGUI.getInstance().textColor.getColor());
		boolean hovered = mouseX > x && mouseX <= x + w && mouseY > y && mouseY <= y + h;
		if(hovered) {
			DrawableHelper.fill(matrices, x + 2, y, x + w, y + h, 0x20ffffff);
			if(GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), 0) == GLFW.GLFW_PRESS && !pressed) {
				setting.setValue(!setting.getBool());
			}
		}
	}
	
	private void drawKeybindSetting(MatrixStack matrices, int mouseX, int mouseY, int x, int y, int w, int h, Setting setting) {
		DrawableHelper.fill(matrices, x, y, x + w, y + h, (int)ClickGUI.getInstance().bgColor.getColor());
		String text = setting.getName() + " [" + (expandedSetting == setting ? "Listening..." : setting.getKeybind().getName()) + "]";
		DrawableHelper.drawStringWithShadow(matrices, textRenderer, text, x + w - textRenderer.getWidth(text) - 2, y + 2, (int)ClickGUI.getInstance().textColor.getColor());
		boolean hovered = mouseX > x && mouseX <= x + w && mouseY > y && mouseY <= y + h;
		if(hovered) {
			DrawableHelper.fill(matrices, x, y, x + w, y + h, 0x20ffffff);
			if(GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), 0) == GLFW.GLFW_PRESS && !pressed) {
				if(expandedSetting != setting) {
					expandedSetting = setting;
				}
			}
			if(GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), 1) == GLFW.GLFW_PRESS && expandedSetting != setting) {
				setting.getKeybind().resetKey();
			}
		}
	}
	
	private void drawColorPicker(MatrixStack matrices, int mouseX, int mouseY, int x, int y, Setting setting) {
		int w = 135;
		int h = 120;
		DrawableHelper.fill(matrices, x, y, x + w, y + h, (int)ClickGUI.getInstance().bgColor.getColor());
		
		int color = (int)setting.getColor();
		int r = (color >> 16 & 255);
		int g = (color >> 8 & 255);
		int b = (color & 255);
		float[] hsb = Color.RGBtoHSB(r, g, b, null);
		
		/* Saturation/Brightness Picker */
		int sbPickerX = x + 2;
		int sbPickerY = y + 2;
		int sbPickerW = 101;
		int sbPickerH = 101;
		fillColorGradient(matrices, sbPickerX, sbPickerY, sbPickerX + sbPickerW, sbPickerY + sbPickerH, hsb[0]);
		// Pointer
		int pointerX = (int)(sbPickerX + (hsb[1] * (sbPickerW - 1)));
		int pointerY = (int)(sbPickerY + ((1 - hsb[2]) * (sbPickerH - 1)));
		DrawableHelper.fill(matrices, pointerX - 1, pointerY - 1, pointerX + 2, pointerY + 2, 0xff202020);
		DrawableHelper.fill(matrices, pointerX, pointerY, pointerX + 1, pointerY + 1, 0xffffffff);
		
		/* Hue Slider */
		int hueSliderX = sbPickerX + sbPickerW + 2;
		int hueSliderY = sbPickerY;
		int hueSliderW = 13;
		int hueSliderH = 101;
		// Draw bg
		{
			RenderSystem.disableTexture();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
			Matrix4f matrix = matrices.peek().getModel();
			int startC = Color.HSBtoRGB(0, 1, 1);
			int endC;
			int startX = hueSliderX;
			int startY = hueSliderY;
			int endX = hueSliderX + hueSliderW;
			int endY = (int)(startY + (hueSliderH / 6f));
			for(int i = 0; i < 6; i++) {
				switch(i) {
					case 0 -> endC = Color.HSBtoRGB(1 / 6f, 1, 1);
					case 1 -> endC = Color.HSBtoRGB(2 / 6f, 1, 1);
					case 2 -> endC = Color.HSBtoRGB(3 / 6f, 1, 1);
					case 3 -> endC = Color.HSBtoRGB(4 / 6f, 1, 1);
					case 4 -> endC = Color.HSBtoRGB(5 / 6f, 1, 1);
					case 5 -> endC = Color.HSBtoRGB(6 / 6f, 1, 1);
					default -> endC = Color.HSBtoRGB(1, 1, 1);
				}
				float sa = (float)(startC >> 24 & 255) / 255.0F;
				float sr = (float)(startC >> 16 & 255) / 255.0F;
				float sg = (float)(startC >> 8 & 255) / 255.0F;
				float sb = (float)(startC & 255) / 255.0F;
				float ea = (float)(endC >> 24 & 255) / 255.0F;
				float er = (float)(endC >> 16 & 255) / 255.0F;
				float eg = (float)(endC >> 8 & 255) / 255.0F;
				float eb = (float)(endC & 255) / 255.0F;
				bufferBuilder.vertex(matrix, (float)endX, (float)startY, (float)0).color(sr, sg, sb, sa).next();
				bufferBuilder.vertex(matrix, (float)startX, (float)startY, (float)0).color(sr, sg, sb, sa).next();
				bufferBuilder.vertex(matrix, (float)startX, (float)endY, (float)0).color(er, eg, eb, ea).next();
				bufferBuilder.vertex(matrix, (float)endX, (float)endY, (float)0).color(er, eg, eb, ea).next();
				startY = endY;
				endY = (int)(startY + (hueSliderH / 6f)) + 1;
				startC = endC;
			}
			tessellator.draw();
			RenderSystem.disableBlend();
			RenderSystem.enableTexture();
		}
		// Slider
		int sliderY = (int)(hueSliderY + (hsb[0] * (hueSliderH - 1)));
		DrawableHelper.fill(matrices, hueSliderX - 1, sliderY - 1, hueSliderX + hueSliderW, sliderY, 0xff202020);
		DrawableHelper.fill(matrices, hueSliderX - 1, sliderY + 1, hueSliderX + hueSliderW, sliderY + 2, 0xff202020);
		DrawableHelper.fill(matrices, hueSliderX - 1, sliderY - 1, hueSliderX, sliderY + 2, 0xff202020);
		DrawableHelper.fill(matrices, hueSliderX + hueSliderW, sliderY - 1, hueSliderX + hueSliderW + 1, sliderY + 2, 0xff202020);
		
		/* Alpha Slider */
		int alphaSliderX = hueSliderX + hueSliderW + 2;
		int alphaSliderY = hueSliderY;
		int alphaSliderW = 13;
		int alphaSliderH = 101;
		// Draw bg
		{
			RenderSystem.disableTexture();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
			Matrix4f matrix = matrices.peek().getModel();
			int startC = 0xffffffff;
			int endC = 0x00ffffff;
			int startX = alphaSliderX;
			int startY = alphaSliderY;
			int endX = alphaSliderX + alphaSliderW;
			int endY = alphaSliderY + alphaSliderH;
			float sa = (float)(startC >> 24 & 255) / 255.0F;
			float sr = (float)(startC >> 16 & 255) / 255.0F;
			float sg = (float)(startC >> 8 & 255) / 255.0F;
			float sb = (float)(startC & 255) / 255.0F;
			float ea = (float)(endC >> 24 & 255) / 255.0F;
			float er = (float)(endC >> 16 & 255) / 255.0F;
			float eg = (float)(endC >> 8 & 255) / 255.0F;
			float eb = (float)(endC & 255) / 255.0F;
			bufferBuilder.vertex(matrix, (float)endX, (float)startY, (float)0).color(sr, sg, sb, sa).next();
			bufferBuilder.vertex(matrix, (float)startX, (float)startY, (float)0).color(sr, sg, sb, sa).next();
			bufferBuilder.vertex(matrix, (float)startX, (float)endY, (float)0).color(er, eg, eb, ea).next();
			bufferBuilder.vertex(matrix, (float)endX, (float)endY, (float)0).color(er, eg, eb, ea).next();
			tessellator.draw();
			RenderSystem.disableBlend();
			RenderSystem.enableTexture();
		}
		// Slider
		sliderY = (int)(alphaSliderY + ((1 - ((float)(color >> 24 & 255) / 255.0F)) * (alphaSliderH - 1)));
		DrawableHelper.fill(matrices, alphaSliderX - 1, sliderY - 1, alphaSliderX + alphaSliderW, sliderY, 0xff202020);
		DrawableHelper.fill(matrices, alphaSliderX - 1, sliderY + 1, alphaSliderX + alphaSliderW, sliderY + 2, 0xff202020);
		DrawableHelper.fill(matrices, alphaSliderX - 1, sliderY - 1, alphaSliderX, sliderY + 2, 0xff202020);
		DrawableHelper.fill(matrices, alphaSliderX + alphaSliderW, sliderY - 1, alphaSliderX + alphaSliderW + 1, sliderY + 2, 0xff202020);
		
		/* Hex Input */
		
		/* Chroma Button */
		int chromaX = sbPickerX;
		int chromaY = sbPickerY + sbPickerH + 2;
		int chromaW = textRenderer.getWidth("Chroma") + textRenderer.fontHeight + 2;
		int chromaH = textRenderer.fontHeight;
		DrawableHelper.fill(matrices, chromaX, chromaY, chromaX + chromaH, chromaY + chromaH, setting.useChroma() ? 0x8000a400 : 0x80a40000);
		DrawableHelper.drawStringWithShadow(matrices, textRenderer, "Chroma", chromaX + textRenderer.fontHeight + 2, chromaY, (int)ClickGUI.getInstance().textColor.getColor());
		
		/* Click Checks */
		boolean sbHovered = mouseX >= sbPickerX && mouseX < sbPickerX + sbPickerW && mouseY >= sbPickerY && mouseY < sbPickerY + sbPickerH;
		boolean hueHovered = mouseX >= hueSliderX && mouseX < hueSliderX + hueSliderW && mouseY >= hueSliderY && mouseY < hueSliderY + hueSliderH;
		boolean alphaHovered = mouseX >= alphaSliderX && mouseX < alphaSliderX + alphaSliderW && mouseY >= alphaSliderY && mouseY < alphaSliderY + alphaSliderH;
//		boolean hexHovered = mouseX >= alphaSliderX && mouseX < alphaSliderX + alphaSliderW && mouseY >= alphaSliderY && mouseY < alphaSliderY + alphaSliderH;
		boolean chromaHovered = mouseX >= chromaX && mouseX < chromaX + chromaW && mouseY >= chromaY && mouseY < chromaY + chromaH;
		if(sbHovered) {
			if(GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), 0) == GLFW.GLFW_PRESS) {
				int oldAlpha = color >> 24 & 255;
				float newSat = (mouseX - sbPickerX) / (float)(sbPickerW - 1);
				float newBrt = 1 - ((mouseY - sbPickerY) / (float)(sbPickerH - 1));
				int newColor = Color.HSBtoRGB(hsb[0], newSat, newBrt);
				newColor = newColor & 0xffffff;
				newColor += oldAlpha << 24;
				setting.setValue(newColor);
			}
		}
		if(hueHovered) {
			if(GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), 0) == GLFW.GLFW_PRESS) {
				int oldAlpha = color >> 24 & 255;
				float newHue = (mouseY - hueSliderY) / (float)(hueSliderH - 1);
				int newColor = Color.HSBtoRGB(newHue, hsb[1], hsb[2]);
				newColor = newColor & 0xffffff;
				newColor += oldAlpha << 24;
				setting.setValue(newColor);
			}
		}
		if(alphaHovered) {
			if(GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), 0) == GLFW.GLFW_PRESS) {
				int oldR = color >> 16 & 255;
				int oldG = color >> 8 & 255;
				int oldB = color & 255;
				int newAlpha = (int)((1 - ((mouseY - alphaSliderY) / (float)(alphaSliderH - 1))) * 255);
				int newColor = new Color(oldR, oldG, oldB, newAlpha).getRGB();
				setting.setValue(newColor);
			}
		}
		if(chromaHovered) {
			DrawableHelper.fill(matrices, chromaX, chromaY, chromaX + chromaH, chromaY + chromaH, 0x20ffffff);
			if(GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), 0) == GLFW.GLFW_PRESS && !pressed) {
				setting.setChroma(!setting.useChroma());
			}
		}
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if(expandedSetting != null) {
			switch(expandedSetting.getType()) {
				case COLOR -> {
				
				}
				case STRING -> {
				
				}
				case KEYBIND -> {
					if(keyCode == GLFW.GLFW_KEY_ESCAPE) {
						expandedSetting.getKeybind().setKey(0);
					} else {
						expandedSetting.getKeybind().setKey(keyCode);
					}
					expandedSetting = null;
					return true;
				}
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(expandedSetting != null) {
			if(expandedSetting.getType() == Setting.Type.KEYBIND) {
				expandedSetting.getKeybind().setKey(button, true);
				expandedSetting = null;
				pressed = true;
				return true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	private int getSettingWidth(Setting setting) {
		switch(setting.getType()) {
			case INT -> {
				return textRenderer.getWidth(setting.getInt() + " " + setting.getName()) + 8 + 100;
			}
			case LIST -> {
				return textRenderer.getWidth(setting.getName()) + 6 + textRenderer.getWidth(" - " + setting.getString()) + textRenderer.fontHeight;
			}
			case COLOR -> {
				return textRenderer.getWidth(setting.getName()) + 6 + 18;
			}
			case FLOAT -> {
				return textRenderer.getWidth(String.format("%.2f", setting.getFloat()) + " " + setting.getName()) + 8 + 100;
			}
			case STRING -> {
				return textRenderer.getWidth(setting.getName()) + 6 + 100;
			}
			case BOOLEAN -> {
				return textRenderer.getWidth(setting.getName()) + 6;
			}
			case KEYBIND -> {
				return textRenderer.getWidth(setting.getName() + " [" + (expandedSetting == setting ? "Listening..." : setting.getKeybind().getName()) + "]") + 4;
			}
			default -> {
				return textRenderer.getWidth(setting.getName()) + 4;
			}
		}
	}
	
	protected void fillColorGradient(MatrixStack matrices, int startX, int startY, int endX, int endY, float hue) {
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		fillColorGradient(matrices.peek().getModel(), bufferBuilder, startX, startY, endX, endY, hue);
		tessellator.draw();
		RenderSystem.disableBlend();
		RenderSystem.enableTexture();
	}
	
	protected static void fillColorGradient(Matrix4f matrix, BufferBuilder bufferBuilder, int startX, int startY, int endX, int endY, float hue) {
		int tr = Color.HSBtoRGB(hue, 1, 1);
		float atr = (float)(tr >> 24 & 255) / 255.0F;
		float rtr = (float)(tr >> 16 & 255) / 255.0F;
		float gtr = (float)(tr >> 8 & 255) / 255.0F;
		float btr = (float)(tr & 255) / 255.0F;
		int br = Color.HSBtoRGB(hue, 1, 0);
		float abr = (float)(br >> 24 & 255) / 255.0F;
		float rbr = (float)(br >> 16 & 255) / 255.0F;
		float gbr = (float)(br >> 8 & 255) / 255.0F;
		float bbr = (float)(br & 255) / 255.0F;
		int tl = Color.HSBtoRGB(hue, 0, 1);
		float atl = (float)(tl >> 24 & 255) / 255.0F;
		float rtl = (float)(tl >> 16 & 255) / 255.0F;
		float gtl = (float)(tl >> 8 & 255) / 255.0F;
		float btl = (float)(tl & 255) / 255.0F;
		int bl = Color.HSBtoRGB(hue, 0, 0);
		float abl = (float)(bl >> 24 & 255) / 255.0F;
		float rbl = (float)(bl >> 16 & 255) / 255.0F;
		float gbl = (float)(bl >> 8 & 255) / 255.0F;
		float bbl = (float)(bl & 255) / 255.0F;
		bufferBuilder.vertex(matrix, (float)endX, (float)startY, (float)0).color(rtr, gtr, btr, atr).next();
		bufferBuilder.vertex(matrix, (float)startX, (float)startY, (float)0).color(rtl, gtl, btl, atl).next();
		bufferBuilder.vertex(matrix, (float)startX, (float)endY, (float)0).color(rbl, gbl, bbl, abl).next();
		bufferBuilder.vertex(matrix, (float)endX, (float)endY, (float)0).color(rbr, gbr, bbr, abr).next();
	}
	
	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
