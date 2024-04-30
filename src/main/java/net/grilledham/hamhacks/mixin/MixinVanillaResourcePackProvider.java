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

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(VanillaResourcePackProvider.class)
public class MixinVanillaResourcePackProvider {
	
	@Inject(method = "register", at = @At("RETURN"))
	private void addResourcePack(Consumer<ResourcePackProfile> profileAdder, CallbackInfo ci) {
		if((Object)this instanceof DefaultClientResourcePackProvider && !FabricLoader.getInstance().isModLoaded("fabric-resource-loader-v0")) {
			profileAdder.accept(ResourcePackProfile.create(new ResourcePackInfo(HamHacksClient.MOD_ID, Text.literal("HamHacks"), ResourcePackSource.BUILTIN, Optional.empty()), new ResourcePackProfile.PackFactory() {
				@Override
				public ResourcePack open(ResourcePackInfo info) {
					return new HamHacksResourcePack();
				}
				
				@Override
				public ResourcePack openWithOverlays(ResourcePackInfo info, ResourcePackProfile.Metadata metadata) {
					return new HamHacksResourcePack();
				}
			}, ResourceType.CLIENT_RESOURCES, new ResourcePackPosition(true, ResourcePackProfile.InsertionPosition.TOP, false)));
		}
	}
}
