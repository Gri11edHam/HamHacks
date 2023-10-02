package net.grilledham.hamhacks.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.grilledham.hamhacks.HamHacksClient;
import net.grilledham.hamhacks.util.HamHacksResourcePack;
import net.minecraft.client.resource.DefaultClientResourcePackProvider;
import net.minecraft.resource.*;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(VanillaResourcePackProvider.class)
public class MixinVanillaResourcePackProvider {
	
	@Inject(method = "register", at = @At("RETURN"))
	private void addResourcePack(Consumer<ResourcePackProfile> profileAdder, CallbackInfo ci) {
		if((Object)this instanceof DefaultClientResourcePackProvider && !FabricLoader.getInstance().isModLoaded("fabric-resource-loader-v0")) {
			profileAdder.accept(ResourcePackProfile.create(HamHacksClient.MOD_ID, Text.literal("HamHacks"), true, new ResourcePackProfile.PackFactory() {
				@Override
				public ResourcePack open(String name) {
					return new HamHacksResourcePack();
				}
				
				@Override
				public ResourcePack openWithOverlays(String name, ResourcePackProfile.Metadata metadata) {
					return new HamHacksResourcePack();
				}
			}, ResourceType.CLIENT_RESOURCES, ResourcePackProfile.InsertionPosition.TOP, ResourcePackSource.BUILTIN));
		}
	}
}
