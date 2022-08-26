package io.ran;

import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TestDoubleIndex {
    private Map<String, IndexResult> index = new HashMap<>();

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

    public void add(Property<?> property, Object indexValue, Object key) {
        index.computeIfAbsent(property.getSnakeCase(), sc -> new IndexResult())
                .add(indexValue, key);
    }

    public static class IndexResult {
        Map<Object, List<Object>> result = new HashMap<>();

        public void add(Object indexValue, Object primaryKeyValue) {
            result.computeIfAbsent(indexValue, iv -> new ArrayList<>()).add(primaryKeyValue);
        }

        public List<Object> getResult(Object indexKey) {
            if (!result.containsKey(indexKey)) {
                return Collections.emptyList();
            }
            return result.get(indexKey);
        }
    }

}
