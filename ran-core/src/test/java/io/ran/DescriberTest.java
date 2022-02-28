package io.ran;

import io.ran.testclasses.Brand;
import io.ran.testclasses.Car;
import io.ran.testclasses.Door;
import io.ran.testclasses.Engine;
import io.ran.token.Token;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.*;

public class DescriberTest {
	private GenericFactory factory;
	private GuiceHelper helper;

	@BeforeClass
	public static void beforeClass() {

	}

	@Before
	public void setup() {
		helper = new GuiceHelper();
		factory = helper.factory;
	}

	@Test
	public void car() throws Throwable {
		TypeDescriber<Car> describer = TypeDescriberImpl.getTypeDescriber(Car.class);
		assertEquals(7, describer.fields().size());
		assertEquals(1, describer.primaryKeys().size());
		assertEquals("id", describer.primaryKeys().get(0).getToken().snake_case());
		assertEquals(String.class, describer.primaryKeys().get(0).getType().clazz);
		assertEquals(4, describer.relations().size());
		assertEquals("id", describer.relations().get(Door.class).get().getFromKeys().get(0).getToken().camelHump());
		assertEquals(String.class, describer.relations().get(Door.class).get().getFromKeys().get(0).getType().clazz);
		assertEquals(Car.class, describer.relations().get(Door.class).get().getFromKeys().get(0).getOn().clazz);

		assertEquals("carId", describer.relations().get(Door.class).get().getToKeys().get(0).getToken().camelHump());
		assertEquals(String.class, describer.relations().get(Door.class).get().getToKeys().get(0).getType().clazz);
		assertEquals(Door.class, describer.relations().get(Door.class).get().getToKeys().get(0).getOn().clazz);

		assertEquals(RelationType.OneToMany, describer.relations().get(Door.class).get().getType());


		assertEquals("engineId", describer.relations().get(Engine.class).get().getFromKeys().get(0).getToken().camelHump());
		assertEquals(UUID.class, describer.relations().get(Engine.class).get().getFromKeys().get(0).getType().clazz);
		assertEquals("id", describer.relations().get(Engine.class).get().getToKeys().get(0).getToken().camelHump());
		assertEquals(UUID.class, describer.relations().get(Engine.class).get().getToKeys().get(0).getType().clazz);
		assertEquals(RelationType.OneToOne, describer.relations().get(Engine.class).get().getType());

		assertEquals("id", describer.relations().get(HeadLights.class).get().getFromKeys().get(0).getToken().camelHump());
		assertEquals(String.class, describer.relations().get(HeadLights.class).get().getFromKeys().get(0).getType().clazz);
		assertEquals("on", describer.relations().get(HeadLights.class).get().getToKeys().get(0).getToken().camelHump());
		assertEquals(String.class, describer.relations().get(HeadLights.class).get().getToKeys().get(0).getType().clazz);
		assertEquals(RelationType.OneToOne, describer.relations().get(HeadLights.class).get().getType());

	}

	@Test
	public void door() throws Throwable {
		TypeDescriber<Door> describer = TypeDescriberImpl.getTypeDescriber(Door.class);

		assertEquals("carId", describer.relations().get(Car.class).get().getFromKeys().get(0).getToken().camelHump());
		assertEquals(String.class, describer.relations().get(Car.class).get().getFromKeys().get(0).getType().clazz);
		assertEquals("id", describer.relations().get(Car.class).get().getToKeys().get(0).getToken().camelHump());
		assertEquals(String.class, describer.relations().get(Car.class).get().getToKeys().get(0).getType().clazz);
	}


	@Test
	public void engine() throws Throwable {
		TypeDescriber<Engine> describer = TypeDescriberImpl.getTypeDescriber(Engine.class);

		assertEquals("id", describer.relations().get(Car.class).get().getFromKeys().get(0).getToken().camelHump());
		assertEquals(UUID.class, describer.relations().get(Car.class).get().getFromKeys().get(0).getType().clazz);
		assertEquals("engineId", describer.relations().get(Car.class).get().getToKeys().get(0).getToken().camelHump());
		assertEquals(UUID.class, describer.relations().get(Car.class).get().getToKeys().get(0).getType().clazz);
	}

	@Test
	public void getValue() throws Throwable {
		TypeDescriber<Engine> describer = TypeDescriberImpl.getTypeDescriber(Engine.class);

		Engine engine = factory.get(Engine.class);
		Mapping engineMapping = (Mapping)engine;
		engine.setId(UUID.randomUUID());

		Object actual = engineMapping._getValue(describer.fields().get(Token.of("id")));
		assertEquals(engine.getId(), actual);
	}

	@Test
	public void getKey() throws Throwable {
		Engine engine = factory.get(Engine.class);
		engine.setId(UUID.randomUUID());

		CompoundKey actual = ((Mapping) engine)._getKey();
		assertEquals(1, actual.getValues().size());
		assertEquals(engine.getId(), actual.getValue(Token.of("id")));
	}

	@Test
	public void setRelation() throws Throwable {
		TypeDescriber<Car> describer = TypeDescriberImpl.getTypeDescriber(Car.class);

		Engine engine = factory.get(Engine.class);
		Car car = factory.get(Car.class);
		Mapping carMapping = (Mapping)car;
		carMapping._setRelation(describer.relations().get(0), engine);

		assertSame(engine, car.getEngine());
	}

	@Test
	public void setCollectionRelation() throws Throwable {
		TypeDescriber<Engine> describer = TypeDescriberImpl.getTypeDescriber(Engine.class);

		Engine engine = factory.get(Engine.class);
		Mapping engineMapping = (Mapping)engine;
		Car car = new Car();
		engineMapping._setRelation(describer.relations().get(0), Arrays.asList(car));

		assertSame(car, engine.getCars().stream().findFirst().get());
	}

	@Test
	public void getRelation() throws Throwable {
		TypeDescriber<Car> describer = TypeDescriberImpl.getTypeDescriber(Car.class);
		Car car = factory.get(Car.class);
		Mapping carMapping = (Mapping)car;

		Object relation = carMapping._getRelation(describer.relations().get(0));
		assertNull(relation);

		Engine engine = new Engine();
		engine.setId(UUID.randomUUID());
		car.setEngine(engine);
		relation = carMapping._getRelation(describer.relations().get(0));
		assertSame(engine, relation);
	}

	@Test
	public void isChanged() throws Throwable {
		TypeDescriber<Car> describer = TypeDescriberImpl.getTypeDescriber(Car.class);
		Car car = factory.get(Car.class);
		Mapping carMapping = (Mapping)car;

		assertFalse(carMapping._isChanged());

		car.setBrand(Brand.Porsche);

		assertTrue(carMapping._isChanged());
	}
}
