package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.modules.Module;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LanguageManager.class)
public abstract class MixinLanguageManager implements SynchronousResourceReloader {
	
	@Inject(method = "setLanguage", at = @At("TAIL"))
	public void setLanguage(LanguageDefinition language, CallbackInfo ci) {
		Module.Category.updateLanguage();
	}
	
	@Inject(method = "reload", at = @At("TAIL"))
	public void reloadLanguage(ResourceManager manager, CallbackInfo ci) {
		Module.Category.updateLanguage();
	}
}
