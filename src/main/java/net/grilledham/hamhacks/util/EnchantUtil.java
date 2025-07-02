package net.grilledham.hamhacks.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;

public class EnchantUtil {
	
	public static String getShortName(RegistryEntry<Enchantment> enchantment) {
		if(enchantment.getKey().isPresent()) {
			return switch(enchantment.getKey().get().getValue().toTranslationKey()) {
				case "minecraft.sharpness" -> "Sharp";
				case "minecraft.smite" -> "Smite";
				case "minecraft.bane_of_arthropods" -> "Bane";
				case "minecraft.knockback" -> "KB";
				case "minecraft.fire_aspect" -> "Fire";
				case "minecraft.sweeping_edge" -> "Sweep";
				case "minecraft.protection" -> "Prot";
				case "minecraft.fire_protection" -> "FProt";
				case "minecraft.feather_falling" -> "FFall";
				case "minecraft.blast_protection" -> "BProt";
				case "minecraft.projectile_protection" -> "PProt";
				case "minecraft.respiration" -> "Resp";
				case "minecraft.aqua_affinity" -> "Aqua";
				case "minecraft.swift_sneak" -> "Sneak";
				case "minecraft.depth_strider" -> "Depth";
				case "minecraft.frost_walker" -> "Frost";
				case "minecraft.soul_speed" -> "Soul";
				case "minecraft.efficiency" -> "Eff";
				case "minecraft.silk_touch" -> "Silk";
				case "minecraft.unbreaking" -> "Unb";
				case "minecraft.looting" -> "Loot";
				case "minecraft.fortune" -> "Fort";
				case "minecraft.luck_of_the_sea" -> "Luck";
				case "minecraft.lure" -> "Lure";
				case "minecraft.power" -> "Pow";
				case "minecraft.flame" -> "Flame";
				case "minecraft.punch" -> "Punch";
				case "minecraft.infinity" -> "Inf";
				case "minecraft.thorns" -> "Thorns";
				case "minecraft.mending" -> "Men";
				case "minecraft.binding_curse" -> "Bin";
				case "minecraft.vanishing_curse" -> "Van";
				case "minecraft.loyalty" -> "Loy";
				case "minecraft.impaling" -> "Imp";
				case "minecraft.riptide" -> "Rip";
				case "minecraft.channeling" -> "Chan";
				case "minecraft.multishot" -> "Multi";
				case "minecraft.quick_charge" -> "Quick";
				case "minecraft.piercing" -> "Pierce";
				default -> enchantment.value().description().getString();
			};
		} else {
			return enchantment.value().description().getString();
		}
	}
}
