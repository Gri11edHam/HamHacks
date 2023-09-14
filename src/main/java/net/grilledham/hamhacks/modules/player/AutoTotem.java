package net.grilledham.hamhacks.modules.player;

import net.grilledham.hamhacks.event.EventListener;
import net.grilledham.hamhacks.event.events.EventTick;
import net.grilledham.hamhacks.modules.Category;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.modules.Module;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public class AutoTotem extends Module {
	
	private int totemsLeft = 0;
	
	public AutoTotem() {
		super(Text.translatable("hamhacks.module.autoTotem"), Category.PLAYER, new Keybind(0));
	}
	
	@Override
	public String getHUDText() {
		return super.getHUDText() + " \u00a77" + totemsLeft;
	}
	
	@EventListener
	public void onTick(EventTick e) {
		if(mc.world == null || mc.player == null) {
			totemsLeft = 0;
			return;
		}
		
		totemsLeft = mc.player.getInventory().count(Items.TOTEM_OF_UNDYING);
		
		ItemStack offhand = mc.player.getOffHandStack();
		if(offhand.getItem() != Items.TOTEM_OF_UNDYING) {
			int totemIndex = -1;
			for(int i = 0; i < 36; i++) {
				if(mc.player.getInventory().getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
					totemIndex = i;
					break;
				}
			}
			if(totemIndex == -1) {
				return;
			}
			PlayerScreenHandler screenHandler = new PlayerScreenHandler(mc.player.getInventory(), false, mc.player);
			int i1 = screenHandler.getSlotIndex(mc.player.getInventory(), totemIndex).getAsInt();
			int i2 = screenHandler.getSlotIndex(mc.player.getInventory(), PlayerInventory.OFF_HAND_SLOT).getAsInt();
			mc.interactionManager.clickSlot(screenHandler.syncId, i1, 0, SlotActionType.PICKUP, mc.player);
			mc.interactionManager.clickSlot(screenHandler.syncId, i2, 0, SlotActionType.PICKUP, mc.player);
		}
	}
}
