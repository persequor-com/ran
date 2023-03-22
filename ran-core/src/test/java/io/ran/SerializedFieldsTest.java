/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran;

import io.ran.testclasses.ObjectWithSerializedField;
import io.ran.testclasses.SerializableObject;
import io.ran.token.Token;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SerializedFieldsTest {
	private GuiceHelper helper;
	private MappingHelper mappingHelper;
	private ObjectWithSerializedField objectWithSerializedField;

	@Before
	public void setup() {
		helper = new GuiceHelper();
		mappingHelper = helper.injector.getInstance(MappingHelper.class);
		objectWithSerializedField = new ObjectWithSerializedField();
		objectWithSerializedField.setId("The id of this thing");
		objectWithSerializedField.setSerialized(new SerializableObject());
		objectWithSerializedField.getSerialized().setDown("down");
		objectWithSerializedField.getSerialized().setUp(33);
	}

	private Property property(String name) {
		return Property.get(Token.of(name));
	}

	@Test
	public void serializedField_happyPath() {
		ObjectMap objectMap = new ObjectMap();
		mappingHelper.columnize(objectWithSerializedField, objectMap);

		assertEquals("The id of this thing", objectMap.get(Token.get("id")));
		assertEquals("{\"up\":33,\"down\":\"down\"}", objectMap.getString(property("serialized")));
	}

	@Test
	public void deserializedField_forNonMappedObject() {
		ObjectMap objectMap = new ObjectMap();
		objectMap.set(property("id"), "the id yo");
		objectMap.set(property("serialized"), "{\"up\":33,\"down\":\"down\"}");
		ObjectWithSerializedField anotherObjectWithSerializedField = new ObjectWithSerializedField();
		mappingHelper.hydrate(anotherObjectWithSerializedField, objectMap);

		assertEquals("the id yo", anotherObjectWithSerializedField.getId());
		assertEquals(33, anotherObjectWithSerializedField.getSerialized().getUp());
		assertEquals("down", anotherObjectWithSerializedField.getSerialized().getDown());
	}

	@Test
	public void deserializedField_forMappedObject() {
		ObjectMap objectMap = new ObjectMap();
		objectMap.set(property("id"), "the id yo");
		objectMap.set(property("serialized"), "{\"up\":33,\"down\":\"down\"}");
		ObjectWithSerializedField anotherObjectWithSerializedField = helper.factory.get(ObjectWithSerializedField.class);
		mappingHelper.hydrate(anotherObjectWithSerializedField, objectMap);

		assertEquals("the id yo", anotherObjectWithSerializedField.getId());
		assertEquals(33, anotherObjectWithSerializedField.getSerialized().getUp());
		assertEquals("down", anotherObjectWithSerializedField.getSerialized().getDown());
	}
}
