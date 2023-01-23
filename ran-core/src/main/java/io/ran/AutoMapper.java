/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
		if (!mapped.containsKey(aClass)) {
			synchronized (AutoMapper.class) {
				if (!mapped.containsKey(aClass)) {
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

	public synchronized static <X, Z extends X> Class<Z> get(Class<X> xClass) {
		if (!mapped.containsKey(xClass)) {
			map(xClass);
		}
		return (Class<Z>) mapped.get(xClass);
	}

	public synchronized static <X, Z extends X> Class<Z> getQueryMaps(Class<X> xClass) {
		return query.get(xClass);
	}
}
