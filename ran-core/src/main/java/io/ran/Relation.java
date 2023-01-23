package io.ran;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Relation {
	Class<?> collectionElementType() default None.class;

	Class<?> via() default None.class;

	String[] fields() default {};

	String[] relationFields() default {};

	boolean autoSave() default false;
}
