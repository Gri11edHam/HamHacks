package net.grilledham.hamhacks.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.grilledham.hamhacks.client.HamHacksClient;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Updater {
	
	private Updater(){}
	
	private static Version latestVersion;
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
	
	public static void update() {
		try {
			File newVersion = new File(MinecraftClient.getInstance().runDirectory, "/mods/hamhacks-" + latestVersion.getVersion(0, true) + ".jar");
			InputStream is;
			FileHelper.writeFile(is = FileHelper.getStreamFromURL(downloadURL), newVersion);
			is.close();
			hasUpdated = true;
			// Manually delete the old version for now
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
}
