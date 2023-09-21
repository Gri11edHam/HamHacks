package net.grilledham.hamhacks.font;

public record FontInfo(String family, Type type) {
	
	public enum Type {
		Regular,
		Bold,
		Italic,
		BoldItalic;
		
		public static Type fromString(String type) {
			return switch(type) {
				case "Bold" -> Bold;
				case "Italic" -> Italic;
				case "Bold Italic", "BoldItalic" -> BoldItalic;
				default -> Regular;
			};
		}
	}
}
