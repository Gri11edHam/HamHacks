package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.HamHacksClient;
import net.grilledham.hamhacks.gui.element.impl.ButtonElement;
import net.grilledham.hamhacks.gui.screen.impl.ChangelogScreen;
import net.grilledham.hamhacks.gui.screen.impl.NewVersionScreen;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.grilledham.hamhacks.util.Updater;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen {
	
	private final ButtonElement changelogButton = new ButtonElement("Changelog", 2F, 2, 100, 20, (float)MinecraftClient.getInstance().getWindow().getScaleFactor(), () -> {
		TitleScreen $this = (TitleScreen)(Object)this;
		MinecraftClient.getInstance().setScreen(new ChangelogScreen($this));
	});
	
	private final ButtonElement updateButton = new ButtonElement("Update", 2, 24, 100, 20, (float)MinecraftClient.getInstance().getWindow().getScaleFactor(), () -> {
		TitleScreen $this = (TitleScreen)(Object)this;
		MinecraftClient.getInstance().setScreen(new NewVersionScreen($this));
	});
	private final ButtonElement dontUpdateButton = new ButtonElement("X", 104, 24, 20, 20, (float)MinecraftClient.getInstance().getWindow().getScaleFactor(), () -> {
		HamHacksClient.seenVersion = Updater.getLatest();
	});
	
	private MixinTitleScreen() {
		super(Text.translatable(""));
	}
	
	@Inject(method = "render", at = @At("TAIL"))
	public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		int showChangelogButton = PageManager.getPage(ClickGUI.class).showChangelogButton.get();
		if(showChangelogButton == 0 || (showChangelogButton == 1 && HamHacksClient.updated)) {
			changelogButton.render(context, mouseX, mouseY, 0, 0, delta);
		}
		if(Updater.newVersionAvailable() && !HamHacksClient.seenVersion.isNewerThan(HamHacksClient.VERSION)) {
			updateButton.setText("Update (" + Updater.getLatest().getVersion(0, true) + ")");
			updateButton.render(context, mouseX, mouseY, 0, 0, delta);
			dontUpdateButton.render(context, mouseX, mouseY, 0, 0, delta);
		}
	}
	
	@Inject(method = "mouseClicked", at = @At("TAIL"), cancellable = true)
	public void clicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
		int showChangelogButton = PageManager.getPage(ClickGUI.class).showChangelogButton.get();
		if((showChangelogButton == 0 || (showChangelogButton == 1 && HamHacksClient.updated)) && changelogButton.click(mouseX, mouseY, 0, 0, button)) {
			cir.setReturnValue(true);
		}
		if(Updater.newVersionAvailable() && !HamHacksClient.seenVersion.isNewerThan(HamHacksClient.VERSION)) {
			if(updateButton.click(mouseX, mouseY, 0, 0, button)) {
				cir.setReturnValue(true);
			}
			if(dontUpdateButton.click(mouseX, mouseY, 0, 0, button)) {
				cir.setReturnValue(true);
			}
		}
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		int showChangelogButton = PageManager.getPage(ClickGUI.class).showChangelogButton.get();
		if((showChangelogButton == 0 || (showChangelogButton == 1 && HamHacksClient.updated)) && changelogButton.release(mouseX, mouseY, 0, 0, button)) {
			return true;
		}
		if(Updater.newVersionAvailable() && !HamHacksClient.seenVersion.isNewerThan(HamHacksClient.VERSION)) {
			if(updateButton.release(mouseX, mouseY, 0, 0, button)) {
				return true;
			}
			if(dontUpdateButton.release(mouseX, mouseY, 0, 0, button)) {
				return true;
			}
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}
}
