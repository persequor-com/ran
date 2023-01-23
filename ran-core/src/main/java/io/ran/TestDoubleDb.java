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
