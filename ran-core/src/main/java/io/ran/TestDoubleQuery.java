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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class TestDoubleQuery<T, Z extends CrudRepository.InlineQuery<T, Z>>
		extends CrudRepoBaseQuery<T, Z>
		implements CrudRepository.InlineQuery<T, Z> {
	protected final List<Predicate<T>> filters = new ArrayList<>();
	protected final List<Supplier<List<Object>>> indexLookups = new ArrayList<>();
	protected final List<Comparator<T>> sorts = new ArrayList<>();
	protected final MappingHelper mappingHelper;
	protected final TestDoubleDb testDoubleDb;
	protected final TestDoubleStore<Object, T> store;
	protected final TestDoubleIndex index;
	protected final GenericFactory factory;
	protected int offset;
	protected Integer limit = null;

	public TestDoubleQuery(Class<T> modelType, GenericFactory genericFactory, MappingHelper mappingHelper, TestDoubleDb testDoubleDb) {
		super(modelType, genericFactory);
		this.mappingHelper = mappingHelper;
		this.testDoubleDb = testDoubleDb;
		this.store = testDoubleDb.getStore(modelType);
		this.index = store.getIndex();
		this.factory = genericFactory;
	}

	public Z eq(Property.PropertyValue<?> propertyValue) {
		Property<?> property = propertyValue.getProperty();
		Object value = propertyValue.getValue();

		if (index.contains(property)) {
			indexLookups.add(() -> index.get(property, value));
		}
		filters.add(t -> Objects.equals(getValue(property, t), value));
		return (Z) this;
	}

	public Z gt(Property.PropertyValue<?> propertyValue) {
		Property<?> property = propertyValue.getProperty();
		Object value = propertyValue.getValue();

		if (index.contains(property)) {
			indexLookups.add(() -> index.gt(property, value));
		}
		filters.add(t -> {
			Object actualValue = getValue(property, t);
			if (actualValue instanceof Comparable) {
				return ((Comparable) actualValue).compareTo(value) > 0;
			}
			return false;
		});
		return (Z) this;
	}

	public Z gte(Property.PropertyValue<?> propertyValue) {
		Property<?> property = propertyValue.getProperty();
		Object value = propertyValue.getValue();

		if (index.contains(property)) {
			indexLookups.add(() -> index.gte(property, value));
		}
		filters.add(t -> {
			Object actualValue = getValue(property, t);
			if (actualValue instanceof Comparable) {
				return ((Comparable) actualValue).compareTo(value) >= 0;
			}
			return false;
		});
		return (Z) this;
	}

	public Z lt(Property.PropertyValue<?> propertyValue) {
		Property<?> property = propertyValue.getProperty();
		Object value = propertyValue.getValue();

		if (index.contains(property)) {
			indexLookups.add(() -> index.lt(property, value));
		}
		filters.add(t -> {
			Object actualValue = getValue(property, t);
			if (actualValue instanceof Comparable) {
				return ((Comparable) actualValue).compareTo(value) < 0;
			}
			return false;
		});
		return (Z) this;
	}

	public Z lte(Property.PropertyValue<?> propertyValue) {
		Property<?> property = propertyValue.getProperty();
		Object value = propertyValue.getValue();

		if (index.contains(property)) {
			indexLookups.add(() -> index.lte(property, value));
		}
		filters.add(t -> {
			Object actualValue = getValue(property, t);
			if (actualValue instanceof Comparable) {
				return ((Comparable) actualValue).compareTo(value) <= 0;
			}
			return false;
		});
		return (Z) this;
	}

	public Z isNull(Property<?> property) {
		filters.add(t -> getValue(property, t) == null);
		return (Z) this;
	}

	@Override
	public Z withEager(RelationDescriber relationDescriber) {
		// Eager should not be needed to be implemented by test doubles, as they should already be setup on the model
		return (Z) this;
	}

	@Override
	public <X extends Comparable<X>> Z sortAscending(Property<X> property) {
		this.sorts.add(Comparator.comparing(o -> getSQLLikeValue(property, o)));
		return (Z) this;
	}

	@Override
	public <X extends Comparable<X>> Z sortDescending(Property<X> property) {
		this.sorts.add(Comparator.comparing((T o) -> getSQLLikeValue(property, o)).reversed());
		return (Z) this;
	}

	private <X extends Comparable<X>> X getSQLLikeValue(Property<X> property, T o) {
		X value = getValue(property, o);
		if (value instanceof String) {
			return (X) ((String) value).toLowerCase();
		} else if (value.getClass().isEnum()) {
			return (X) value.toString().toLowerCase();
		}
		return value;
	}

	@Override
	public Z limit(int offset, int limit) {
		this.offset = offset;
		this.limit = limit;
		return (Z) this;
	}

	@Override
	public Z limit(int limit) {
		this.offset = 0;
		this.limit = limit;
		return (Z) this;
	}

	@Override
	public <X, Y extends CrudRepository.InlineQuery<X, Y>> Z subQuery(RelationDescriber relationDescriber, Consumer<Y> consumer) {
		if (!relationDescriber.getVia().isEmpty()) {
			return (Z) subQuery(relationDescriber.getVia().get(0), q -> q.subQuery(relationDescriber.getVia().get(1), (Consumer) consumer));
		}

		Y otherQuery = (Y) getQuery(relationDescriber.getToClass().clazz);
		consumer.accept(otherQuery);

		filters.add(t -> {
			List<X> subResult = otherQuery.execute().collect(Collectors.toList()); // todo sub query executed for every row?
			for (int i = 0; i < relationDescriber.getFromKeys().size(); i++) {
				Object tv = mappingHelper.getValue(t, relationDescriber.getFromKeys().get(i).getProperty());
				int finalI = i;
				subResult.removeIf(o -> {
					Object ov = mappingHelper.getValue(o, relationDescriber.getToKeys().get(finalI).getProperty());
					return !tv.equals(ov);
				});
			}
			return !subResult.isEmpty();
		});
		return (Z) this;
	}

	protected abstract Z getQuery(Class<?> queryClass);

	@Override
	public Stream<T> execute() {
		return executeInternal().map(e -> mappingHelper.makeCopy(clazz, e));
	}

	protected Stream<T> executeInternal() {
		Stream<T> values;
		if (!indexLookups.isEmpty()) {
			values = indexLookups.stream().map(Supplier::get)
					.min(Comparator.comparing(List::size)).orElseThrow(RuntimeException::new)
					.stream().map(store::get);
		} else {
			values = store.values().stream();
		}

		for (Predicate<T> filter : filters) {
			values = values.filter(filter);
		}

		if (!sorts.isEmpty()) {
			Comparator<T> c = null;
			for (Comparator<T> comparator : sorts) {
				if (c == null) {
					c = comparator;
				} else {
					c = c.thenComparing(comparator);
				}
			}
			values = values.sorted(c);
		}

		values = values.skip(offset);
		if (limit != null) {
			values = values.limit(limit);
		}
		return values;
	}

	@Override
	public long count() {
		return executeInternal().count();
	}

	@Override
	public CrudRepository.CrudUpdateResult delete() {
		Iterator<T> iterator = store.values().iterator();
		int count = 0;
		while (iterator.hasNext()) {
			T val = iterator.next();
			if (filters.stream().allMatch(f -> f.test(val))) {
				iterator.remove();
				count++;
			}
		}
		int deleted = count;
		return () -> deleted;
	}

	private <X> X getValue(Property<X> property, T t) {
		return (X) mappingHelper.getValue(t, property);
	}
}
