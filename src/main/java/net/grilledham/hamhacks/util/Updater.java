package net.grilledham.hamhacks.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.grilledham.hamhacks.HamHacksClient;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.Updates;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;

import java.io.*;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

public class Updater {
	
	private Updater(){}
	
	private static final Pattern ASSET_NAME_PATTERN = Pattern.compile("^hamhacks-[\\d.]+(-(beta|dev)\\.\\d+)?\\.jar$");
	
	private static Version latestVersion;
	private static String changelog = "";
	private static String downloadURL;
	
	private static boolean hasUpdated = false;
	
	private static boolean isDevUpdate = false;
	
	public static void init() {
		Gson json = HamHacksClient.GSON;
		try {
			checkArtifacts:
			{
				if(PageManager.getPage(Updates.class).branch.get() == 1) {
					InputStream is;
					InputStreamReader isr;
					BufferedReader br;
					JsonObject obj = json.fromJson(isr = new InputStreamReader(is = FileHelper.getStreamFromURL("https://api.github.com/repos/Gri11edHam/HamHacks/actions/artifacts")), JsonObject.class);
					isr.close();
					is.close();
					br = new BufferedReader(isr = new InputStreamReader(is = FileHelper.getStreamFromURL("https://raw.githubusercontent.com/Gri11edHam/HamHacks/master/gradle.properties")));
					String line = "";
					while(!line.startsWith("mod_version")) {
						line = br.readLine();
						if(line == null) break checkArtifacts;
					}
					String[] parts = line.split("=");
					latestVersion = new Version(parts[1]);
					br.close();
					isr.close();
					is.close();
					downloadURL = "https://github.com/Gri11edHam/HamHacks/actions/runs/" + obj.get("artifacts").getAsJsonArray().get(0).getAsJsonObject().get("workflow_run").getAsJsonObject().get("id").getAsLong();
					changelog = "\u00a7c\u00a7l### \u00a7r\u00a7c\u00a7l\u00a7nWARNING\u00a7r\u00a7c\u00a7l ###\u00a7r\nDevelopment updates may contain bugs.\nUse at your own risk.";
					isDevUpdate = true;
					return;
				}
			}
			InputStream is;
			InputStreamReader isr;
			JsonObject obj = json.fromJson(isr = new InputStreamReader(is = FileHelper.getStreamFromURL("https://api.github.com/repos/Gri11edHam/HamHacks/releases/latest")), JsonObject.class);
			isr.close();
			is.close();
			latestVersion = new Version(obj.get("tag_name").getAsString().replace("v", ""));
			changelog = obj.get("body").getAsString();
			JsonArray assets = obj.get("assets").getAsJsonArray();
			downloadURL = null;
			for(JsonElement e : assets) {
				JsonObject asset = e.getAsJsonObject();
				if(asset.get("name").getAsString().matches(ASSET_NAME_PATTERN.pattern())) {
					downloadURL = asset.get("browser_download_url").getAsString();
					break;
				}
			}
		} catch(IOException e) {
			HamHacksClient.LOGGER.error("Checking for updates", e);
		}
		if(Updater.newVersionAvailable()) {
			HamHacksClient.LOGGER.info("New version available! ({})", Updater.getLatest().getVersion(0, true));
		} else {
			HamHacksClient.LOGGER.info("Up to date! ({} >= {})", HamHacksClient.VERSION.getVersion(0, true), latestVersion.getVersion(0, true));
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
	
	public static String getDownloadURL() {
		return downloadURL;
	}
	
	public static boolean isDevUpdate() {
		return isDevUpdate;
	}
	
	public static void update() {
		if(downloadURL == null) {
			HamHacksClient.LOGGER.error("Error updating hamhacks", new IllegalStateException("downloadURL cannot be null"));
			return;
		}
		try {
			if(!isDevUpdate) {
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
			}
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
						
						String deleterProgram =
								"@echo off\n" +
								":TestFile\n" +
								"REN \"" + modFile.getAbsolutePath() + "\" \"" + modFile.getName() + "\" 2>nul\n" +
								"IF not ERRORLEVEL 1 GOTO Continue\n" +
								"GOTO TestFile\n" +
								":Continue\n" +
								"ECHO Deleting \"" + modFile.getAbsolutePath() + "\"\n" +
								"DEL /F \"" + modFile.getAbsolutePath() + "\"\n" +
								"ECHO Finished deleting\n" +
								"EXIT\n";
						try {
							FileHelper.writeFile(deleterProgram, deleter);
							Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "deleter.bat"}, null, deleter.getParentFile());
						} catch(IOException e) {
							HamHacksClient.LOGGER.error("Starting deleter.bat", e);
						}
					}));
				}
			} catch(URISyntaxException e) {
				HamHacksClient.LOGGER.error("Removing old version", e);
			}
		} catch(IOException e) {
			HamHacksClient.LOGGER.error("Writing new version", e);
		}
	}
}
