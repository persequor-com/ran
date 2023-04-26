package io.ran;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TestDoubleStore<K, V> {
	private final Map<K, V> map = new ConcurrentHashMap<>();
	public final TestDoubleIndex index = new TestDoubleIndex();
	private final MappingHelper mappingHelper;

	@Inject
	public TestDoubleStore(MappingHelper mappingHelper) {
		this.mappingHelper = mappingHelper;
	}

	public TestDoubleIndex getIndex() {
		return index;
	}

	public void index(KeySet keySet, V obj, Object key) {
		for (KeySet.Field field : keySet.parts) {
			index.add(field.getProperty(), getValueUntyped(field.getProperty(), obj), key);
		}
	}

	public Collection<V> values() {
		return map.values();
	}

	public Set<Map.Entry<K, V>> entrySet() {
		return map.entrySet();
	}

	public V remove(K d) {
		return map.remove(d);
	}

	public V get(K keyFromKey) {
		return map.get(keyFromKey);
	}

	public V put(K key, V mappingCopy, List<KeySet> indexes) {
		for (KeySet keySet : indexes) {
			index(keySet, mappingCopy, key);
		}
		return map.put(key, mappingCopy);
	}

	private Object getValueUntyped(Property property, Object t) {
		return mappingHelper.getValue(t, property);
	}

}
