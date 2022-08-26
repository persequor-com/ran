package io.ran;

import java.util.*;
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

	Store<Object, T> getStore(Class<T> modelType) {
		return store.getStore(modelType);
	}

	@Override
	public Optional<T> get(K k) {
		return Optional.ofNullable(getStore(modelType).get(getKeyFromKey(k))).map(this::mappingCopy);
	}



	@Override
	public Stream<T> getAll() {
		return getStore(modelType).values().stream().map(this::mappingCopy);
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

	private T mappingCopy(T t) {
		T tc = genericFactory.get(modelType);
		mappingHelper.copyValues(modelType, t, tc);
		return tc;
	}

	@Override
	public CrudRepository.CrudUpdateResult save(T t) {
		Object key = getKey(t);
		Store<Object, T> thisStore = getStore(modelType);
		List<KeySet> keys = new ArrayList<>();
		keys.add(typeDescriber.primaryKeys());
		keys.addAll(typeDescriber.indexes());
		T existing = thisStore.put(key, mappingCopy(t), keys);

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
