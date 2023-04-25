package io.ran;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

public class TestDoubleIndex {
	private final Map<String, IndexResult> index = new ConcurrentHashMap<>();

	public boolean contains(Property<?> property) {
		return index.containsKey(property.getSnakeCase());
	}

	public void add(Property<?> property, Object indexValue, Object key) {
		index.computeIfAbsent(property.getSnakeCase(), sc -> new IndexResult())
				.add(indexValue, key);
	}

	public List<Object> get(Property<?> property, Object value) {
		return getIndex(property.getSnakeCase()).get(value);
	}

	public List<Object> lt(Property<?> property, Object value) {
		return getIndex(property.getSnakeCase()).lt(value);
	}

	public List<Object> lte(Property<?> property, Object value) {
		return getIndex(property.getSnakeCase()).lte(value);
	}

	public List<Object> gt(Property<?> property, Object value) {
		return getIndex(property.getSnakeCase()).gt(value);
	}

	public List<Object> gte(Property<?> property, Object value) {
		return getIndex(property.getSnakeCase()).gte(value);
	}

	private IndexResult getIndex(String propertyName) {
		IndexResult idx = index.get(propertyName);
		if (idx == null) {
			throw new UnsupportedOperationException("There is no index for " + propertyName);
		}
		return idx;
	}

	public static class IndexResult {
		ConcurrentNavigableMap<Object, List<Object>> result = new ConcurrentSkipListMap<>();

		public void add(Object indexValue, Object primaryKeyValue) {
			result.computeIfAbsent(indexValue, iv -> new ArrayList<>()).add(primaryKeyValue);
		}

		public List<Object> get(Object indexKey) {
			List<Object> res = result.get(indexKey);
			if (res == null) {
				return Collections.emptyList();
			}
			return res;
		}

		public List<Object> lt(Object value) {
			return result.headMap(value, false).values().stream().flatMap(Collection::stream).collect(Collectors.toList());
		}

		public List<Object> lte(Object value) {
			return result.headMap(value, true).values().stream().flatMap(Collection::stream).collect(Collectors.toList());
		}

		public List<Object> gt(Object value) {
			return result.tailMap(value, false).values().stream().flatMap(Collection::stream).collect(Collectors.toList());
		}

		public List<Object> gte(Object value) {
			return result.tailMap(value, true).values().stream().flatMap(Collection::stream).collect(Collectors.toList());
		}
	}

}
