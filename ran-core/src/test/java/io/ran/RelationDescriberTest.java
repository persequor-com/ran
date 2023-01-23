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
