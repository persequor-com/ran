/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 
 */
package io.ran;

import java.util.Collection;

public interface Resolver {
	<FROM, TO> TO get(Class<FROM> fromClass, String field, FROM obj);
	<FROM, TO> Collection<TO> getCollection(Class<FROM> fromClass, String field, FROM obj);


	default <FROM, TO, L extends Collection<TO>> L getTypedCollection(Class<FROM> fromClass, Class<L> lClass, String field, FROM obj) {
		try {
			Collection<TO> collection = getCollection(fromClass, field, obj);
			if (lClass.isInstance(collection)) {
				return (L) collection;
			}
			Class<?> collectionClass = lClass;
			if (lClass.isInterface()) {
				collectionClass = CollectionMap.collectionMap.get(lClass);
			}
			return (L) collectionClass.getConstructor(Collection.class).newInstance(collection);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
