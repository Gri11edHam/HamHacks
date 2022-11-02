package net.grilledham.hamhacks.page;

import net.grilledham.hamhacks.event.EventManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

public class Page {

	protected Text name;
	
	protected Text toolTip;
	
	protected MinecraftClient mc = MinecraftClient.getInstance();
	
	public Page(Text name) {
		this.name = name;
		this.toolTip = Text.translatable(getConfigName() + ".tooltip");
		EventManager.register(this);
	}
	
	public String getName() {
		return this.name.getString();
	}
	
	public String getToolTip() {
		return this.toolTip.getString();
	}
	
	public boolean hasToolTip() {
		return !getToolTip().equals(getConfigName() + ".tooltip");
	}
	
	public String getConfigName() {
		return ((TranslatableTextContent)name.getContent()).getKey();
	}
	
	@Override
	public String toString() {
		return "Page{" +
				"name=" + getName() +
				", config_name=" + getConfigName() +
				'}';
	}
}
