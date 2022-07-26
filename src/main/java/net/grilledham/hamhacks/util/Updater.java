package net.grilledham.hamhacks.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.grilledham.hamhacks.HamHacksClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

public class Updater {
	
	private Updater(){}
	
	private static Version latestVersion;
	private static String changelog = "";
	private static String downloadURL;
	
	private static boolean hasUpdated = false;
	
	public static void init() {
		Gson json = HamHacksClient.GSON;
		try {
			InputStream is;
			InputStreamReader isr;
			JsonObject obj = json.fromJson(isr = new InputStreamReader(is = FileHelper.getStreamFromURL("https://api.github.com/repos/Gri11edHam/HamHacks/releases/latest")), JsonObject.class);
			isr.close();
			is.close();
			latestVersion = new Version(obj.get("tag_name").getAsString().replace("v", ""));
			changelog = obj.get("body").getAsString();
			downloadURL = obj.get("assets").getAsJsonArray().get(0).getAsJsonObject().get("browser_download_url").getAsString();
			if(Updater.newVersionAvailable()) {
				HamHacksClient.LOGGER.info("New version available! (" + Updater.getLatest().getVersion(0, true) + ")");
			} else {
				HamHacksClient.LOGGER.info("Up to date! (" + latestVersion.getVersion(0, true) + ")");
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static boolean newVersionAvailable() {
		if(latestVersion == null) {
			latestVersion = new Version("0");
		}
		return latestVersion.isNewerThan(HamHacksClient.VERSION) && !hasUpdated;
	}
	
	public static Version getLatest() {
		if(latestVersion == null) {
			latestVersion = new Version("0");
		}
		return latestVersion;
	}
	
	public static String getChangelog() {
		return changelog;
	}
	
	public static void update() {
		try {
			File newVersion = new File(MinecraftClient.getInstance().runDirectory, "/mods/hamhacks-" + latestVersion.getVersion(0, true) + ".jar");
			int i = 1;
			while(newVersion.exists()) {
				newVersion = new File(MinecraftClient.getInstance().runDirectory, "/mods/hamhacks-" + latestVersion.getVersion(0, true) + " (" + i + ").jar");
				i++;
			}
			InputStream is;
			FileHelper.writeFile(is = FileHelper.getStreamFromURL(downloadURL), newVersion);
			is.close();
			hasUpdated = true;
			try {
				File modFile = new File(HamHacksClient.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
				if(Util.getOperatingSystem() != Util.OperatingSystem.WINDOWS) {
					// For mac and linux
					HamHacksClient.LOGGER.info("Deleting old mod file via File.deleteOnExit()");
					modFile.deleteOnExit(); // haven't tested but it should work
				} else {
					Runtime.getRuntime().addShutdownHook(new Thread(() -> {
						// For windows (why does it have to be so complicated microsoft)
						HamHacksClient.LOGGER.info("Deleting old mod file via deleter.bat");
						File deleter = new File(FabricLoader.getInstance().getGameDir().toFile(), HamHacksClient.MOD_ID + "/deleter.bat");
						
						String deleterProgram = "" +
								"@echo off\n" +
								":TestFile\n" +
								"REN \"" + modFile.getAbsolutePath() + "\" \"" + modFile.getName() + "\"\n" +
								"IF not ERRORLEVEL 1 GOTO Continue\n" +
								"GOTO TestFile\n" +
								":Continue\n" +
								"ECHO Deleting \"" + modFile.getAbsolutePath() + "\"\n" +
								"DEL /F \"" + modFile.getAbsolutePath() + "\"\n" +
								"ECHO Finished deleting\n" +
//								"DEL /F \"deleter.bat\"\n";
								"EXIT\n";
						try {
							FileHelper.writeFile(deleterProgram, deleter);
						} catch(IOException e) {
							throw new RuntimeException(e);
						}
						
						try {
							Runtime.getRuntime().exec("cmd /c start deleter.bat", null, deleter.getParentFile());
						} catch(IOException e) {
							throw new RuntimeException(e);
						}
					}));
				}
			} catch(URISyntaxException e) {
				throw new RuntimeException(e);
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
}
