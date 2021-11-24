package net.grilledham.hamhacks.modules;

import com.google.common.collect.Lists;
import net.grilledham.hamhacks.util.ChromaUtil;

import java.util.ArrayList;
import java.util.List;

public class Setting {
	
	private final String name;
	
	private String warning;
	private int warningSeverity;
	
	private Object value;
	private Type type;
	
	private final List<String> list = new ArrayList<>();
	
	private float min;
	private float max;
	
	private boolean mutable;
	
	private boolean chroma;
	
	/**
	 * Used for String, Boolean, Color, and Keybind settings
	 * @param name The name of the setting
	 * @param value The value of the setting
	 */
	public Setting(String name, Object value) {
		this.name = name;
		this.value = value;
		this.type = (value instanceof String) ? Type.STRING : (value instanceof Boolean) ? Type.BOOLEAN : null;
		if(this.type == null) {
			if(value instanceof Integer) {
				this.type = Type.COLOR;
				this.min = 0x0;
				this.max = 0xffffffff;
			} else if(value instanceof Keybind) {
				this.type = Type.KEYBIND;
			} else if(value instanceof Setting) {
				this.value = Lists.newArrayList(value);
				this.type = Type.SETTING_LIST;
			} else {
				throw new IllegalArgumentException("Value cannot be instance of " + value.getClass().getSimpleName());
			}
		}
	}
	
	/**
	 * Used for Integers
	 * @param name The name of the setting
	 * @param value The value of the setting
	 * @param min The minimum value of the setting
	 * @param max The maximum value of the setting
	 */
	public Setting(String name, int value, int min, int max) {
		this.name = name;
		this.value = value;
		this.type = Type.INT;
		this.min = min;
		this.max = max;
	}
	
	/**
	 * Used for Floats
	 * @param name The name of the setting
	 * @param value The value of the setting
	 * @param min The minimum value of the setting
	 * @param max The maximum value of the setting
	 */
	public Setting(String name, float value, float min, float max) {
		this.name = name;
		this.value = value;
		this.type = Type.FLOAT;
		this.min = min;
		this.max = max;
	}
	
	/**
	 * Used for Lists
	 * @param name The name of the setting
	 * @param value The value of the setting
	 * @param list The list of possible values for the setting
	 */
	public Setting(String name, String value, List<String> list) {
		this.name = name;
		this.value = value;
		this.type = Type.LIST;
		this.list.addAll(list);
	}
	
	/**
	 * Used for Settings of Settings
	 * @param name The name of the setting
	 * @param settings The sub-settings to add
	 */
	public Setting(String name, Setting... settings) {
		this.name = name;
		this.value = Lists.newArrayList(settings);
		this.type = Type.SETTING_LIST;
		mutable = false;
	}
	
	/**
	 * Used to allow users to add or remove sub-settings
	 * @param mutable Should the user be able to add or remove sub-settings
	 */
	public void setMutable(boolean mutable) {
		this.mutable = mutable;
	}
	
	/**
	 * Allows you to warn the user about anything this setting might do
	 * @param warning What should the user know about this setting
	 * @param severity How severe this warning is (0-2)
	 */
	public void setWarning(String warning, int severity) {
		this.warning = warning;
		this.warningSeverity = severity;
	}
	
	public void setValue(Object value) {
		switch(type) {
			case COLOR:
				this.value = value;
				break;
			case INT:
				if(value instanceof Integer) {
					this.value = Math.max(Math.min((int)value, (int)max), (int)min);
				} else {
					throw new IllegalArgumentException("New value cannot be instance of " + value.getClass().getSimpleName());
				}
				break;
			case FLOAT:
				if(value instanceof Float) {
					this.value = Math.max(Math.min((float)value, max), min);
				} else {
					throw new IllegalArgumentException("New value cannot be instance of " + value.getClass().getSimpleName());
				}
				break;
			case LIST:
				if(value instanceof String) {
					if(list.contains(value)) {
						this.value = value;
					} else {
						throw new IllegalArgumentException("New value cannot be \"" + value + "\" since it is not in the list of possible values");
					}
				} else {
					throw new IllegalArgumentException("New value cannot be instance of " + value.getClass().getSimpleName());
				}
				break;
			case STRING:
				if(value instanceof String) {
					this.value = value;
				} else {
					throw new IllegalArgumentException("New value cannot be instance of " + value.getClass().getSimpleName());
				}
				break;
			case BOOLEAN:
				if(value instanceof Boolean) {
					this.value = value;
				} else {
					throw new IllegalArgumentException("New value cannot be instance of " + value.getClass().getSimpleName());
				}
				break;
			case KEYBIND:
				if(value instanceof Integer) {
					((Keybind)this.value).setKey((int)value);
				} else {
					throw new IllegalArgumentException("New value cannot be instance of " + value.getClass().getSimpleName());
				}
				break;
		}
		valueChanged();
	}
	
	public void setValue(String name, Object value) {
		Setting subSetting = getSubSetting(name);
		if(subSetting != null) {
			subSetting.setValue(value);
		} else {
			throw new IllegalArgumentException("No sub-setting named " + name);
		}
	}
	
	public void setChroma(boolean chroma) {
		if(type == Type.COLOR) {
			this.chroma = chroma;
		} else {
			throw new IllegalStateException("This setting is not a color");
		}
	}
	
	public boolean useChroma() {
		return chroma;
	}
	
	protected void valueChanged() {
	}
	
	public void setWithoutLimit(int value) {
		this.value = value;
	}
	
	public void setWithoutLimit(float value) {
		this.value = value;
	}
	
	public int getInt() {
		return (int)value;
	}
	
	public long getColor() {
		if(chroma) {
			return (ChromaUtil.getColor() & 0xffffff) + (((int)value) & 0xff000000);
		} else {
			return Integer.toUnsignedLong((int)value);
		}
	}
	
	public String getString() {
		return (String)value;
	}
	
	public boolean getBool() {
		return (boolean)value;
	}
	
	public Keybind getKeybind() {
		return (Keybind)value;
	}
	
	public float getFloat() {
		return (float)value;
	}
	
	public Setting getSubSetting(String name) {
		if(value instanceof ArrayList) {
			ArrayList<Setting> subSettings = (ArrayList<Setting>)value;
			for(Setting s : subSettings) {
				if(s.getName().equalsIgnoreCase(name)) {
					return s;
				}
			}
		} else {
			throw new IllegalStateException("Setting is not a list of settings");
		}
		return null;
	}
	
	public ArrayList<Setting> getSubSettings() {
		return (ArrayList<Setting>)value;
	}
	
	public void addSubSetting(String name) {}
	
	public List<String> getPossibleValues() {
		return list;
	}
	
	public float getMin() {
		return min;
	}
	
	public float getMax() {
		return max;
	}
	
	public boolean isMutable() {
		return mutable;
	}
	
	public String getWarning() {
		return warning;
	}
	
	public int getWarningSeverity() {
		return warningSeverity;
	}
	
	public String getName() {
		return name;
	}
	
	public Type getType() {
		return type;
	}
	
	public enum Type {
		STRING,
		BOOLEAN,
		INT,
		COLOR,
		LIST,
		KEYBIND,
		FLOAT,
		SETTING_LIST
	}
}
