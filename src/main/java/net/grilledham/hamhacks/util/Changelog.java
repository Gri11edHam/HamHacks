package net.grilledham.hamhacks.util;

public class Changelog {
	
	private static final String CHANGELOG = """
			Update 1.5:
				- Added translations to module settings
				- Added config versions
				- Added more customizations to the Click GUI
				- Added scrolling to the Click GUI
				- Added commands
				- Added module:
					: Instant Kill Bow
				- Added settings to Speed module:
					: In Air Multiplier
					: On Ice Multiplier
					: In Tunnel Multiplier
					: In Water Multiplier
				- Added settings to No Fall module:
					: Mode
				- Added option to "Mode" in Fly module:
					: Jetpack
				- Added descriptions and tooltips
				- Bug fixes
			
			Update 1.4.2:
				- Added the ability for modules to force-disable other modules
				- Removed unnecessary settings in Fly
				- Added settings to Fly module:
					: Smooth Movement
				- Split "Range" into "Block Range" (vanilla: 4.5) and "Entity Range" (vanilla: 3.0) in Reach module
				- Added options to show ping, tps, and last server response time in the HUD
				- Added Full Changelog screen
				- Fixed translations not being used and updated translations
				- Bug fixes
			
			Update 1.4.1:
				- Updated to 1.18.2
				- Improved vanilla fly bypass
				- Improved server-side rotations
				- Added module:
					: Reach
				- Bug fixes
				
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
