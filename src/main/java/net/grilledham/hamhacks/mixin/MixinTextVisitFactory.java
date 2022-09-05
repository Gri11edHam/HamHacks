package net.grilledham.hamhacks.mixin;

import net.grilledham.hamhacks.modules.misc.NameHider;
import net.minecraft.client.font.TextVisitFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TextVisitFactory.class)
public class MixinTextVisitFactory {
	
	@ModifyArg(method = "visitFormatted(Ljava/lang/String;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextVisitFactory;visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z"))
	private static String modifyName(String text) {
		return NameHider.getInstance().modifyName(text);
	}
}
