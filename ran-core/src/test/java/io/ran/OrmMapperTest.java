/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran;

import io.ran.token.Token;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrmMapperTest {
	private static AutoMapper mapper;

	@BeforeClass
	public static void setup() {

	}

	@Test
	public void simpleField() throws Throwable {
		TypeDescriber<TestClass> describer = TypeDescriberImpl.getTypeDescriber(TestClass.class);
		assertTrue(describer.fields().stream().filter(p -> "id".equals(p.getToken().snake_case())).findAny().isPresent());
	}

	@Test
	public void collectionField() throws Throwable {
		TypeDescriber<Other> describer = TypeDescriberImpl.getTypeDescriber(Other.class);
		assertTrue(describer.relations().stream().filter(p -> "test_classes".equals(p.getField().snake_case())).findAny().isPresent());
	}

	@Test
	public void getValueFromProperty() throws Throwable {
		TypeDescriber<TestClass> describer = TypeDescriberImpl.getTypeDescriber(TestClass.class);
		TestClass t = mapper.get(TestClass.class).newInstance();
		Mapping tMapping = (Mapping) t;
		t.setId("my id");

		Object actual = tMapping._getValue(describer.fields().get(Token.of("id")));

		assertEquals("my id", actual);
	}

	@Test
	public void keys() throws Throwable {
		TypeDescriber<TestClass> describer = TypeDescriberImpl.getTypeDescriber(TestClass.class);
		assertEquals(1, describer.fields().stream().filter(p -> "other_id".equals(p.getToken().snake_case())).findAny().get().getKeys().size());
	}

	@Test
	public void fieldAnnotations() throws Throwable {
		TypeDescriber<TestClass> describer = TypeDescriberImpl.getTypeDescriber(TestClass.class);
		assertEquals("muh", describer.fields().stream().filter(p -> "other_id".equals(p.getToken().snake_case())).findAny().get().getAnnotations().get(Key.class).name());
	}
}
