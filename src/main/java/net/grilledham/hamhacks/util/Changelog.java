package net.grilledham.hamhacks.util;

public class Changelog {
	
	private static final String CHANGELOG = """
			Update 1.4:
				- Overhauled event system
				- Overhauled GUI
				- Overhauled settings
				- Added changelog button to title screen
				- Bug fixes
			
			Update 1.3.3:
				- Fixed incompatibility with iris shaders
			
			Update 1.3.2:
				- Updated to 1.18.1
				- Added settings to HUD module:
					: Held Item Scale
					: Shield Height
					: Fire Height
					: Model Bobbing Only
					: No Hurt Cam
				- Bug fixes
			
			Update 1.3.1:
				- Fixed incompatibility with multiconnect
				- Fixed color settings not saving chroma
			
			Update 1.3:
				- Updated to 1.18
			
			Update 1.2:
				- Bug fixes
			
			Update 1.1.3:
				- Bug fixes
			
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
