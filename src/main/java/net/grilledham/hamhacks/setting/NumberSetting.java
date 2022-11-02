package net.grilledham.hamhacks.setting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NumberSetting {
	
	/**
	 * The translatable name of this setting
	 */
	String name();
	
	/**
	 * The default value of this setting
	 */
	float defaultValue() default 0;
	
	/**
	 * <code>true</code> if this setting should never be displayed to the player
	 */
	boolean neverShow() default false;
	
	/**
	 * <p>Names of fields within the containing class that this setting depends on</p>
	 * <p>When a dependency is <code>false</code>, this setting will not be displayed to the player</p>
	 */
	String[] dependsOn() default {};
	
	/**
	 * The minimum value for this setting
	 */
	float min();
	
	/**
	 * The maximum value for this setting
	 */
	float max();
	
	/**
	 * How large of a step in between values
	 */
	float step() default -1;
	
	/**
	 * Should the step be forced
	 */
	boolean forceStep() default true;
}
