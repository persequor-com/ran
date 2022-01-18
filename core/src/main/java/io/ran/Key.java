package io.ran;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Key {
	/**
	 * If not set, the order of the fields on the model class will be used for ordering
	 * @return the order in the keyset
	 */
	int order() default -1;

	String name();
	boolean unique() default false;
}
