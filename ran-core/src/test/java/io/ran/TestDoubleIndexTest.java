package io.ran;

import io.ran.testclasses.Brand;
import io.ran.testclasses.IndexedCar;
import io.ran.testclasses.IndexedEngine;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class TestDoubleIndexTest {
	private TestCarRepo carRepo;
	private final List<String> carIds = new ArrayList<>();
	private TestDoubleIndex carIndex;
	private TypeDescriber<IndexedCar> carDescriber = TypeDescriberImpl.getTypeDescriber(IndexedCar.class);
	private final TypeDescriber<IndexedEngine> engineDescriber = TypeDescriberImpl.getTypeDescriber(IndexedEngine.class);
	private TestDoubleIndex engineIndex;

	@Before
	public void setup() {
		GuiceHelper helper = new GuiceHelper();
		TestDoubleDb testDoubleDb = helper.injector.getInstance(TestDoubleDb.class);
		Store<Object, IndexedCar> carStore = Mockito.spy(testDoubleDb.getStore(IndexedCar.class));
		when(testDoubleDb.getStore(IndexedCar.class)).thenReturn(carStore);
		carIndex = Mockito.spy(carStore.index);
		when(carStore.getIndex()).thenReturn(carIndex);

		Store<Object, IndexedEngine> engineStore = Mockito.spy(testDoubleDb.getStore(IndexedEngine.class));
		when(testDoubleDb.getStore(IndexedEngine.class)).thenReturn(engineStore);
		engineIndex = Mockito.spy(engineStore.index);
		when(engineStore.getIndex()).thenReturn(engineIndex);

		carRepo = helper.injector.getInstance(TestCarRepo.class);
		TestEngineRepo engineRepo = helper.injector.getInstance(TestEngineRepo.class);


		IndexedEngine engine1 = new IndexedEngine();
		engine1.setId(UUID.randomUUID());
		engine1.setBrand(Brand.Hyundai);
		engineRepo.save(engine1);

		IndexedEngine engine2 = new IndexedEngine();
		engine2.setId(UUID.randomUUID());
		engine2.setBrand(Brand.Porsche);
		engineRepo.save(engine2);

		for (int i = 0; i < 5; i++) {
			IndexedCar car = new IndexedCar();
			car.setBrand(i % 2 == 1 ? Brand.Hyundai : Brand.Porsche);
			car.setConstructionDate(ZonedDateTime.now().minus(Duration.ofDays(2)));
			car.setCrashRating(2.0);
			car.setId(UUID.randomUUID().toString());
			car.setEngine(i % 2 == 0 ? engine1 : engine2);
			car.setTitle("SUV " + i);

			carRepo.save(car);
			carIds.add(car.getId());
		}

	}

	@Test
	public void useIndexOnPrimaryKeyLookup() {
		String id = carIds.get(((int) (Math.random() * 4)));
		Optional<IndexedCar> foundCar = carRepo.query().eq(IndexedCar::getId, id).execute().findFirst();
		assertTrue(foundCar.isPresent());
		carDescriber = TypeDescriberImpl.getTypeDescriber(IndexedCar.class);
		verify(carIndex).get(eq(carDescriber.getPropertyFromSnakeCase("id")), eq(id));
	}

	@Test
	public void useIndexOnNonExistingPrimaryKeyLookup() {
		String id = UUID.randomUUID().toString();
		Optional<IndexedCar> foundCar = carRepo.query().eq(IndexedCar::getId, id).execute().findFirst();
		assertFalse(foundCar.isPresent());
		verify(carIndex).get(eq(carDescriber.getPropertyFromSnakeCase("id")), eq(id));
	}

	@Test
	public void useIndexOnOtherKey() {
		Optional<IndexedCar> foundCar = carRepo.query().eq(IndexedCar::getTitle, "SUV 1").execute().findFirst();
		assertTrue(foundCar.isPresent());
		verify(carIndex).get(eq(carDescriber.getPropertyFromSnakeCase("title")), eq("SUV 1"));
	}

	@Test
	public void useIndexOnNonExistingOtherKey() {
		Optional<IndexedCar> foundCar = carRepo.query().eq(IndexedCar::getTitle, "SUV 151").execute().findFirst();
		assertFalse(foundCar.isPresent());
		verify(carIndex).get(eq(carDescriber.getPropertyFromSnakeCase("title")), eq("SUV 151"));
	}

	@Test
	public void useIndexOnJoins() {
		Optional<IndexedCar> foundCar = carRepo.query().subQuery(IndexedCar::getEngine, engineQuery ->
			engineQuery.eq(IndexedEngine::getBrand, Brand.Porsche)
		).execute().findFirst();
		assertTrue(foundCar.isPresent());
		verify(engineIndex, atLeastOnce()).get(eq(engineDescriber.getPropertyFromSnakeCase("brand")), eq(Brand.Porsche));
	}

	@Test
	public void useIndexOnLessThan() {
		Optional<IndexedCar> foundCar = carRepo.query().lt(IndexedCar::getCrashRating, 5.0).execute().findFirst();
		assertTrue(foundCar.isPresent());
		verify(carIndex).lt(eq(carDescriber.getPropertyFromSnakeCase("crash_rating")), eq(5.0));
	}

	@Test
	public void useIndexOnLessThanOrEqual() {
		Optional<IndexedCar> foundCar = carRepo.query().lte(IndexedCar::getCrashRating, 2.0).execute().findFirst();
		assertTrue(foundCar.isPresent());
		verify(carIndex).lte(eq(carDescriber.getPropertyFromSnakeCase("crash_rating")), eq(2.0));
	}

	@Test
	public void useIndexOnGreaterThan() {
		Optional<IndexedCar> foundCar = carRepo.query().gt(IndexedCar::getCrashRating, 1.0).execute().findFirst();
		assertTrue(foundCar.isPresent());
		verify(carIndex).gt(eq(carDescriber.getPropertyFromSnakeCase("crash_rating")), eq(1.0));
	}

	@Test
	public void useIndexOnGreaterThanOrEqual() {
		Optional<IndexedCar> foundCar = carRepo.query().gte(IndexedCar::getCrashRating, 2.0).execute().findFirst();
		assertTrue(foundCar.isPresent());
		verify(carIndex).gte(eq(carDescriber.getPropertyFromSnakeCase("crash_rating")), eq(2.0));
	}

	public static class TestCarRepo extends CrudRepositoryTestDoubleBase<IndexedCar, String> {
		@Inject
		public TestCarRepo(GenericFactory genericFactory, MappingHelper mappingHelper, TestDoubleDb store) {
			super(genericFactory, IndexedCar.class, String.class, mappingHelper, store);
		}

		public TestQuery<IndexedCar> query() {
			return new TestQuery<>(modelType, genericFactory, mappingHelper, store);
		}
	}

	public static class TestEngineRepo extends CrudRepositoryTestDoubleBase<IndexedEngine, UUID> {
		@Inject
		public TestEngineRepo(GenericFactory genericFactory, MappingHelper mappingHelper, TestDoubleDb store) {
			super(genericFactory, IndexedEngine.class, UUID.class, mappingHelper, store);
		}

		public TestQuery<IndexedEngine> query() {
			return new TestQuery<>(modelType, genericFactory, mappingHelper, store);
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
