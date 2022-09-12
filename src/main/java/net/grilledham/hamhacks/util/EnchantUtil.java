package net.grilledham.hamhacks.util;

import net.minecraft.enchantment.Enchantment;

public class EnchantUtil {
	
	public static String getShortName(Enchantment enchantment) {
		return switch(enchantment.getTranslationKey()) {
			case "enchantment.minecraft.sharpness" -> "Sharp";
			case "enchantment.minecraft.smite" -> "Smite";
			case "enchantment.minecraft.bane_of_arthropods" -> "Bane";
			case "enchantment.minecraft.knockback" -> "KB";
			case "enchantment.minecraft.fire_aspect" -> "Fire";
			case "enchantment.minecraft.sweeping" -> "Sweep";
			case "enchantment.minecraft.protection" -> "Prot";
			case "enchantment.minecraft.fire_protection" -> "FProt";
			case "enchantment.minecraft.feather_falling" -> "FFall";
			case "enchantment.minecraft.blast_protection" -> "BProt";
			case "enchantment.minecraft.projectile_protection" -> "PProt";
			case "enchantment.minecraft.respiration" -> "Resp";
			case "enchantment.minecraft.aqua_affinity" -> "Aqua";
			case "enchantment.minecraft.swift_sneak" -> "Sneak";
			case "enchantment.minecraft.depth_strider" -> "Depth";
			case "enchantment.minecraft.frost_walker" -> "Frost";
			case "enchantment.minecraft.soul_speed" -> "Soul";
			case "enchantment.minecraft.efficiency" -> "Eff";
			case "enchantment.minecraft.silk_touch" -> "Silk";
			case "enchantment.minecraft.unbreaking" -> "Unb";
			case "enchantment.minecraft.looting" -> "Loot";
			case "enchantment.minecraft.fortune" -> "Fort";
			case "enchantment.minecraft.luck_of_the_sea" -> "Luck";
			case "enchantment.minecraft.lure" -> "Lure";
			case "enchantment.minecraft.power" -> "Pow";
			case "enchantment.minecraft.flame" -> "Flame";
			case "enchantment.minecraft.punch" -> "Punch";
			case "enchantment.minecraft.infinity" -> "Inf";
			case "enchantment.minecraft.thorns" -> "Thorns";
			case "enchantment.minecraft.mending" -> "Men";
			case "enchantment.minecraft.binding_curse" -> "Bin";
			case "enchantment.minecraft.vanishing_curse" -> "Van";
			case "enchantment.minecraft.loyalty" -> "Loy";
			case "enchantment.minecraft.impaling" -> "Imp";
			case "enchantment.minecraft.riptide" -> "Rip";
			case "enchantment.minecraft.channeling" -> "Chan";
			case "enchantment.minecraft.multishot" -> "Multi";
			case "enchantment.minecraft.quick_charge" -> "Quick";
			case "enchantment.minecraft.piercing" -> "Pierce";
			default -> enchantment.getTranslationKey();
		};
	}
}
