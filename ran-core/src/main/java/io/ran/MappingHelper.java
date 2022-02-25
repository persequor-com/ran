package io.ran;

import io.ran.token.Token;

import javax.inject.Inject;
import java.util.function.Consumer;
import java.util.function.Function;

public class MappingHelper {
	private final GenericFactory genericFactory;
	private final AutoMapper autoMapper;
	@Inject
	public MappingHelper(GenericFactory genericFactory, AutoMapper autoMapper) {
		this.genericFactory = genericFactory;
		this.autoMapper = autoMapper;
	}

	public void hydrate(Object toHydrate, ObjectMapHydrator hydrator) {
		if (toHydrate instanceof Mapping) {
			((Mapping) toHydrate).hydrate(hydrator);
		} else {
			((Mapping)genericFactory.get(toHydrate.getClass())).hydrate(toHydrate, hydrator);
		}
	}

	public void columnize(Object toColumnize, ObjectMapColumnizer columnizer) {
		if (toColumnize instanceof Mapping) {
			((Mapping) toColumnize).columnize(columnizer);
		} else {
			((Mapping)genericFactory.get(toColumnize.getClass())).columnize(toColumnize, columnizer);
		}
	}

	public CompoundKey getKey(Object obj) {
		if (obj instanceof Mapping) {
			return ((Mapping) obj)._getKey();
		} else {
			return ((Mapping)genericFactory.get(obj.getClass()))._getKey(obj);
		}
	}

	public Object getValue(Object obj, Property property) {
		if (obj instanceof Mapping) {
			return ((Mapping) obj)._getValue(property);
		} else {
			return ((Mapping)genericFactory.get(obj.getClass()))._getValue(obj, property);
		}
	}

	public Object getRelation(Object obj, RelationDescriber describer) {
		if (obj instanceof Mapping) {
			return ((Mapping) obj)._getRelation(describer);
		} else {
			return ((Mapping)genericFactory.get(obj.getClass()))._getRelation(obj, describer);
		}
	}

	public Object getRelation(Object obj, Token fieldToken) {
		return ((Mapping)genericFactory.get(obj.getClass()))._getRelation(obj, fieldToken);
	}

	public <T> ClazzMethod getMethod(Class<T> tClass, Function<T, ?> methodReference) {
		TypeDescriberImpl.getTypeDescriber(tClass, autoMapper);
		T queryInstance = genericFactory.getQueryInstance(tClass);
		methodReference.apply(queryInstance);
		return ((QueryWrapper)queryInstance).getCurrentMethod();
	}

	public <T> ClazzMethod getMethod(Class<T> tClass, Consumer<T> methodReference) {
		TypeDescriberImpl.getTypeDescriber(tClass, autoMapper);
		T queryInstance = genericFactory.getQueryInstance(tClass);
		methodReference.accept(queryInstance);
		return ((QueryWrapper)queryInstance).getCurrentMethod();
	}
}
