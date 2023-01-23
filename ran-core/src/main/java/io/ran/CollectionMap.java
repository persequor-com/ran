package io.ran;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionMap {
	public static Map<Class<?>, Class<?>> collectionMap = new HashMap<>();

	static {
		collectionMap.put(List.class, ArrayList.class);
		collectionMap.put(Collection.class, ArrayList.class);
	}
}
