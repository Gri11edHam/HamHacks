package net.grilledham.hamhacks.setting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ColorSetting {
	
	/**
	 * The translatable name of this setting
	 */
	String name();
	
	/**
	 * The category of this setting
	 */
	String category() default "";
	
	/**
	 * <code>true</code> if this setting should never be displayed to the player
	 */
	boolean neverShow() default false;
	
	/**
	 * <p>Names of fields within the containing class that this setting depends on</p>
	 * <p>When a dependency is <code>false</code>, this setting will not be displayed to the player</p>
	 */
	String[] dependsOn() default {};
}
