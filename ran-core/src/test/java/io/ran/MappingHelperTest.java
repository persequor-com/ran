/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran;

import io.ran.testclasses.Brand;
import io.ran.testclasses.Car;
import io.ran.token.Token;
import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class MappingHelperTest {
	private GuiceHelper helper;
	private MappingHelper mappingHelper;

	@Before
	public void setup() {
		helper = new GuiceHelper();
		mappingHelper = new MappingHelper(helper.factory);
	}

	@Test
	public void happy_columnize() {
		Car car = helper.factory.get(Car.class);
		car.setId("my id");
		ObjectMap map = new ObjectMap();
		mappingHelper.columnize(car, map);

		assertEquals("my id", map.get(Token.of("id")));
	}

	@Test
	public void happy_columnizeCleanObject() {
		Car car = new Car();
		car.setId("my id");
		ObjectMap map = new ObjectMap();
		mappingHelper.columnize(car, map);

		assertEquals("my id", map.get(Token.of("id")));
	}

	@Test
	public void happy_hydrate() {
		Car car = helper.factory.get(Car.class);

		ObjectMap map = new ObjectMap();
		map.put(Token.of("id"), "my id");
		map.put(Token.of("the", "boolean"), true);
		mappingHelper.hydrate(car, map);

		assertEquals("my id", car.getId());
	}

	@Test
	public void happy_hydrateCleanObject() {
		Car car = new Car();

		ObjectMap map = new ObjectMap();
		map.put(Token.of("id"), "my id");
		map.put(Token.of("the", "boolean"), true);
		mappingHelper.hydrate(car, map);

		assertEquals("my id", car.getId());
	}

	@Test
	public void happy_copy() {
		Car car = new Car();
		car.setBrand(Brand.Hyundai);
		car.setCanBeSoldInEu(true);
		car.setConstructionDate(ZonedDateTime.now());
		car.setCrashRating(12.45);
		car.setEngineId(UUID.randomUUID());
		car.setTitle("The car");

		Car newCar = new Car();
		mappingHelper.copyValues(Car.class, car, newCar);

		assertEquals(car.getBrand(), newCar.getBrand());
		assertEquals(car.getCanBeSoldInEu(), newCar.getCanBeSoldInEu());
		assertEquals(car.getConstructionDate(), newCar.getConstructionDate());
		assertEquals(car.getCrashRating(), newCar.getCrashRating());
		assertEquals(car.getEngineId(), newCar.getEngineId());
		assertEquals(car.getTitle(), newCar.getTitle());
	}

	@Test
	public void getAndSetValue() {
		Car car = new Car();
		TypeDescriber<Car> typeDescriber = TypeDescriberImpl.getTypeDescriber(Car.class);
		mappingHelper.setValue(car, typeDescriber.getPropertyFromSnakeCase("id"), "my id");
		assertEquals("my id", mappingHelper.getValue(car, typeDescriber.getPropertyFromSnakeCase("id")));
		mappingHelper.setValue(car, typeDescriber.getPropertyFromSnakeCase("brand"), Brand.Porsche);
		assertEquals(Brand.Porsche, mappingHelper.getValue(car, typeDescriber.getPropertyFromSnakeCase("brand")));
		mappingHelper.setValue(car, typeDescriber.getPropertyFromSnakeCase("can_be_sold_in_eu"), true);
		assertEquals(true, mappingHelper.getValue(car, typeDescriber.getPropertyFromSnakeCase("can_be_sold_in_eu")));
		mappingHelper.setValue(car, typeDescriber.getPropertyFromSnakeCase("construction_date"), ZonedDateTime.parse("2022-01-01T12:15:45Z"));
		assertEquals(ZonedDateTime.parse("2022-01-01T12:15:45Z"), mappingHelper.getValue(car, typeDescriber.getPropertyFromSnakeCase("construction_date")));
		mappingHelper.setValue(car, typeDescriber.getPropertyFromSnakeCase("engine_id"), UUID.fromString("170b8575-9885-40dc-82a2-7ab49fcd6579"));
		assertEquals(UUID.fromString("170b8575-9885-40dc-82a2-7ab49fcd6579"), mappingHelper.getValue(car, typeDescriber.getPropertyFromSnakeCase("engine_id")));
		mappingHelper.setValue(car, typeDescriber.getPropertyFromSnakeCase("crash_rating"), 12.34);
		assertEquals(12.34, mappingHelper.getValue(car, typeDescriber.getPropertyFromSnakeCase("crash_rating")));
	}
}
