/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran;

import io.ran.CrudRepository.InlineQuery;

import java.util.function.BiConsumer;
import java.util.function.Function;


public abstract class CrudRepoBaseQuery<T, Z extends InlineQuery<T, Z>> implements InlineQuery<T, Z> {
	protected T instance;
	protected QueryWrapper queryWrapper;
	protected Class<T> clazz;
	protected TypeDescriber<T> typeDescriber;
	protected GenericFactory genericFactory;
	// The currentProperty is usede to pass on the property which was called by the field consumer
	// For example: query().eq(Car::getId(), "the id") would call the eq method here, which would set the currentProperty to the property of the id field
	protected Property currentProperty = null;

	public CrudRepoBaseQuery(Class<T> clazz, GenericFactory genericFactory) {
		this.clazz = clazz;
		this.typeDescriber = TypeDescriberImpl.getTypeDescriber(clazz);
		this.genericFactory = genericFactory;
		instance = genericFactory.getQueryInstance(clazz);
		queryWrapper = (QueryWrapper) instance;
	}

	@Override
	public <X> Z eq(Function<T, X> field, X value) {
		field.apply(instance);
		this.eq(queryWrapper.getCurrentProperty().value(value));
		return (Z) this;
	}

	@Override
	public <X> Z eq(BiConsumer<T, X> field, X value) {
		field.accept(instance, null);
		this.eq(queryWrapper.getCurrentProperty().value(value));
		return (Z) this;
	}

	@Override
	public <X extends Comparable<X>> Z gt(Function<T, X> field, X value) {
		field.apply(instance);
		this.gt(queryWrapper.getCurrentProperty().value(value));
		return (Z) this;
	}

	@Override
	public <X extends Comparable<X>> Z gt(BiConsumer<T, X> field, X value) {
		field.accept(instance, null);
		this.gt(queryWrapper.getCurrentProperty().value(value));
		return (Z) this;
	}

	@Override
	public <X extends Comparable<X>> Z gte(Function<T, X> field, X value) {
		field.apply(instance);
		this.gte(queryWrapper.getCurrentProperty().value(value));
		return (Z) this;
	}

	@Override
	public <X extends Comparable<X>> Z gte(BiConsumer<T, X> field, X value) {
		field.accept(instance, null);
		this.gte(queryWrapper.getCurrentProperty().value(value));
		return (Z) this;
	}

	@Override
	public <X extends Comparable<X>> Z lt(Function<T, X> field, X value) {
		field.apply(instance);
		this.lt(queryWrapper.getCurrentProperty().value(value));
		return (Z) this;
	}

	@Override
	public <X extends Comparable<X>> Z lt(BiConsumer<T, X> field, X value) {
		field.accept(instance, null);
		this.lt(queryWrapper.getCurrentProperty().value(value));
		return (Z) this;
	}

	@Override
	public <X extends Comparable<X>> Z lte(Function<T, X> field, X value) {
		field.apply(instance);
		this.lte(queryWrapper.getCurrentProperty().value(value));
		return (Z) this;
	}

	@Override
	public <X extends Comparable<X>> Z lte(BiConsumer<T, X> field, X value) {
		field.accept(instance, null);
		this.lte(queryWrapper.getCurrentProperty().value(value));
		return (Z) this;
	}

	@Override
	public <X> Z isNull(Function<T, X> field) {
		field.apply(instance);
		this.isNull(queryWrapper.getCurrentProperty().copy());
		return (Z) this;
	}

	@Override
	public <X> Z isNull(BiConsumer<T, X> field) {
		field.accept(instance, null);
		this.isNull(queryWrapper.getCurrentProperty().copy());
		return (Z) this;
	}

	@Override
	public <X extends Comparable<X>> Z sortAscending(Function<T, X> field) {
		field.apply(instance);
		this.sortAscending(queryWrapper.getCurrentProperty().copy());
		return (Z) this;
	}

	@Override
	public <X extends Comparable<X>> Z sortAscending(BiConsumer<T, X> field) {
		field.accept(instance, null);
		this.sortAscending(queryWrapper.getCurrentProperty().copy());
		return (Z) this;
	}

	@Override
	public <X extends Comparable<X>> Z sortDescending(Function<T, X> field) {
		field.apply(instance);
		this.sortDescending(queryWrapper.getCurrentProperty().copy());
		return (Z) this;
	}

	@Override
	public <X extends Comparable<X>> Z sortDescending(BiConsumer<T, X> field) {
		field.accept(instance, null);
		this.sortDescending(queryWrapper.getCurrentProperty().copy());
		return (Z) this;
	}

	@Override
	public <X> Z withEager(Function<T, X> field) {
		field.apply(instance);
		this.withEager(typeDescriber.relations().get(queryWrapper.getCurrentProperty().copy().getToken().snake_case()));
		return (Z) this;
	}

	@Override
	public <X> Z withEager(BiConsumer<T, X> field) {
		field.accept(instance, null);
		this.withEager(typeDescriber.relations().get(queryWrapper.getCurrentProperty().getToken().snake_case()));
		return (Z) this;
	}

//	@Override
//	public <X, SQ extends InlineQuery<X, SQ>> Z subQuery(Function<T, X> field, Consumer<SQ> subQuery) {
//		field.apply(instance);
//		this.subQuery(typeDescriber.relations().get(queryWrapper.getCurrentProperty().getToken().snake_case()), subQuery);
//		return (Z)this;
//	}
//
//	@Override
//	public <X, SQ extends InlineQuery<X, SQ>> Z subQuery(BiConsumer<T, X> field, Consumer<SQ> subQuery) {
//		field.accept(instance, null);
//		this.subQuery(typeDescriber.relations().get(queryWrapper.getCurrentProperty().getToken().snake_case()), subQuery);
//		return (Z)this;
//	}
//
//	@Override
//	public <X, SQ extends InlineQuery<X, SQ>> Z subQueryList(Function<T, List<X>> field, Consumer<SQ> subQuery) {
//		field.apply(instance);
//		this.subQuery(typeDescriber.relations().get(queryWrapper.getCurrentProperty().getToken().snake_case()), subQuery);
//		return (Z)this;
//	}
//
//	@Override
//	public <X, SQ extends InlineQuery<X, SQ>> Z subQueryList(BiConsumer<T, List<X>> field, Consumer<SQ> subQuery) {
//		field.accept(instance, null);
//		this.subQuery(typeDescriber.relations().get(queryWrapper.getCurrentProperty().getToken().snake_case()), subQuery);
//		return (Z)this;
//	}
//
//	public class FieldHandler implements MethodInterceptor {
//		private Property property;
//		public FieldHandler(Property property) {
//			this.property = property;
//		}
//
//		@Override
//		public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
//			property.setToken(Token.CamelCase(method.getName().replaceFirst("^(?:is|get|set)","")));
//			return null;
//		}
//	}
}
