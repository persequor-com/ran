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
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class PropertyListTest {

	@Test
	public void deduplicatePropertiesBySnakeCase_onAddProperty() {
		Property.PropertyList list = new Property.PropertyList();
		list.add(Property.get(Token.snake_case("the_column")));
		list.add(Property.get(Token.snake_case("the_column")));

		assertEquals(1, list.size());
	}

	@Test
	public void deduplicatePropertiesBySnakeCase_onAddToken() {
		Property.PropertyList list = new Property.PropertyList();
		list.add(Token.snake_case("the_column"), Clazz.of(String.class));
		list.add(Token.snake_case("the_column"), Clazz.of(String.class));

		assertEquals(1, list.size());
	}

	@Test
	public void deduplicatePropertiesBySnakeCase_onAddString() {
		Property.PropertyList list = new Property.PropertyList();
		list.add("the_column", Clazz.of(String.class));
		list.add("the_column", Clazz.of(String.class));

		assertEquals(1, list.size());
	}

	@Test(expected = RuntimeException.class)
	public void addByPositionIsUnsupported() {
		Property.PropertyList list = new Property.PropertyList();
		list.add(1, Property.get(Token.snake_case("the_column")));
	}

	@Test(expected = RuntimeException.class)
	public void addAllByPositionIsUnsupported() {
		Property.PropertyList list = new Property.PropertyList();
		list.addAll(1, Arrays.asList(Property.get(Token.snake_case("the_column"))));
	}

}
