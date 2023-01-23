/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
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
