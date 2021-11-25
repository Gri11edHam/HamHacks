package net.grilledham.hamhacks.mixininterface;

import net.minecraft.client.util.InputUtil;

public interface IKeyBinding {
	public InputUtil.Key getBound();
}
