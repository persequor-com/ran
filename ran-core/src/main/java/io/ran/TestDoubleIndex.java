package io.ran;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class TestDoubleIndex {
	private final Map<String, IndexResult> index = new HashMap<>();

	public boolean contains(Property<?> property) {
		return index.containsKey(property.getSnakeCase());
	}

	public List<Object> get(Property<?> property, Object value) {
		IndexResult idx = index.get(property.getSnakeCase());
		if (idx == null) {
			return Collections.emptyList();
		}
		return idx.getResult(value);
	}

	public List<Object> lt(Property<?> property, Object value) {
		IndexResult idx = index.get(property.getSnakeCase());
		if (idx == null) {
			return Collections.emptyList();
		}
		return idx.lt(value);
	}

	public List<Object> lte(Property<?> property, Object value) {
		IndexResult idx = index.get(property.getSnakeCase());
		if (idx == null) {
			return Collections.emptyList();
		}
		return idx.lte(value);
	}

	public List<Object> gt(Property<?> property, Object value) {
		IndexResult idx = index.get(property.getSnakeCase());
		if (idx == null) {
			return Collections.emptyList();
		}
		return idx.gt(value);
	}

	public List<Object> gte(Property<?> property, Object value) {
		IndexResult idx = index.get(property.getSnakeCase());
		if (idx == null) {
			return Collections.emptyList();
		}
		List<Object> res = idx.gte(value);
		res.addAll(get(property, value));
		return res;
	}

	public void add(Property<?> property, Object indexValue, Object key) {
		index.computeIfAbsent(property.getSnakeCase(), sc -> new IndexResult())
				.add(indexValue, key);
	}

	public static class IndexResult {
		NavigableMap<Object, List<Object>> result = new TreeMap<>();

		public void add(Object indexValue, Object primaryKeyValue) {
			result.computeIfAbsent(indexValue, iv -> new ArrayList<>()).add(primaryKeyValue);
		}

		public List<Object> getResult(Object indexKey) {
			if (!result.containsKey(indexKey)) {
				return Collections.emptyList();
			}
			return result.get(indexKey);
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
