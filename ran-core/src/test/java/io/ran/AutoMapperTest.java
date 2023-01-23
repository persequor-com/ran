/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran;

import io.ran.testclasses.Bike;
import io.ran.testclasses.BikeGear;
import io.ran.testclasses.BikeType;
import io.ran.testclasses.BikeWheel;
import io.ran.testclasses.Brand;
import io.ran.testclasses.Car;
import io.ran.testclasses.Door;
import io.ran.testclasses.Engine;
import io.ran.testclasses.ObjectWithoutPrimaryKey;
import io.ran.testclasses.Regular;
import io.ran.testclasses.WithBinaryField;
import io.ran.testclasses.WithCollections;
import io.ran.token.Token;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AutoMapperTest {
	private AutoMapper mapper;
	private GuiceHelper helper;

	Resolver resolver = new Resolver() {
		@Override
		public <FROM, TO> TO get(Class<FROM> fromClass, String field, FROM obj) {
			return null;
		}

		@Override
		public <FROM, TO> Collection<TO> getCollection(Class<FROM> fromClass, String field, FROM obj) {
			return null;
		}
	};

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
		map.set(Property.get(Token.CamelCase("Brand")), Brand.Hyundai);
		map.set(Property.get(Token.CamelCase("CrashRating")), 4.4);
		map.set(Property.get(Token.CamelCase("TheBoolean")), false);

		Car car = helper.factory.get(Car.class);
		Mapping carMapping = (Mapping) car;
		carMapping.hydrate(map);

		assertEquals("My id", car.getId());
		assertEquals(now, car.getConstructionDate());
		assertEquals(Double.valueOf(4.4), car.getCrashRating());
		assertFalse(carMapping._isChanged());
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
		Mapping carMapping = (Mapping) car;
		carMapping.columnize(result);

		assertEquals("My id", result.getString("Id"));
		assertEquals(now, result.getZonedDateTime("ConstructionDate"));
		assertEquals(Brand.Hyundai, result.getEnum(Property.get(Token.CamelCase("Brand")), Brand.class));
		assertEquals(Double.valueOf(4.4), result.getDouble(Property.get(Token.CamelCase("CrashRating"))));
	}

	@Test
	public void lazyLoad() {
		Car car = helper.factory.get(Car.class);

		ZonedDateTime now = ZonedDateTime.now();
		car.setId("My id");
		car.setEngineId(new UUID(0, 0));
		car.setConstructionDate(now);

		Engine engine = car.getEngine();

		assertNotNull(engine);
	}

	@Test
	public void lazyLoadCollection() {
		Car car = helper.factory.get(Car.class);
		ZonedDateTime now = ZonedDateTime.now();
		car.setId("My id");
		car.setEngineId(new UUID(0, 0));
		car.setConstructionDate(now);

		Collection<Door> doors = car.getDoors();
		assertEquals(2, doors.size());
	}

	@Test
	public void getSetCollections() {
		WithCollections w = helper.factory.get(WithCollections.class);
		w.setId(Arrays.asList("id1", "id2"));
		w.setField(new HashSet<>(Arrays.asList("field1", "field2")));

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

		assertEquals("reg", map.getString(Property.get(Token.of("reg"))));
		assertEquals("sup", map.getString(Property.get(Token.of("sup"))));

		Regular r2 = helper.factory.get(Regular.class);
		Mapping mapping2 = (Mapping) r2;

		mapping2.hydrate(map);

		assertEquals("reg", r2.getReg());
		assertEquals("sup", r2.getSup());
	}

	@Test
	public void compoundKey() throws Throwable {
		Class<Bike> bikeClass = AutoMapper.get(Bike.class);
		Bike bike = bikeClass.newInstance();
		Mapping bikeMapping = (Mapping) bike;
		bike.setId("my id");
		CompoundKey bikeKey = bikeMapping._getKey();
		assertEquals(1, bikeKey.getValues().size());
		assertEquals("my id", bikeKey.getValue(Token.get("id")));

		Class<BikeWheel> bikeWheelClass = AutoMapper.get(BikeWheel.class);
		BikeWheel bikeWheel = bikeWheelClass.newInstance();
		Mapping bikeWheelMapping = (Mapping) bikeWheel;
		bikeWheel.setSize(20);
		bikeWheel.setBikeType(BikeType.Mountain);
		CompoundKey wheelKey = bikeWheelMapping._getKey();
		assertEquals(2, wheelKey.getValues().size());
		assertEquals(20, wheelKey.getValue(Token.get("size")));
		assertEquals(BikeType.Mountain, wheelKey.getValue(Token.get("bikeType")));

		Class<BikeGear> bikeGearClass = AutoMapper.get(BikeGear.class);
		BikeGear bikeGear = bikeGearClass.newInstance();
		Mapping bikeGearMapping = (Mapping) bikeGear;
		bikeGear.setGearNum(20);
		CompoundKey bikeGearKey = bikeGearMapping._getKey();
		assertEquals(1, bikeGearKey.getValues().size());
		assertEquals(20, bikeGearKey.getValue(Token.get("gearNum")));
	}

	@Test
	public void compoundKeyRelation_typeDescriber() throws Throwable {
		TypeDescriber<Bike> typeDescriber = TypeDescriberImpl.getTypeDescriber(Bike.class);
		RelationDescriber gearsRelation = typeDescriber.relations().get("gears");
		assertEquals(2, gearsRelation.getVia().size());
		assertEquals("id", gearsRelation.getVia().get(0).getFromKeys().get(0).getToken().snake_case());
		assertEquals("bike_id", gearsRelation.getVia().get(0).getToKeys().get(0).getToken().snake_case());
		assertEquals("gear_num", gearsRelation.getVia().get(1).getFromKeys().get(0).getToken().snake_case());
		assertEquals("gear_num", gearsRelation.getVia().get(1).getToKeys().get(0).getToken().snake_case());

		RelationDescriber wheelRelation = typeDescriber.relations().get("front_wheel");
		assertEquals(2, wheelRelation.getFromKeys().size());
		assertEquals("bike_type", wheelRelation.getFromKeys().get(0).getToken().snake_case());
		assertEquals("wheel_size", wheelRelation.getFromKeys().get(1).getToken().snake_case());
		assertEquals(2, wheelRelation.getToKeys().size());
		assertEquals("bike_type", wheelRelation.getToKeys().get(0).getToken().snake_case());
		assertEquals("size", wheelRelation.getToKeys().get(1).getToken().snake_case());
	}

	@Test
	public void objectWithoutPrimaryKey() throws Throwable {
		TypeDescriberImpl.getTypeDescriber(ObjectWithoutPrimaryKey.class);
	}


	@Test
	public void binaryData() throws IllegalAccessException, InstantiationException {
		ObjectMap map = new ObjectMap();

		WithBinaryField withBinaryField = helper.factory.get(WithBinaryField.class);
		withBinaryField.setUuid(UUID.randomUUID());
		withBinaryField.setBytes(UUID.randomUUID().toString().getBytes());
		Mapping mapping = (Mapping) withBinaryField;
		mapping.columnize(map);

		WithBinaryField withBinaryFieldHydrated = helper.factory.get(WithBinaryField.class);
		mapping.hydrate(withBinaryFieldHydrated, map);

		assertEquals(withBinaryField.getUuid(), withBinaryFieldHydrated.getUuid());
		assertEquals(withBinaryField.getBytes(), withBinaryFieldHydrated.getBytes());
	}

	@Test
	public void setRelationForObject() throws IllegalAccessException, InstantiationException {
		TypeDescriber<Car> describer = TypeDescriberImpl.getTypeDescriber(Car.class);
		Car car = helper.factory.get(Car.class);
		Mapping carMapping = (Mapping) car;

		carMapping._setRelation(describer.relations().get(0), null);

		assertNull(car.getEngine());
	}

	@Test
	public void setRelationForObject_manyToMany() throws Throwable {
		Bike bike = helper.factory.get(Bike.class);
		BikeGear gear = helper.factory.get(BikeGear.class);
		bike.setGears(Collections.singletonList(gear));
		gear.setBikes(Collections.singletonList(bike));

		Mapping bikeMapping = (Mapping) bike;
		bike.getClass().getMethod("_resolverInject", Resolver.class).invoke(bike, resolver);
		RelationDescriber gearsRelation = TypeDescriberImpl.getTypeDescriber(Bike.class).relations().get("gears");
		bikeMapping._setRelation(gearsRelation, null);
		bikeMapping._setRelationNotLoaded(gearsRelation);

		Mapping gearMapping = (Mapping) gear;
		gear.getClass().getMethod("_resolverInject", Resolver.class).invoke(gear, resolver);
		RelationDescriber gearRelation = TypeDescriberImpl.getTypeDescriber(BikeGear.class).relations().get("bikes");
		gearMapping._setRelation(gearRelation, null);
		gearMapping._setRelationNotLoaded(gearRelation);

		assertNull(bike.getGears());
		assertNull(gear.getBikes());
	}

	@Test
	public void settersSetsLoadedFlag() throws Throwable {
		Car car = helper.factory.get(Car.class);
		car.setId("car id");

		Resolver mockedResolver = mock(Resolver.class);
		HeadLights existingHeadlights = helper.factory.get(HeadLights.class);
		existingHeadlights.setOn("existing headlights on");
		when(mockedResolver.get(any(), anyString(), any())).thenAnswer(i -> existingHeadlights);
		car.getClass().getMethod("_resolverInject", Resolver.class).invoke(car, mockedResolver);

		HeadLights headLights = helper.factory.get(HeadLights.class);
		headLights.setOn("car id");
		car.setHeadLights(headLights);

		assertSame(headLights, car.getHeadLights());
	}

	@Test
	public void collectionSettersSetsLoadedFlag() throws Throwable {
		Car car = helper.factory.get(Car.class);
		car.setId("car id");

		Resolver mockedResolver = mock(Resolver.class);
		Door existingDoor = helper.factory.get(Door.class);
		existingDoor.setId("existing door id");
		when(mockedResolver.get(any(), anyString(), any())).thenAnswer(i -> Collections.singletonList(existingDoor));
		car.getClass().getMethod("_resolverInject", Resolver.class).invoke(car, mockedResolver);

		Door door = helper.factory.get(Door.class);
		door.setId("new car id");
		car.setDoors(Collections.singletonList(door));

		assertSame(door, car.getDoors().get(0));
	}
}
