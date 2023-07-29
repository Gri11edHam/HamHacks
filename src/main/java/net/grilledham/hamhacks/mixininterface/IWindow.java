package net.grilledham.hamhacks.mixininterface;

import net.grilledham.hamhacks.modules.misc.TitleBar;

import java.io.IOException;

public interface IWindow {
	
	void hamhacks$updateVideoMode();
	void hamhacks$setIcon(TitleBar.IconProvider provider) throws IOException;
}
