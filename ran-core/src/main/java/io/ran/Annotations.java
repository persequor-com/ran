/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;

public class Annotations {
	HashMap<Class, Annotation> map = new HashMap<>();

	public void addFrom(Clazz clazz) {
		Arrays.asList(clazz.clazz.getAnnotations()).forEach(annotation -> {
			map.put(annotation.annotationType(), annotation);
		});
	}

	public void addFrom(Field field) {
		Arrays.asList(field.getAnnotations()).forEach(annotation -> {
			map.put(annotation.annotationType(), annotation);
		});
	}

	public <T extends Annotation> T get(Class<T> dbType) {
		return (T) map.get(dbType);
	}
}
