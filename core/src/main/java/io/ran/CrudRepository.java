/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 
 */
package io.ran;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public interface CrudRepository<T, K> {
	Optional<T> get(K id);
	Stream<T> getAll();
	CrudUpdateResult deleteById(K id);
	CrudUpdateResult deleteByIds(Collection<K> id);
	CrudUpdateResult save(T t);

	interface InlineQuery<T, Q extends InlineQuery<T, Q>> {
		<X> Q eq(Function<T, X> field, X value);
		<X> Q eq(BiConsumer<T, X> field, X value);
		<X extends Comparable<X>> Q gt(Function<T, X> field, X value);
		<X extends Comparable<X>> Q gt(BiConsumer<T, X> field, X value);
		<X extends Comparable<X>> Q lt(Function<T, X> field,  X value);
		<X extends Comparable<X>> Q lt(BiConsumer<T, X> field,  X value);
		<X> Q isNull(Function<T, X> field);
		<X> Q isNull(BiConsumer<T, X> field);
		<X> Q withEager(Function<T, X> field);
		<X> Q withEager(BiConsumer<T, X> field);
//		<X, Z extends InlineQuery<X, Z>> Q subQuery(Function<T, X> field, Consumer<Z> subQuery);
//		<X, Z extends InlineQuery<X, Z>> Q subQuery(BiConsumer<T, X> field, Consumer<Z> subQuery);
//		<X, Z extends InlineQuery<X, Z>> Q subQueryList(Function<T, List<X>> field, Consumer<Z> subQuery);
//		<X, Z extends InlineQuery<X, Z>> Q subQueryList(BiConsumer<T, List<X>> field, Consumer<Z> subQuery);

		Q eq(Property.PropertyValue<?> property);
		Q gt(Property.PropertyValue<?> property);
		Q lt(Property.PropertyValue<?> property);
		Q isNull(Property<?> property);
		Q withEager(RelationDescriber relation);

		<X extends Comparable<X>> Q sortAscending(Function<T, X> field);
		<X extends Comparable<X>> Q sortAscending(BiConsumer<T, X> field);
		<X extends Comparable<X>> Q sortDescending(Function<T, X> property);
		<X extends Comparable<X>> Q sortDescending(BiConsumer<T, X> property);
		<X extends Comparable<X>> Q sortAscending(Property<X> property);
		<X extends Comparable<X>> Q sortDescending(Property<X> property);
		Q limit(int offset, int limit);
		Q limit(int limit);

		<X, Z extends InlineQuery<X, Z>> Q subQuery(RelationDescriber relationDescriber, Consumer<Z> subQuery);

		Stream<T> execute();
		long count();
		CrudUpdateResult delete();
	}
//
//	interface Query<T> {
//		Query<T> eq(Property.PropertyValue<?> property);
//		Query<T> gt(Property.PropertyValue<?> property);
//		Query<T> lt(Property.PropertyValue<?> property);
//		Query<T> isNull(Property<?> property);
//		Query<T> withEager(RelationDescriber relation);
//
//		Stream<T> execute();
//	}

	interface CrudUpdateResult {
		int affectedRows();
	}
}
