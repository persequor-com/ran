package io.ran;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class Store<K,V> {
    private final Map<K,V> map = Collections.synchronizedMap(new HashMap<K, V>());
    public final TestDoubleIndex index = new TestDoubleIndex();
    private MappingHelper mappingHelper;

    @Inject
    public Store(MappingHelper mappingHelper) {
        this.mappingHelper = mappingHelper;
    }

    public void index(KeySet keySet, V obj, Object key) {
        List<KeySet.Field> keys = keySet.stream().collect(Collectors.toList());
        for(KeySet.Field field : keySet.parts) {

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
