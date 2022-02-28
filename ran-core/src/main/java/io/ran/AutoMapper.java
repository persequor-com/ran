/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 
 */
package io.ran;

import java.util.HashMap;
import java.util.Map;

public class AutoMapper {
	private static Map<Class, Class> mapped = new HashMap<>();
	private static Map<Class, Class> query = new HashMap<>();
	private static MapperGenerator mapperGenerator = new MapperGenerator();
	private static AutoMapperClassLoader classLoader = new AutoMapperClassLoader(AutoMapper.class.getClassLoader());

	public Map<Class, Class> getMapped() {
		return mapped;
	}

	public static void map(Class aClass) {
		if(!mapped.containsKey(aClass)) {
			synchronized (AutoMapper.class) {
				if(!mapped.containsKey(aClass)) {
					try {
						MapperGenerator.Wrapped wrapped = mapperGenerator.generate(classLoader, Clazz.of(aClass));

						mapped.put(aClass, wrapped.mapping);
						query.put(aClass, wrapped.query);
					} catch (Throwable e) {
						System.out.println("Error generating mappings for: " + aClass.getName());
						System.out.println(e.toString());
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				}
			}
		}
	}

	public synchronized static  <X, Z extends X> Class<Z> get(Class<X> xClass) {
		if(!mapped.containsKey(xClass)) {
			map(xClass);
		}
		return (Class<Z>)mapped.get(xClass);
	}

	public synchronized static  <X, Z extends  X> Class<Z> getQueryMaps(Class<X> xClass) {
		return query.get(xClass);
	}
}
