/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-03-22
 */
package io.ran;

import io.ran.testclasses.Bike;
import io.ran.testclasses.BikeGear;
import io.ran.testclasses.BikeWheel;
import io.ran.testclasses.Car;
import io.ran.testclasses.Door;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RelationDescriberTest {

	@Test
	public void inverse_simple() {
		RelationDescriber relationDescriber = TypeDescriberImpl.getTypeDescriber(Car.class).relations().get("doors");
		RelationDescriber inverse = relationDescriber.inverse();
		assertEquals(Car.class, inverse.getToClass().clazz);
		assertEquals(Door.class, inverse.getFromClass().clazz);
		assertEquals(relationDescriber.getFromKeys(), inverse.getToKeys());
		assertEquals(relationDescriber.getToKeys(), inverse.getFromKeys());
	}

	@Test
	public void inverse_compoundKey() {
		RelationDescriber relationDescriber = TypeDescriberImpl.getTypeDescriber(Bike.class).relations().get("front_wheel");
		RelationDescriber inverse = relationDescriber.inverse();
		assertEquals(Bike.class, inverse.getToClass().clazz);
		assertEquals(BikeWheel.class, inverse.getFromClass().clazz);
		assertEquals(relationDescriber.getFromKeys(), inverse.getToKeys());
		assertEquals(relationDescriber.getToKeys(), inverse.getFromKeys());
	}

	@Test
	public void inverse_via() {
		RelationDescriber relationDescriber = TypeDescriberImpl.getTypeDescriber(Bike.class).relations().get("gears");
		assertTrue(relationDescriber.getToKeys().isEmpty());
		assertTrue(relationDescriber.getFromKeys().isEmpty());
		RelationDescriber inverse = relationDescriber.inverse();
		assertEquals(Bike.class, inverse.getToClass().clazz);
		assertEquals(BikeGear.class, inverse.getFromClass().clazz);
		assertTrue(inverse.getToKeys().isEmpty());
		assertTrue(inverse.getFromKeys().isEmpty());
		assertTrue(relationDescriber.getVia().get(1).inverse().requiredFieldsEquals(inverse.getVia().get(0)));
		assertTrue(relationDescriber.getVia().get(0).inverse().requiredFieldsEquals(inverse.getVia().get(1)));

	}

}
