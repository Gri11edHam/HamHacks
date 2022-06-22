package net.grilledham.hamhacks.util;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class FileHelper {

	public static InputStream getStreamFromURL(String url) throws IOException {
		URLConnection connection = new URL(url).openConnection();
		connection.setRequestProperty("User-Agent", "Mozilla/5.0");
		connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		connection.setDoOutput(true);
		return connection.getInputStream();
	}
	
	public static void deleteDirectory(File dir) {
		if(dir.exists()) {
			for(File file : dir.listFiles()) {
				if(file.isDirectory()) {
					deleteDirectory(file);
				} else {
					file.delete();
				}
			}
		}
	}
	
	public static String readFile(File file) throws IOException {
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		StringBuilder sb = new StringBuilder();
		
		String current;
		while((current = br.readLine()) != null && !current.startsWith("#")) {
			sb.append(current);
		}
		
		br.close();
		fr.close();
		
		return sb.toString();
	}
	
	public static void writeFile(String text, File file) throws IOException {
		file.mkdirs();
		if(file.exists()) {
			file.delete();
		}
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		writer.println(text);
		writer.close();
	}
	
	public static void writeFile(InputStream input, File file) throws IOException {
		file.mkdirs();
		if(file.exists()) {
			file.delete();
		}
		FileOutputStream fos = new FileOutputStream(file);
		byte[] buffer = new byte[8192];
		int bytesRead;
		while((bytesRead = input.read(buffer)) != -1) {
			fos.write(buffer, 0, bytesRead);
		}
		fos.close();
	}
}
