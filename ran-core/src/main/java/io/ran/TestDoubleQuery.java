package io.ran;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class TestDoubleQuery<T, Z extends CrudRepository.InlineQuery<T, Z>>
		extends CrudRepoBaseQuery<T, Z>
		implements CrudRepository.InlineQuery<T, Z> {
	protected List<Predicate<T>> filters = new ArrayList<>();
	protected List<Comparator<T>> sorts = new ArrayList<>();
	protected Integer limit = null;
	protected MappingHelper mappingHelper;
	protected TestDoubleDb testDoubleDb;
	protected GenericFactory factory;
	protected int offset;
	protected TypeDescriberFactory typeDescriberFactory;

	public TestDoubleQuery(Class<T> modelType, GenericFactory genericFactory, MappingHelper mappingHelper, TestDoubleDb testDoubleDb, TypeDescriberFactory typeDescriberFactory) {
		super(modelType, genericFactory, typeDescriberFactory);
		this.mappingHelper = mappingHelper;
		this.testDoubleDb = testDoubleDb;
		this.factory = genericFactory;
		this.typeDescriberFactory = typeDescriberFactory;
	}

	public Z eq(Property.PropertyValue<?> propertyValue) {
		filters.add(t -> {
			Object actualValue = getValue(propertyValue.getProperty(), t);
			return Objects.equals(actualValue, propertyValue.getValue());
		});
		return (Z)this;
	}

	public Z gt(Property.PropertyValue<?> propertyValue) {
		filters.add(t -> {
			Object actualValue = getValue(propertyValue.getProperty(), t);
			if (actualValue instanceof Comparable) {
				return ((Comparable) actualValue).compareTo(propertyValue.getValue()) > 0;
			}
			return false;
		});
		return (Z)this;
	}

	public Z gte(Property.PropertyValue<?> propertyValue) {
		filters.add(t -> {
			Object actualValue = getValue(propertyValue.getProperty(), t);
			if (actualValue instanceof Comparable) {
				return ((Comparable) actualValue).compareTo(propertyValue.getValue()) >= 0;
			}
			return false;
		});
		return (Z)this;
	}

	public Z lt(Property.PropertyValue<?> propertyValue) {
		filters.add(t -> {
			Object actualValue = getValue(propertyValue.getProperty(), t);
			if (actualValue instanceof Comparable) {
				return ((Comparable) actualValue).compareTo(propertyValue.getValue()) < 0;
			}
			return false;
		});
		return (Z)this;
	}

	public Z lte(Property.PropertyValue<?> propertyValue) {
		filters.add(t -> {
			Object actualValue = getValue(propertyValue.getProperty(), t);
			if (actualValue instanceof Comparable) {
				return ((Comparable) actualValue).compareTo(propertyValue.getValue()) <= 0;
			}
			return false;
		});
		return (Z)this;
	}

	public Z isNull(Property<?> property) {
		filters.add(t -> {
			Object actualValue = getValue(property, t);
			return actualValue == null;
		});
		return (Z)this;
	}

	@Override
	public Z withEager(RelationDescriber relationDescriber) {
		// Eager should not be needed to be implemented by test doubles, as they should already be setup on the model
		return (Z)this;
	}

	@Override
	public <X extends Comparable<X>> Z sortAscending(Property<X> property) {
		this.sorts.add(Comparator.comparing(o -> getValue(property, o)));
		return (Z)this;
	}

	@Override
	public <X extends Comparable<X>> Z sortDescending(Property<X> property) {
		this.sorts.add(Comparator.comparing((T o) -> getValue(property, o)).reversed());
		return (Z)this;
	}

	@Override
	public Z limit(int offset, int limit) {
		this.offset = offset;
		this.limit = limit;
		return (Z)this;
	}

	@Override
	public Z limit(int limit) {
		this.offset = 0;
		this.limit = limit;
		return (Z)this;
	}

	@Override
	public <X, Y extends CrudRepository.InlineQuery<X, Y>> Z subQuery(RelationDescriber relationDescriber, Consumer<Y> consumer) {
		if (!relationDescriber.getVia().isEmpty()) {
			return (Z)subQuery(relationDescriber.getVia().get(0), q -> {
				q.subQuery(relationDescriber.getVia().get(1), (Consumer) consumer);
			});
		}

		Y otherQuery = (Y)getQuery(relationDescriber.getToClass().clazz);
		consumer.accept(otherQuery);

		filters.add(t -> {
			List<X> subResult = otherQuery.execute().collect(Collectors.toList());
			for(int i=0;i<relationDescriber.getFromKeys().size();i++) {
				Object tv = mappingHelper.getValue(t, relationDescriber.getFromKeys().get(i).getProperty());
				int finalI = i;
				subResult.removeIf(o -> {
					Object ov = mappingHelper.getValue(o, relationDescriber.getToKeys().get(finalI).getProperty());
					return !tv.equals(ov);
				});
			}
			return !subResult.isEmpty();
		});
		return (Z)this;


	}

	protected abstract Z getQuery(Class<?> queryClass);

	@Override
	public Stream<T> execute() {
		Stream<T> values = testDoubleDb.getStore(clazz).values().stream();
		for (Predicate<T> filter : filters) {
			values = values.filter(filter);
		}
		List<T> list = values.collect(Collectors.toList());
		if(!sorts.isEmpty()) {
			Comparator<T> c = null;
			for (Comparator<T> comparator : sorts) {
				if (c == null) {
					c = comparator;
				} else {
					c = c.thenComparing(comparator);
				}
			}
			list.sort(c);
		}
		if (limit != null) {
			list = list.subList(offset, offset+limit);
		}
		return list.stream();
	}

	@Override
	public long count() {
		return execute().count();
	}

	@Override
	public CrudRepository.CrudUpdateResult delete() {
		Stream<T> values = testDoubleDb.getStore(clazz).values().stream();
		for (Predicate<T> filter : filters) {
			values = values.filter(filter);
		}
		List<Object> toDelete = new ArrayList<>();
		testDoubleDb.getStore(clazz).entrySet().forEach(entry -> {
			for (Predicate<T> filter : filters) {
				if (!filter.test(entry.getValue())) {
					return;
				}
			}
			toDelete.add(entry.getKey());
		});
		toDelete.forEach(d -> testDoubleDb.getStore(clazz).remove(d));
		return () -> toDelete.size();
	}

	private <X> X getValue(Property<X> property, T t) {
		return (X)mappingHelper.getValue(t, property);
	}

}