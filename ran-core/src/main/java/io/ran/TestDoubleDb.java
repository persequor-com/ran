/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran;

import javax.inject.Singleton;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class TestDoubleDb {
	public Map<Class<?>, Map<Object, Object>> store = Collections.synchronizedMap(new HashMap<>());

	public <T> Map<Object, T> getStore(Class<T> modelType) {
		return (Map<Object, T>) store.computeIfAbsent(modelType, t -> Collections.synchronizedMap(new HashMap<>()));
	}
}
