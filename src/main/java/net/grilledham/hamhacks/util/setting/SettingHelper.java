package net.grilledham.hamhacks.util.setting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.grilledham.hamhacks.modules.Keybind;
import net.grilledham.hamhacks.util.Color;
import net.grilledham.hamhacks.util.SelectableList;
import net.grilledham.hamhacks.util.StringList;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class SettingHelper {
	
	public static List<Field> getSettings(Object o) {
		return Arrays.stream(o.getClass().getFields()).filter(field ->
				field.isAnnotationPresent(BoolSetting.class)
				|| field.isAnnotationPresent(ColorSetting.class)
				|| field.isAnnotationPresent(NumberSetting.class)
				|| field.isAnnotationPresent(KeySetting.class)
				|| field.isAnnotationPresent(ListSetting.class)
				|| field.isAnnotationPresent(SelectionSetting.class)
				|| field.isAnnotationPresent(StringSetting.class)).toList();
	}
	
	public static List<Field> getBoolSettings(Object o) {
		return Arrays.stream(o.getClass().getFields()).filter(field -> field.isAnnotationPresent(BoolSetting.class)).toList();
	}
	
	public static List<Field> getColorSettings(Object o) {
		return Arrays.stream(o.getClass().getFields()).filter(field -> field.isAnnotationPresent(ColorSetting.class)).toList();
	}
	
	public static List<Field> getNumberSettings(Object o) {
		return Arrays.stream(o.getClass().getFields()).filter(field -> field.isAnnotationPresent(NumberSetting.class)).toList();
	}
	
	public static List<Field> getKeySettings(Object o) {
		return Arrays.stream(o.getClass().getFields()).filter(field -> field.isAnnotationPresent(KeySetting.class)).toList();
	}
	
	public static List<Field> getListSettings(Object o) {
		return Arrays.stream(o.getClass().getFields()).filter(field -> field.isAnnotationPresent(ListSetting.class)).toList();
	}
	
	public static List<Field> getSelectionSettings(Object o) {
		return Arrays.stream(o.getClass().getFields()).filter(field -> field.isAnnotationPresent(SelectionSetting.class)).toList();
	}
	
	public static List<Field> getStringSettings(Object o) {
		return Arrays.stream(o.getClass().getFields()).filter(field -> field.isAnnotationPresent(StringSetting.class)).toList();
	}
	
	public static void addSaveData(Object o, JsonObject saveData) {
		List<Field> boolSettings = getBoolSettings(o);
		List<Field> colorSettings = getColorSettings(o);
		List<Field> numberSettings = getNumberSettings(o);
		List<Field> keySettings = getKeySettings(o);
		List<Field> listSettings = getListSettings(o);
		List<Field> selectionSettings = getSelectionSettings(o);
		List<Field> stringSettings = getStringSettings(o);
		for(Field setting : boolSettings) {
			try {
				addBoolSaveData(setting, o, saveData);
			} catch(IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		for(Field setting : colorSettings) {
			try {
				addColorSaveData(setting, o, saveData);
			} catch(IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		for(Field setting : numberSettings) {
			try {
				addNumberSaveData(setting, o, saveData);
			} catch(IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		for(Field setting : keySettings) {
			try {
				addKeySaveData(setting, o, saveData);
			} catch(IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		for(Field setting : listSettings) {
			try {
				addListSaveData(setting, o, saveData);
			} catch(IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		for(Field setting : selectionSettings) {
			try {
				addSelectionSaveData(setting, o, saveData);
			} catch(IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		for(Field setting : stringSettings) {
			try {
				addStringSaveData(setting, o, saveData);
			} catch(IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public static void addBoolSaveData(Field f, Object o, JsonObject saveData) throws IllegalAccessException {
		String name = f.getAnnotation(BoolSetting.class).name();
		saveData.addProperty(name, f.getBoolean(o));
	}
	
	public static void addColorSaveData(Field f, Object o, JsonObject saveData) throws IllegalAccessException {
		String name = f.getAnnotation(ColorSetting.class).name();
		Color c = (Color)f.get(o);
		JsonObject cObj = new JsonObject();
		cObj.addProperty("hue", c.getTrueHue());
		cObj.addProperty("saturation", c.getSaturation());
		cObj.addProperty("brightness", c.getBrightness());
		cObj.addProperty("alpha", c.getAlpha());
		cObj.addProperty("chroma", c.getChroma());
		saveData.add(name, cObj);
	}
	
	public static void addNumberSaveData(Field f, Object o, JsonObject saveData) throws IllegalAccessException {
		String name = f.getAnnotation(NumberSetting.class).name();
		saveData.addProperty(name, f.getFloat(o));
	}
	
	public static void addKeySaveData(Field f, Object o, JsonObject saveData) throws IllegalAccessException {
		String name = f.getAnnotation(KeySetting.class).name();
		saveData.addProperty(name, ((Keybind)f.get(o)).getKey());
	}
	
	public static void addListSaveData(Field f, Object o, JsonObject saveData) throws IllegalAccessException {
		JsonArray arr = new JsonArray();
		for(String s : ((StringList)f.get(o)).getList()) {
			arr.add(s);
		}
		String name = f.getAnnotation(ListSetting.class).name();
		saveData.add(name, arr);
	}
	
	public static void addSelectionSaveData(Field f, Object o, JsonObject saveData) throws IllegalAccessException {
		String name = f.getAnnotation(SelectionSetting.class).name();
		saveData.addProperty(name, ((SelectableList)f.get(o)).get());
	}
	
	public static void addStringSaveData(Field f, Object o, JsonObject saveData) throws IllegalAccessException {
		String name = f.getAnnotation(StringSetting.class).name();
		saveData.addProperty(name, (String)f.get(o));
	}
	
	public static void parseSaveData(Object o, JsonObject saveData) {
		List<Field> boolSettings = getBoolSettings(o);
		List<Field> colorSettings = getColorSettings(o);
		List<Field> numberSettings = getNumberSettings(o);
		List<Field> keySettings = getKeySettings(o);
		List<Field> listSettings = getListSettings(o);
		List<Field> selectionSettings = getSelectionSettings(o);
		List<Field> stringSettings = getStringSettings(o);
		for(Field setting : boolSettings) {
			try {
				parseBoolSaveData(setting, o, saveData);
			} catch(IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		for(Field setting : colorSettings) {
			try {
				parseColorSaveData(setting, o, saveData);
			} catch(IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		for(Field setting : numberSettings) {
			try {
				parseNumberSaveData(setting, o, saveData);
			} catch(IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		for(Field setting : keySettings) {
			try {
				parseKeySaveData(setting, o, saveData);
			} catch(IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		for(Field setting : listSettings) {
			try {
				parseListSaveData(setting, o, saveData);
			} catch(IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		for(Field setting : selectionSettings) {
			try {
				parseSelectionSaveData(setting, o, saveData);
			} catch(IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		for(Field setting : stringSettings) {
			try {
				parseStringSaveData(setting, o, saveData);
			} catch(IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public static void parseBoolSaveData(Field f, Object o, JsonObject saveData) throws IllegalAccessException {
		String name = f.getAnnotation(BoolSetting.class).name();
		if(saveData.has(name)) {
			f.setBoolean(o, saveData.get(name).getAsBoolean());
		}
	}
	
	public static void parseColorSaveData(Field f, Object o, JsonObject saveData) throws IllegalAccessException {
		String name = f.getAnnotation(ColorSetting.class).name();
		if(saveData.has(name)) {
			JsonObject cObj = saveData.get(name).getAsJsonObject();
			float hue = cObj.get("hue").getAsFloat();
			float saturation = cObj.get("saturation").getAsFloat();
			float brightness = cObj.get("brightness").getAsFloat();
			float alpha = cObj.get("alpha").getAsFloat();
			boolean chroma = cObj.get("chroma").getAsBoolean();
			Color c = (Color)f.get(o);
			c.set(hue, saturation, brightness, alpha);
			c.setChroma(chroma);
		}
	}
	
	public static void parseNumberSaveData(Field f, Object o, JsonObject saveData) throws IllegalAccessException {
		String name = f.getAnnotation(NumberSetting.class).name();
		if(saveData.has(name)) {
			f.setFloat(o, saveData.get(name).getAsFloat());
		}
	}
	
	public static void parseKeySaveData(Field f, Object o, JsonObject saveData) throws IllegalAccessException {
		String name = f.getAnnotation(KeySetting.class).name();
		if(saveData.has(name)) {
			((Keybind)f.get(o)).setKey(saveData.get(name).getAsInt());
		}
	}
	
	public static void parseListSaveData(Field f, Object o, JsonObject saveData) throws IllegalAccessException {
		String name = f.getAnnotation(ListSetting.class).name();
		if(saveData.has(name)) {
			JsonArray arr = saveData.getAsJsonArray(name);
			StringList list = ((StringList)f.get(o));
			list.clear();
			for(JsonElement el : arr) {
				String s = el.getAsString();
				list.add(s);
			}
		}
	}
	
	public static void parseSelectionSaveData(Field f, Object o, JsonObject saveData) throws IllegalAccessException {
		String name = f.getAnnotation(SelectionSetting.class).name();
		((SelectableList)f.get(o)).set(saveData.get(name).getAsString());
	}
	
	public static void parseStringSaveData(Field f, Object o, JsonObject saveData) throws IllegalAccessException {
		String name = f.getAnnotation(StringSetting.class).name();
		f.set(o, saveData.get(name).getAsString());
	}
	
	public static void reset(Field f, Object o) throws IllegalAccessException {
		if(f.isAnnotationPresent(BoolSetting.class)) {
			f.setBoolean(o, f.getAnnotation(BoolSetting.class).defaultValue());
		} else if(f.isAnnotationPresent(ColorSetting.class)) {
			((Color)f.get(o)).reset();
		} else if(f.isAnnotationPresent(NumberSetting.class)) {
			f.setFloat(o, f.getAnnotation(NumberSetting.class).defaultValue());
		} else if(f.isAnnotationPresent(KeySetting.class)) {
			((Keybind)f.get(o)).resetKey();
		} else if(f.isAnnotationPresent(ListSetting.class)) {
			((StringList)f.get(o)).reset();
		} else if(f.isAnnotationPresent(SelectionSetting.class)) {
			((SelectableList)f.get(o)).reset();
		} else if(f.isAnnotationPresent(StringSetting.class)) {
			f.set(o, f.getAnnotation(StringSetting.class).defaultValue());
		}
	}
	
	public static Text getName(Field f) {
		if(f.isAnnotationPresent(BoolSetting.class)) {
			return getName(f.getAnnotation(BoolSetting.class));
		} else if(f.isAnnotationPresent(ColorSetting.class)) {
			return getName(f.getAnnotation(ColorSetting.class));
		} else if(f.isAnnotationPresent(NumberSetting.class)) {
			return getName(f.getAnnotation(NumberSetting.class));
		} else if(f.isAnnotationPresent(KeySetting.class)) {
			return getName(f.getAnnotation(KeySetting.class));
		} else if(f.isAnnotationPresent(ListSetting.class)) {
			return getName(f.getAnnotation(ListSetting.class));
		} else if(f.isAnnotationPresent(SelectionSetting.class)) {
			return getName(f.getAnnotation(SelectionSetting.class));
		} else if(f.isAnnotationPresent(StringSetting.class)) {
			return getName(f.getAnnotation(StringSetting.class));
		}
		return Text.empty();
	}
	
	public static boolean hasTooltip(Field f) {
		if(f.isAnnotationPresent(BoolSetting.class)) {
			return hasTooltip(f.getAnnotation(BoolSetting.class));
		} else if(f.isAnnotationPresent(ColorSetting.class)) {
			return hasTooltip(f.getAnnotation(ColorSetting.class));
		} else if(f.isAnnotationPresent(NumberSetting.class)) {
			return hasTooltip(f.getAnnotation(NumberSetting.class));
		} else if(f.isAnnotationPresent(KeySetting.class)) {
			return hasTooltip(f.getAnnotation(KeySetting.class));
		} else if(f.isAnnotationPresent(ListSetting.class)) {
			return hasTooltip(f.getAnnotation(ListSetting.class));
		} else if(f.isAnnotationPresent(SelectionSetting.class)) {
			return hasTooltip(f.getAnnotation(SelectionSetting.class));
		} else if(f.isAnnotationPresent(StringSetting.class)) {
			return hasTooltip(f.getAnnotation(StringSetting.class));
		}
		return false;
	}
	
	public static Text getTooltip(Field f) {
		if(f.isAnnotationPresent(BoolSetting.class)) {
			return getTooltip(f.getAnnotation(BoolSetting.class));
		} else if(f.isAnnotationPresent(ColorSetting.class)) {
			return getTooltip(f.getAnnotation(ColorSetting.class));
		} else if(f.isAnnotationPresent(NumberSetting.class)) {
			return getTooltip(f.getAnnotation(NumberSetting.class));
		} else if(f.isAnnotationPresent(KeySetting.class)) {
			return getTooltip(f.getAnnotation(KeySetting.class));
		} else if(f.isAnnotationPresent(ListSetting.class)) {
			return getTooltip(f.getAnnotation(ListSetting.class));
		} else if(f.isAnnotationPresent(SelectionSetting.class)) {
			return getTooltip(f.getAnnotation(SelectionSetting.class));
		} else if(f.isAnnotationPresent(StringSetting.class)) {
			return getTooltip(f.getAnnotation(StringSetting.class));
		}
		return Text.empty();
	}
	
	private static Text getName(BoolSetting setting) {
		return Text.translatable(setting.name());
	}
	
	private static boolean hasTooltip(BoolSetting setting) {
		return !getTooltip(setting).getString().equals(setting.name() + ".tooltip");
	}
	
	private static Text getTooltip(BoolSetting setting) {
		return Text.translatable(setting.name() + ".tooltip");
	}
	
	private static Text getName(ColorSetting setting) {
		return Text.translatable(setting.name());
	}
	
	private static boolean hasTooltip(ColorSetting setting) {
		return !getTooltip(setting).getString().equals(setting.name() + ".tooltip");
	}
	
	private static Text getTooltip(ColorSetting setting) {
		return Text.translatable(setting.name() + ".tooltip");
	}
	
	private static Text getName(NumberSetting setting) {
		return Text.translatable(setting.name());
	}
	
	private static boolean hasTooltip(NumberSetting setting) {
		return !getTooltip(setting).getString().equals(setting.name() + ".tooltip");
	}
	
	private static Text getTooltip(NumberSetting setting) {
		return Text.translatable(setting.name() + ".tooltip");
	}
	
	private static Text getName(KeySetting setting) {
		return Text.translatable(setting.name());
	}
	
	private static boolean hasTooltip(KeySetting setting) {
		return !getTooltip(setting).getString().equals(setting.name() + ".tooltip");
	}
	
	private static Text getTooltip(KeySetting setting) {
		return Text.translatable(setting.name() + ".tooltip");
	}
	
	private static Text getName(ListSetting setting) {
		return Text.translatable(setting.name());
	}
	
	private static boolean hasTooltip(ListSetting setting) {
		return !getTooltip(setting).getString().equals(setting.name() + ".tooltip");
	}
	
	private static Text getTooltip(ListSetting setting) {
		return Text.translatable(setting.name() + ".tooltip");
	}
	
	private static Text getName(SelectionSetting setting) {
		return Text.translatable(setting.name());
	}
	
	private static boolean hasTooltip(SelectionSetting setting) {
		return !getTooltip(setting).getString().equals(setting.name() + ".tooltip");
	}
	
	private static Text getTooltip(SelectionSetting setting) {
		return Text.translatable(setting.name() + ".tooltip");
	}
	
	private static Text getName(StringSetting setting) {
		return Text.translatable(setting.name());
	}
	
	private static boolean hasTooltip(StringSetting setting) {
		return !getTooltip(setting).getString().equals(setting.name() + ".tooltip");
	}
	
	private static Text getTooltip(StringSetting setting) {
		return Text.translatable(setting.name() + ".tooltip");
	}
	
	public static boolean shouldShow(Field f, Object o) {
		if(f.isAnnotationPresent(BoolSetting.class)) {
			return shouldShow(f.getAnnotation(BoolSetting.class), o);
		} else if(f.isAnnotationPresent(ColorSetting.class)) {
			return shouldShow(f.getAnnotation(ColorSetting.class), o);
		} else if(f.isAnnotationPresent(NumberSetting.class)) {
			return shouldShow(f.getAnnotation(NumberSetting.class), o);
		} else if(f.isAnnotationPresent(KeySetting.class)) {
			return shouldShow(f.getAnnotation(KeySetting.class), o);
		} else if(f.isAnnotationPresent(ListSetting.class)) {
			return shouldShow(f.getAnnotation(ListSetting.class), o);
		} else if(f.isAnnotationPresent(SelectionSetting.class)) {
			return shouldShow(f.getAnnotation(SelectionSetting.class), o);
		} else if(f.isAnnotationPresent(StringSetting.class)) {
			return shouldShow(f.getAnnotation(StringSetting.class), o);
		}
		return true;
	}
	
	private static boolean shouldShow(BoolSetting setting, Object o) {
		if(setting.neverShow()) {
			return false;
		}
		String[] dependsOn = setting.dependsOn();
		return shouldShow(o, dependsOn);
	}
	
	private static boolean shouldShow(ColorSetting setting, Object o) {
		if(setting.neverShow()) {
			return false;
		}
		String[] dependsOn = setting.dependsOn();
		return shouldShow(o, dependsOn);
	}
	
	private static boolean shouldShow(NumberSetting setting, Object o) {
		if(setting.neverShow()) {
			return false;
		}
		String[] dependsOn = setting.dependsOn();
		return shouldShow(o, dependsOn);
	}
	
	private static boolean shouldShow(KeySetting setting, Object o) {
		if(setting.neverShow()) {
			return false;
		}
		String[] dependsOn = setting.dependsOn();
		return shouldShow(o, dependsOn);
	}
	
	private static boolean shouldShow(ListSetting setting, Object o) {
		if(setting.neverShow()) {
			return false;
		}
		String[] dependsOn = setting.dependsOn();
		return shouldShow(o, dependsOn);
	}
	
	private static boolean shouldShow(SelectionSetting setting, Object o) {
		if(setting.neverShow()) {
			return false;
		}
		String[] dependsOn = setting.dependsOn();
		return shouldShow(o, dependsOn);
	}
	
	private static boolean shouldShow(StringSetting setting, Object o) {
		if(setting.neverShow()) {
			return false;
		}
		String[] dependsOn = setting.dependsOn();
		return shouldShow(o, dependsOn);
	}
	
	private static boolean shouldShow(Object o, String[] dependsOn) {
		boolean shouldShow = true;
		for(String s : dependsOn) {
			String condition = s.replaceAll("!", "");
			String[] args = condition.split("->");
			boolean dependencyMet;
			try {
				if(args.length == 1) {
					dependencyMet = o.getClass().getField(args[0]).getBoolean(o);
				} else if(args.length == 2) {
					dependencyMet = ((SelectableList)o.getClass().getField(args[0]).get(o)).get().equals(args[1]);
				} else {
					throw new IllegalStateException("args.length must be 1 or 2 but was " + args.length);
				}
			} catch(IllegalAccessException | NoSuchFieldException e) {
				throw new RuntimeException(e);
			}
			if(s.startsWith("!")) {
				if(dependencyMet) {
					shouldShow = false;
				}
			} else {
				if(!dependencyMet) {
					shouldShow = false;
				}
			}
		}
		return shouldShow;
	}
}