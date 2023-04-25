/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class CrudRepositoryTestDoubleBase<T, K> implements CrudRepository<T, K> {
	protected final TestDoubleDb store;
	protected GenericFactory genericFactory;
	protected Class<T> modelType;
	protected Class<K> keyType;
	protected TypeDescriber<T> typeDescriber;
	protected MappingHelper mappingHelper;

	public CrudRepositoryTestDoubleBase(GenericFactory genericFactory, Class<T> modelType, Class<K> keyType, MappingHelper mappingHelper, TestDoubleDb store) {
		this.store = store;
		this.genericFactory = genericFactory;
		this.modelType = modelType;

		this.keyType = keyType;
		this.typeDescriber = TypeDescriberImpl.getTypeDescriber(modelType);
		this.mappingHelper = mappingHelper;
	}

	<Z> TestDoubleStore<Object, Z> getStore(Class<Z> modelType) {
		return store.getStore(modelType);
	}

	@Override
	public Optional<T> get(K k) {
		return Optional.ofNullable(getStore(modelType).get(getKeyFromKey(k))).map(s -> mappingCopy(s, modelType));
	}


	@Override
	public Stream<T> getAll() {
		return getStore(modelType).values().stream().map(s -> mappingCopy(s, modelType));
	}

	@Override
	public CrudRepository.CrudUpdateResult deleteById(K k) {
		T existing = getStore(modelType).remove(k);
		return () -> existing != null ? 1 : 0;
	}

	@Override
	public CrudRepository.CrudUpdateResult deleteByIds(Collection<K> k) {
		return () -> k
				.stream()
				.mapToInt(element -> getStore(modelType).remove(element) != null ? 1 : 0)
				.sum();
	}

	private <Z> Z mappingCopy(Z t, Class<Z> zClass) {
		Z tc = genericFactory.get(zClass);
		mappingHelper.copyValues(zClass, t, tc);
		return tc;
	}

	@Override
	public CrudRepository.CrudUpdateResult save(T t) {
		return save(t, modelType);
	}

	protected <Z> CrudRepository.CrudUpdateResult save(Z t, Class<Z> zClass) {
		Object key = getKey(t);
		TestDoubleStore<Object, Z> thisStore = getStore(zClass);
		List<KeySet> keys = new ArrayList<>();
		keys.add(typeDescriber.primaryKeys());
		keys.addAll(typeDescriber.indexes());
		Z existing = thisStore.put(key, mappingCopy(t, zClass), keys);

		return () -> existing != null && !existing.equals(t) ? 1 : 0;
	}

	private Object getKeyFromKey(K key) {
		if (keyType.equals(modelType)) {
			return mappingHelper.getKey(key);
		} else {
			return key;
		}
	}

	private Object getKey(Object t) {
		Object key;
		CompoundKey k = getCompoundKeyFor(t);
		if (keyType.equals(modelType)) {
			key = mappingHelper.getKey(t);
		} else {
			key = ((Property.PropertyValueList<?>) k.getValues()).get(0).getValue();
		}
		return key;
	}

	private CompoundKey getCompoundKeyFor(Object t) {
		return mappingHelper.getKey(t);
	}
}
