package io.ran;

import io.ran.testclasses.Brand;
import io.ran.testclasses.Car;
import io.ran.testclasses.Engine;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestDoubleTest {

	private GuiceHelper helper;
	private TestCarRepo carRepo;
	private TestEngineRepo engineRepo;

	private Car car1;
	private Car car2;
	private Engine engine1;
	private Engine engine2;

	@Before
	public void setup() {
		helper = new GuiceHelper();

		carRepo = helper.injector.getInstance(TestCarRepo.class);
		engineRepo = helper.injector.getInstance(TestEngineRepo.class);

		engine1 = new Engine();
		engine1.setId(UUID.randomUUID());
		engine1.setBrand(Brand.Hyundai);
		engineRepo.save(engine1);

		engine2 = new Engine();
		engine2.setId(UUID.randomUUID());
		engine2.setBrand(Brand.Porsche);
		engineRepo.save(engine2);

		car1 = new Car();
		car1.setBrand(Brand.Porsche);
		car1.setConstructionDate(ZonedDateTime.now().minus(Duration.ofDays(2)));
		car1.setCrashRating(2.0);
		car1.setId("My car");
		car1.setEngine(engine1);
		carRepo.save(car1);

		car2 = new Car();
		car2.setBrand(Brand.Hyundai);
		car2.setCrashRating(2.0);
		car2.setConstructionDate(ZonedDateTime.now().plus(Duration.ofDays(1)));
		car2.setId("My other car");
		car2.setEngine(engine2);
		carRepo.save(car2);
	}

	@Test
	public void simpleQuery() {
		Optional<Car> actual = carRepo.query().eq(Car::getId, "My other car").execute().findFirst();
		assertTrue(actual.isPresent());
		assertEquals("My other car", actual.get().getId());
	}

	@Test
	public void greaterThan() {
		List<Car> actual = carRepo.query().gt(Car::getConstructionDate, ZonedDateTime.now()).execute().collect(Collectors.toList());
		assertEquals(1, actual.size());
		assertEquals("My other car", actual.get(0).getId());
	}

	@Test
	public void lessThan() {
		List<Car> actual = carRepo.query().lt(Car::getConstructionDate, ZonedDateTime.now()).execute().collect(Collectors.toList());
		assertEquals(1, actual.size());
		assertEquals("My car", actual.get(0).getId());
	}

	@Test
	public void limitAndSortAscending() {
		List<Car> actual = carRepo.query().sortAscending(Car::getConstructionDate).limit(1).execute().collect(Collectors.toList());
		assertEquals(1, actual.size());
		assertEquals("My car", actual.get(0).getId());
	}

	@Test
	public void limitAndSortDescending() {
		List<Car> actual = carRepo.query().sortDescending(Car::getConstructionDate).limit(1).execute().collect(Collectors.toList());
		assertEquals(1, actual.size());
		assertEquals("My other car", actual.get(0).getId());
	}

	@Test
	public void limitAndMultipleSorts_firstSortEquals() {
		List<Car> actual = carRepo.query().sortAscending(Car::getCrashRating).sortAscending(Car::getConstructionDate).limit(1).execute().collect(Collectors.toList());
		assertEquals(1, actual.size());
		assertEquals("My car", actual.get(0).getId());
	}

	@Test
	public void limitAndMultipleSorts_firstSortDiffers() {
		car2.setCrashRating(1.0);
		List<Car> actual = carRepo.query().sortAscending(Car::getCrashRating).sortAscending(Car::getConstructionDate).limit(1).execute().collect(Collectors.toList());
		assertEquals(1, actual.size());
		assertEquals("My other car", actual.get(0).getId());
	}

	@Test
	public void subQuery() {
		Optional<Car> actual = carRepo.query().subQuery(Car::getEngine, sq -> {
			sq.eq(Engine::getBrand, Brand.Hyundai);
		}).execute().findFirst();
		assertTrue(actual.isPresent());
		assertEquals("My car", actual.get().getId());
	}

	@Test
	public void isNull() {
		car2.setBrand(null);
		Optional<Car> actual = carRepo.query().isNull(Car::getBrand).execute().findFirst();
		assertTrue(actual.isPresent());
		assertEquals("My other car", actual.get().getId());
	}

	@Test
	public void count() {
		long actual = carRepo.query().count();
		assertEquals(2, actual);
	}

	@Test
	public void delete() {
		carRepo.query().eq(Car::getId, "My other car").delete();
		List<Car> actual = carRepo.query().execute().collect(Collectors.toList());
		assertEquals(1, actual.size());
	}

	@Test
	public void deleteBySubquery() {
		carRepo.query().subQuery(Car::getEngine, sq -> {
			sq.eq(Engine::getBrand, Brand.Hyundai);
		}).delete();
		List<Car> actual = carRepo.query().execute().collect(Collectors.toList());
		assertEquals(1, actual.size());
	}



	public static class TestCarRepo extends CrudRepositoryTestDoubleBase<Car, String> {
		@Inject
		public TestCarRepo(GenericFactory genericFactory, MappingHelper mappingHelper, TestDoubleDb store) {
			super(genericFactory, Car.class, String.class, mappingHelper, store);
		}

		public TestQuery<Car> query() {
			return new TestQuery<Car>(modelType, genericFactory, mappingHelper, store);
		}
	}

	public static class TestEngineRepo extends CrudRepositoryTestDoubleBase<Engine, UUID> {
		@Inject
		public TestEngineRepo(GenericFactory genericFactory, MappingHelper mappingHelper, TestDoubleDb store) {
			super(genericFactory, Engine.class, UUID.class, mappingHelper, store);
		}

		public TestQuery<Engine> query() {
			return new TestQuery<Engine>(modelType, genericFactory, mappingHelper, store);
		}
	}

	public static class TestQuery<T> extends TestDoubleQuery<T, TestQuery<T>> {

		public TestQuery(Class<T> modelType, GenericFactory genericFactory, MappingHelper mappingHelper, TestDoubleDb testDoubleDb) {
			super(modelType, genericFactory, mappingHelper, testDoubleDb);
		}

		@Override
		protected TestQuery<T> getQuery(Class<?> queryClass) {
			return new TestQuery(queryClass, genericFactory, mappingHelper, testDoubleDb);
		}

		public <X> TestQuery<T> subQuery(Function<T, X> field, Consumer<TestQuery<X>> subQuery) {
			field.apply(instance);
			this.subQuery(typeDescriber.relations().get(queryWrapper.getCurrentProperty().getToken().snake_case()), subQuery);
			return this;
		}

		public <X> TestQuery<T> subQuery(BiConsumer<T, X> field, Consumer<TestQuery<X>> subQuery) {
			field.accept(instance, null);
			this.subQuery(typeDescriber.relations().get(queryWrapper.getCurrentProperty().getToken().snake_case()), subQuery);
			return this;
		}

		public <X> TestQuery<T> subQueryList(Function<T, List<X>> field, Consumer<TestQuery<X>> subQuery) {
			field.apply(instance);
			this.subQuery(typeDescriber.relations().get(queryWrapper.getCurrentProperty().getToken().snake_case()), subQuery);
			return this;
		}

		public <X> TestQuery<T> subQueryList(BiConsumer<T, List<X>> field, Consumer<TestQuery<X>> subQuery) {
			field.accept(instance, null);
			this.subQuery(typeDescriber.relations().get(queryWrapper.getCurrentProperty().getToken().snake_case()), subQuery);
			return this;
		}
	}
}
