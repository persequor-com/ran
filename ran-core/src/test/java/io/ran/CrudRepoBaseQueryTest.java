/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran;

import io.ran.testclasses.Brand;
import io.ran.testclasses.Car;
import io.ran.testclasses.Engine;
import io.ran.token.Token;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.function.Consumer;
import java.util.stream.Stream;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CrudRepoBaseQueryTest {
	private static AutoMapper ormMapper;
	private GuiceHelper helper;

	@BeforeClass
	public static void beforeClass() {

	}

	@Before
	public void setup() {
		helper = new GuiceHelper();
	}

	@Test
	public void awarmup() {
		long s = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			TestRepoQuery<Car> query = new TestRepoQuery<>(Car.class, helper.factory)
					.eq(Car::getBrand, Brand.Hyundai)
//					.subQuery(Car::getEngine, sq -> {
//						sq.lt(Engine::getId, UUID.randomUUID());
//					})
					.isNull(Car::getCrashRating);
		}

	}


	@Test
	public void performance() {
		long s = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			TestRepoQuery<Car> query = new TestRepoQuery<>(Car.class, helper.factory)
					.eq(Car::getBrand, Brand.Hyundai)
//					.subQuery(Car::getEngine, sq -> {
//						sq.lt(Engine::getId, UUID.randomUUID());
//					})
					.isNull(Car::getCrashRating);
		}
		System.out.println("time cglib: " + (System.currentTimeMillis() - s));
	}

	@Test
	public void performanceGenerated() {
		long s = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			TestRepoQuery<Car> query = new TestRepoQuery<>(Car.class, helper.factory)
					.eq(Car::getBrand, Brand.Hyundai)
//					.subQuery(Car::getEngine, sq -> {
//						sq.lt(Engine::getId, UUID.randomUUID());
//					})
					.isNull(Car::getCrashRating);
		}
		System.out.println("time generated: " + (System.currentTimeMillis() - s));
	}

	class TestRepoQuery<T> extends CrudRepoBaseQuery<T, TestRepoQuery<T>> {

		public TestRepoQuery(Class<T> clazz, GenericFactory genericFactory) {
			super(clazz, genericFactory);
		}

		@Override
		public TestRepoQuery<T> eq(Property.PropertyValue<?> property) {
			return null;
		}

		@Override
		public TestRepoQuery<T> gt(Property.PropertyValue<?> property) {
			return null;
		}

		@Override
		public TestRepoQuery<T> lt(Property.PropertyValue<?> property) {
			return null;
		}

		@Override
		public TestRepoQuery<T> gte(Property.PropertyValue<?> property) {
			return null;
		}

		@Override
		public TestRepoQuery<T> lte(Property.PropertyValue<?> property) {
			return null;
		}

		@Override
		public TestRepoQuery<T> isNull(Property<?> property) {
			return null;
		}

		@Override
		public TestRepoQuery<T> withEager(RelationDescriber relation) {
			return null;
		}

		@Override
		public <X extends Comparable<X>> TestRepoQuery<T> sortAscending(Property<X> property) {
			return null;
		}

		@Override
		public <X extends Comparable<X>> TestRepoQuery<T> sortDescending(Property<X> property) {
			return null;
		}

		@Override
		public TestRepoQuery<T> limit(int offset, int limit) {
			return null;
		}

		@Override
		public TestRepoQuery<T> limit(int limit) {
			return null;
		}

		@Override
		public <X, Z extends CrudRepository.InlineQuery<X, Z>> TestRepoQuery<T> subQuery(RelationDescriber relationDescriber, Consumer<Z> subQuery) {
			return null;
		}

		@Override
		public Stream<T> execute() {
			return null;
		}

		@Override
		public long count() {
			return 0;
		}

		@Override
		public CrudRepository.CrudUpdateResult delete() {
			return null;
		}
	}

	class TestRepoQueryCar extends CrudRepoBaseQuery<Car, TestRepoQueryCar> {

		public TestRepoQueryCar(TypeDescriber<Car> typeDescriber) {
			super(Car.class, helper.factory);
			instance = new CarQuery(currentProperty);
		}

		@Override
		public TestRepoQueryCar eq(Property.PropertyValue<?> property) {
			return null;
		}

		@Override
		public TestRepoQueryCar gt(Property.PropertyValue<?> property) {
			return null;
		}

		@Override
		public TestRepoQueryCar gte(Property.PropertyValue<?> property) {
			return null;
		}

		@Override
		public TestRepoQueryCar lt(Property.PropertyValue<?> property) {
			return null;
		}

		@Override
		public TestRepoQueryCar lte(Property.PropertyValue<?> property) {
			return null;
		}

		@Override
		public TestRepoQueryCar isNull(Property<?> property) {
			return null;
		}

		@Override
		public TestRepoQueryCar withEager(RelationDescriber relation) {
			return null;
		}

		@Override
		public <X extends Comparable<X>> TestRepoQueryCar sortAscending(Property<X> property) {
			return null;
		}

		@Override
		public <X extends Comparable<X>> TestRepoQueryCar sortDescending(Property<X> property) {
			return null;
		}

		@Override
		public TestRepoQueryCar limit(int offset, int limit) {
			return null;
		}

		@Override
		public TestRepoQueryCar limit(int limit) {
			return null;
		}

		@Override
		public <X, Z extends CrudRepository.InlineQuery<X, Z>> TestRepoQueryCar subQuery(RelationDescriber relationDescriber, Consumer<Z> subQuery) {
			return null;
		}

		@Override
		public Stream<Car> execute() {
			return null;
		}

		@Override
		public long count() {
			return 0;
		}

		@Override
		public CrudRepository.CrudUpdateResult delete() {
			return null;
		}

		class CarQuery extends Car {
			private Property property;

			CarQuery(Property property) {
				this.property = property;
			}

			@Override
			public Brand getBrand() {
				property.setToken(Token.of("brand"));
				return null;
			}

			@Override
			public Engine getEngine() {
				property.setToken(Token.of("engine"));
				return null;
			}

			@Override
			public Double getCrashRating() {
				property.setToken(Token.of("crash", "rating"));
				return null;
			}
		}
	}
}
