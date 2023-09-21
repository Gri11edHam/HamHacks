package net.grilledham.hamhacks.font;

import net.grilledham.hamhacks.HamHacksClient;
import net.grilledham.hamhacks.page.PageManager;
import net.grilledham.hamhacks.page.pages.ClickGUI;
import net.minecraft.util.Util;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FontManager {
	
	public static final String[] CUSTOM_FONTS = {
			"UbuntuMono-Regular", "UbuntuMono-Bold", "UbuntuMono-Italic", "UbuntuMono-BoldItalic",
			"JetBrainsMono-Regular", "JetBrainsMono-Bold", "JetBrainsMono-Italic", "JetBrainsMono-BoldItalic"
	};
	
	public static final List<FontFamily> FONT_FAMILIES = new ArrayList<>();
	
	public static void init() {
		loadCustomFonts();
		loadSystemFonts();
		String[] fontNames = new String[FONT_FAMILIES.size() + 1];
		fontNames[0] = "Vanilla";
		for(int i = 0; i < FONT_FAMILIES.size(); i++) {
			fontNames[i + 1] = FONT_FAMILIES.get(i).getName();
		}
		PageManager.getPage(ClickGUI.class).font.setOptions(fontNames);
	}
	
	private static void loadCustomFonts() {
		for(String customFont : CUSTOM_FONTS) {
			loadCustom(customFont);
		}
	}
	
	private static void loadCustom(String path) {
		FontInfo info;
		try {
			info = getFontInfo(HamHacksClient.class.getResourceAsStream("/assets/" + HamHacksClient.MOD_ID + "/fonts/" + path + ".ttf"));
		} catch(IOException e) {
			HamHacksClient.LOGGER.error("Could not load custom font: {}", path, e);
			return;
		}
		if(info == null) return;
		
		FontFace fontFace = FontFace.custom(info, path);
		if(!addFont(fontFace)) {
			HamHacksClient.LOGGER.warn("Could not load font: {}", fontFace);
		}
	}
	
	private static void loadSystemFonts() {
		final List<File> fontDirs = new ArrayList<>();
		switch(Util.getOperatingSystem()) {
			case WINDOWS -> {
				fontDirs.add(new File(System.getenv("SystemRoot") + "\\Fonts"));
				fontDirs.add(new File(System.getProperty("user.home") + "\\AppData\\Local\\Microsoft\\Windows\\Fonts"));
			}
			case OSX -> {
				fontDirs.add(new File("/System/Library/Fonts/"));
				fontDirs.add(new File(System.getProperty("user.home") + "/Library/Fonts/"));
			}
			default -> {
				fontDirs.add(new File("/usr/share/fonts"));
				fontDirs.add(new File(System.getProperty("user.home") + "/.local/share/fonts"));
				fontDirs.add(new File(System.getProperty("user.home") + "/.fonts"));
			}
		}
		for(File dir : fontDirs) {
			loadSystem(dir);
		}
	}
	
	private static void loadSystem(File dir) {
		if(!dir.exists() || !dir.isDirectory()) return;
		
		for(File file : dir.listFiles()) {
			if(file.isDirectory()) {
				loadSystem(file);
				continue;
			}
			
			if(!(file.isFile() && file.getName().endsWith(".ttf"))) continue;
			
			FontInfo info;
			try {
				info = getFontInfo(new FileInputStream(file));
			} catch(IOException e) {
				HamHacksClient.LOGGER.error("Loading font file: " + dir.getAbsolutePath(), e);
				return;
			}
			
			if(info == null) return;
			
			FontFace fontFace = FontFace.system(info, file.toPath());
			if(!addFont(fontFace)) {
				HamHacksClient.LOGGER.warn("Could not load font: {}", fontFace);
			}
		}
	}
	
	private static boolean addFont(FontFace fontFace) {
		FontInfo info = fontFace.info;
		FontFamily family = getFamily(info.family());
		if(family == null) {
			family = new FontFamily(info.family());
			FONT_FAMILIES.add(family);
		}
		
		if(family.hasType(info.type())) return false;
		
		return family.addFont(fontFace);
	}
	
	private static FontFamily getFamily(String name) {
		for(FontFamily family : FONT_FAMILIES) {
			if(family.getName().equalsIgnoreCase(name)) return family;
		}
		return null;
	}
	
	private static FontInfo getFontInfo(InputStream stream) throws IOException {
		if(stream == null) return null;
		
		byte[] bytes = stream.readAllBytes();
		if(bytes.length < 5) return null;
		
		if(
			bytes[0] != 0
			|| bytes[1] != 1
			|| bytes[2] != 0
			|| bytes[3] != 0
			|| bytes[4] != 0
		) return null;
		
		ByteBuffer buf = BufferUtils.createByteBuffer(bytes.length).put(bytes).flip();
		STBTTFontinfo info = STBTTFontinfo.create();
		if(!STBTruetype.stbtt_InitFont(info, buf)) return null;
		
		ByteBuffer nameBuf = STBTruetype.stbtt_GetFontNameString(info, STBTruetype.STBTT_PLATFORM_ID_MICROSOFT, STBTruetype.STBTT_MS_EID_UNICODE_BMP, STBTruetype.STBTT_MS_LANG_ENGLISH, 1);
		ByteBuffer typeBuf = STBTruetype.stbtt_GetFontNameString(info, STBTruetype.STBTT_PLATFORM_ID_MICROSOFT, STBTruetype.STBTT_MS_EID_UNICODE_BMP, STBTruetype.STBTT_MS_LANG_ENGLISH, 2);
		if(nameBuf == null || typeBuf == null) return null;
		
		return new FontInfo(StandardCharsets.UTF_16.decode(nameBuf).toString(), FontInfo.Type.fromString(StandardCharsets.UTF_16.decode(typeBuf).toString()));
	}
}
