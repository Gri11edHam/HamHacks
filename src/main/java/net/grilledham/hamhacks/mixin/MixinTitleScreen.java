package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.gui.parts.impl.ButtonPart;
import net.grilledham.hamhacks.gui.screens.ChangelogScreen;
import net.grilledham.hamhacks.gui.screens.UpdateScreen;
import net.grilledham.hamhacks.util.Updater;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen {
	
	private final ButtonPart changelogButton = new ButtonPart("Changelog", 2, 2, 100, 20, () -> {
		TitleScreen $this = (TitleScreen)(Object)this;
		MinecraftClient.getInstance().setScreen(new ChangelogScreen($this));
	});
	
	private final ButtonPart updateButton = new ButtonPart("Update", 2, 24, 100, 20, () -> {
		Updater.update();
		TitleScreen $this = (TitleScreen)(Object)this;
		MinecraftClient.getInstance().setScreen(new UpdateScreen($this));
	});
	
	private MixinTitleScreen() {
		super(Text.translatable(""));
	}
	
	@Inject(method = "render", at = @At("TAIL"))
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		changelogButton.draw(matrices, mouseX, mouseY, delta);
		if(Updater.newVersionAvailable()) {
			updateButton.setText("Update (" + Updater.getLatest().getVersion(0, true) + ")");
			updateButton.draw(matrices, mouseX, mouseY, delta);
		}
	}
	
	@Inject(method = "mouseClicked", at = @At("TAIL"), cancellable = true)
	public void clicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
		if(changelogButton.click(mouseX, mouseY, button)) {
			cir.setReturnValue(true);
		}
		if(Updater.newVersionAvailable() && updateButton.click(mouseX, mouseY, button)) {
			cir.setReturnValue(true);
		}
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if(changelogButton.release(mouseX, mouseY, button)) {
			return true;
		}
		if(Updater.newVersionAvailable() && updateButton.release(mouseX, mouseY, button)) {
			return true;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}
}
