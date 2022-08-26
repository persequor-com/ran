package io.ran;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class TestDoubleDb {
	public Map<Class<?>, Store<Object, Object>> store = Collections.synchronizedMap(new HashMap<>());
	private MappingHelper mappingHelper;

	@Inject
	public TestDoubleDb(MappingHelper mappingHelper) {
		this.mappingHelper = mappingHelper;
	}

	public <T> Store<Object, T> getStore(Class<T> modelType) {
		return (Store<Object, T>)store.computeIfAbsent(modelType, t -> (Store)new Store<>(mappingHelper));
	}
}
