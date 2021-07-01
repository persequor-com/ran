/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 
 */
package io.ran;

import io.ran.testclasses.Brand;
import io.ran.testclasses.Car;
import io.ran.testclasses.Door;
import io.ran.testclasses.Engine;
import io.ran.testclasses.ObjectMap;
import io.ran.testclasses.Regular;
import io.ran.testclasses.WithCollections;
import io.ran.token.Token;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.Assert.*;

public class AutoMapperTest {
	private AutoMapper mapper;
	private GuiceHelper helper;

	@BeforeClass
	public static void beforeClass() throws IOException {

	}

	@Before
	public void setup() {
		helper = new GuiceHelper();
	}

	@Test
	public void hydrate() throws IllegalAccessException, InstantiationException {
		ObjectMap map = new ObjectMap();
		map.set("Id", "My id");
		ZonedDateTime now = ZonedDateTime.now();
		map.set("ConstructionDate", now);
		map.set(Token.CamelCase("Brand"), Brand.Hyundai);
		map.set(Token.CamelCase("CrashRating"), 4.4);
		map.set(Token.CamelCase("TheBoolean"), false);

		Car car = helper.factory.get(Car.class);
		Mapping carMapping = (Mapping)car;
		carMapping.hydrate(map);

		assertEquals("My id", car.getId());
		assertEquals(now, car.getConstructionDate());
		assertEquals(Double.valueOf(4.4), car.getCrashRating());
	}

	@Test
	public void columnize() {
		Car car = helper.factory.get(Car.class);
		ZonedDateTime now = ZonedDateTime.now();
		car.setId("My id");
		car.setConstructionDate(now);
		car.setBrand(Brand.Hyundai);
		car.setCrashRating(4.4);

		ObjectMap result = new ObjectMap();
		Mapping carMapping = (Mapping)car;
		carMapping.columnize(result);

		assertEquals("My id", result.getString("Id"));
		assertEquals(now, result.getZonedDateTime("ConstructionDate"));
		assertEquals(Brand.Hyundai, result.getEnum(Token.CamelCase("Brand"), Brand.class));
		assertEquals(Double.valueOf(4.4), result.getDouble(Token.CamelCase("CrashRating")));
	}

	@Test
	public void lazyLoad() {
		Car car = helper.factory.get(Car.class);

		ZonedDateTime now = ZonedDateTime.now();
		car.setId("My id");
		car.setEngineId(new UUID(0,0));
		car.setConstructionDate(now);

		Engine engine = car.getEngine();

		assertNotNull(engine);
	}

	@Test
	public void lazyLoadCollection() {
		Car car = helper.factory.get(Car.class);
		ZonedDateTime now = ZonedDateTime.now();
		car.setId("My id");
		car.setEngineId(new UUID(0,0));
		car.setConstructionDate(now);

		Collection<Door> doors = car.getDoors();
		assertEquals(2, doors.size());
	}

	@Test
	public void getSetCollections() {
		WithCollections w = helper.factory.get(WithCollections.class);
		w.setId(Arrays.asList("id1","id2"));
		w.setField(new HashSet<>(Arrays.asList("field1","field2")));

		Mapping mapping = (Mapping) w;
		ObjectMap map = new ObjectMap();
		mapping.columnize(map);

		WithCollections w2 = helper.factory.get(WithCollections.class);
		Mapping mapping2 = (Mapping) w2;

		mapping2.hydrate(map);

		assertEquals(2, w2.getId().size());
		assertEquals("id1", w2.getId().get(0));
		assertEquals("id2", w2.getId().get(1));
		assertEquals(2, w2.getField().size());
		assertTrue(w2.getField().contains("field1"));
		assertTrue(w2.getField().contains("field2"));
	}

	@Test
	public void getSetFieldsOnSuper() {
		Regular r = helper.factory.get(Regular.class);
		r.setReg("reg");
		r.setSup("sup");

		Mapping mapping = (Mapping) r;
		ObjectMap map = new ObjectMap();
		mapping.columnize(map);

		assertEquals("reg", map.getString(Token.of("reg")));
		assertEquals("sup", map.getString(Token.of("sup")));

		Regular r2 = helper.factory.get(Regular.class);
		Mapping mapping2 = (Mapping) r2;

		mapping2.hydrate(map);

		assertEquals("reg", r2.getReg());
		assertEquals("sup", r2.getSup());
	}
}
