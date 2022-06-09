package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.gui.parts.impl.ButtonPart;
import net.grilledham.hamhacks.gui.screens.ChangelogScreen;
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
	
	private final ButtonPart changelogButton = new ButtonPart("Changelog", 2, 2, 60, 20, () -> {
		TitleScreen $this = (TitleScreen)(Object)this;
		MinecraftClient.getInstance().setScreen(new ChangelogScreen($this));
	});
	
	private MixinTitleScreen() {
		super(Text.translatable(""));
	}
	
	@Inject(method = "render", at = @At("TAIL"))
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		changelogButton.draw(matrices, mouseX, mouseY, delta);
	}
	
	@Inject(method = "mouseClicked", at = @At("TAIL"))
	public void clicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
		if(changelogButton.click(mouseX, mouseY, button)) {
			cir.setReturnValue(true);
		}
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if(changelogButton.release(mouseX, mouseY, button)) {
			return true;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}
}
