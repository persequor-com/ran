/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
