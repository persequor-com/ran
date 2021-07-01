package io.ran;

import java.util.Map;
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

	Map<Object, T> getStore(Class<T> modelType) {
		return store.getStore(modelType);
	}

	@Override
	public Optional<T> get(K k) {
		return Optional.ofNullable(getStore(modelType).get(getKeyFromKey(k)));
	}



	@Override
	public Stream<T> getAll() {
		return getStore(modelType).values().stream();
	}

	@Override
	public CrudRepository.CrudUpdateResult deleteById(K k) {
		T existing = getStore(modelType).remove(k);
		return () -> existing != null ? 1 : 0;
	}

	@Override
	public CrudRepository.CrudUpdateResult save(T t) {
		Object key = getKey(t);
		T existing = getStore(modelType).put((Object) key, t);
		return new CrudRepository.CrudUpdateResult() {
			@Override
			public int affectedRows() {
				return existing != null && !existing.equals(t) ? 1 : 0;
			}
		};
	}

	private Object getKeyFromKey(K key) {
		if (keyType.equals(modelType)) {
			return mappingHelper.getKey(key);
		} else {
			return key;
		}
	}

	private Object getKey(T t) {
		Object key;
		CompoundKey k = getCompoundKeyFor(t);
		if (keyType.equals(modelType)) {
			key = mappingHelper.getKey(t);
		} else {
			key = (K)((Property.PropertyValueList<?>)k.getValues()).get(0).getValue();
		}
		return key;
	}

	private CompoundKey getCompoundKeyFor(Object t) {
		return mappingHelper.getKey(t);
	}
}
