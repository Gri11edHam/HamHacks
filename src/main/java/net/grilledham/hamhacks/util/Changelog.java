package net.grilledham.hamhacks.util;

public class Changelog {
	
	private static final String CHANGELOG = """
			Update 1.1.2:
				- Added modules:
					: Sprint
					: Step
				- Updated modules:
					: Speed
					: Fly
				- Bug fixes
			
			Update 1.1.1:
				- Added modules:
					: Click GUI
					: HUD
				- Added a new way to change keybinds and settings (Click GUI)
			
			Update 1.1:
				- Switched handling of keybinds from Minecraft's built in keybinds
					: There is currently no way to change the keys besides editing the config
				- Added a config to save keybinds and settings
				- Added comments to frequently used methods
				- Updated dependencies
				- Renamed "Smooth Aim" to "Aimbot"
				- Bug fixes
			
			Update 1.0:
				- Added modules:
					: Fly
					: Trap
					: Encase
					: Crystal Aura
					: Scroll Clicker
					: No Fall
					: Smooth Aim
					: Boat Fly
					: Auto Elytra
					: Speed
					: Jesus""";
	
	/**
	 * Used to get the entire changelog
	 * @return The entire changelog
	 */
	public static String getChangelog() {
		return CHANGELOG;
	}
	
	/**
	 * Used to get the latest update from the changelog
	 * @return The latest update's changelog
	 */
	public static String getLatest() {
		return CHANGELOG.subSequence(0, CHANGELOG.indexOf("\n\n")).toString();
	}
}
