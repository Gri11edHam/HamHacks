package net.grilledham.hamhacks.font;

import net.grilledham.hamhacks.HamHacksClient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;

public abstract class FontFace {

	public final FontInfo info;
	
	private FontFace(FontInfo info) {
		this.info = info;
	}
	
	public abstract InputStream toStream();
	
	public static FontFace custom(FontInfo info, String path) {
		return new FontFace(info) {
			@Override
			public InputStream toStream() {
				return HamHacksClient.class.getResourceAsStream("/assets/" + HamHacksClient.MOD_ID + "/fonts/" + path + ".ttf");
			}
		};
	}
	
	public static FontFace system(FontInfo info, Path path) {
		return new FontFace(info) {
			@Override
			public InputStream toStream() {
				if(!path.toFile().exists()) {
					throw new RuntimeException("File does not exist: " + path.toString());
				}
				
				try {
					return new FileInputStream(path.toFile());
				} catch(FileNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
}
