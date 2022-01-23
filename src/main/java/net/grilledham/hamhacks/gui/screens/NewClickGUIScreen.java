package net.grilledham.hamhacks.gui.screens;

import net.grilledham.hamhacks.gui.parts.impl.CategoryPart;
import net.grilledham.hamhacks.modules.Module;
import net.grilledham.hamhacks.modules.render.ClickGUI;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.List;

public class NewClickGUIScreen extends Screen {
	
	private final Screen last;
	
	private final List<CategoryPart> categories = new ArrayList<>();
	
	public NewClickGUIScreen(Screen last) {
		super(new TranslatableText("menu.hamhacks.clickgui"));
		this.last = last;
		for(Module.Category category : Module.Category.values()) {
			categories.add(new CategoryPart(this, category));
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
		for(CategoryPart category : categories) {
			category.draw(matrices, mouseX, mouseY, delta);
		}
		matrices.pop();
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		mouseX = (mouseX * client.getWindow().getScaleFactor()) / ClickGUI.getInstance().scale.getValue();
		mouseY = (mouseY * client.getWindow().getScaleFactor()) / ClickGUI.getInstance().scale.getValue();
		for(CategoryPart category : categories) {
			if(category.click(mouseX, mouseY, button)) {
				return true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		mouseX = (mouseX * client.getWindow().getScaleFactor()) / ClickGUI.getInstance().scale.getValue();
		mouseY = (mouseY * client.getWindow().getScaleFactor()) / ClickGUI.getInstance().scale.getValue();
		for(CategoryPart category : categories) {
			if(category.release(mouseX, mouseY, button)) {
				return true;
			}
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		mouseX = (mouseX * client.getWindow().getScaleFactor()) / ClickGUI.getInstance().scale.getValue();
		mouseY = (mouseY * client.getWindow().getScaleFactor()) / ClickGUI.getInstance().scale.getValue();
		for(CategoryPart category : categories) {
			if(category.drag(mouseX, mouseY, button, deltaX, deltaY)) {
				return true;
			}
		}
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}
	
	@Override
	public void onClose() {
		client.setScreen(last);
	}

	@Override
	public boolean shouldPause() {
		return false;
	}
}
