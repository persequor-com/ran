package io.ran;

import io.ran.testclasses.Brand;
import io.ran.testclasses.Car;
import io.ran.testclasses.Engine;
import io.ran.testclasses.IndexedCar;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class TestDoublePerfTest {

	public static final int NUMBER_OF_SUVS = 50;
	public static final int NUMBER_OF_CARS = 2000;
	private GuiceHelper helper;
	private TestCarRepo carRepo;
	List<String> carIds = new ArrayList<>();

	@Before
	public void setup() {
		helper = new GuiceHelper();

		carRepo = helper.injector.getInstance(TestCarRepo.class);


		for(int i = 0; i< NUMBER_OF_CARS; i++) {
			IndexedCar car = new IndexedCar();
			car.setBrand(Brand.Porsche);
			car.setConstructionDate(ZonedDateTime.now().minus(Duration.ofDays(2)));
			car.setCrashRating(2.0);
			car.setId(UUID.randomUUID().toString());
			car.setTitle("SUV "+((int)(Math.random()* NUMBER_OF_SUVS)));

			carRepo.save(car);
			carIds.add(car.getId());
		}

	}

	@Test
	public void perf() {
		long s = System.currentTimeMillis();
		for(int i =0;i<1000;i++) {
			Optional<IndexedCar> foundCar = carRepo.query().eq(IndexedCar::getId, carIds.get(((int) (Math.random() * NUMBER_OF_CARS)))).execute().findFirst();
			assertTrue(foundCar.isPresent());
		}
		System.out.println(System.currentTimeMillis()-s+"ms for pk");
	}


	@Test
	public void perfsdf() {
		long s = System.currentTimeMillis();
		for(int i =0;i<1000;i++) {
			List<IndexedCar> foundCar = carRepo.query().eq(IndexedCar::getTitle, "SUV "+((int)(Math.random()*NUMBER_OF_SUVS))).execute().collect(Collectors.toList());
			assertFalse(foundCar.isEmpty());
		}
		System.out.println(System.currentTimeMillis()-s+"ms");
	}

	public static class TestCarRepo extends CrudRepositoryTestDoubleBase<IndexedCar, String> {
		@Inject
		public TestCarRepo(GenericFactory genericFactory, MappingHelper mappingHelper, TestDoubleDb store) {
			super(genericFactory, IndexedCar.class, String.class, mappingHelper, store);
		}

		public TestQuery<IndexedCar> query() {
			return new TestQuery<IndexedCar>(modelType, genericFactory, mappingHelper, store);
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
