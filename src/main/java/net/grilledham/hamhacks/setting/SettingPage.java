package net.grilledham.hamhacks.setting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SettingPage {
	
	/**
	 * The translatable name of this setting page
	 */
	String name();
	
	/**
	 * The category of this setting page
	 */
	String category() default "";
	
	/**
	 * <p>Names of fields within the containing class that this setting page depends on</p>
	 * <p>When a dependency is <code>false</code>, this page will not be available to the player</p>
	 */
	String[] dependsOn() default {};
}
