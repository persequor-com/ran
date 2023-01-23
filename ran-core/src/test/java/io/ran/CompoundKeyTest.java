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

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class CompoundKeyTest {

	@Test
	public void equals() {
		CompoundKey compoundKey = new CompoundKey();
		compoundKey.add(Property.get(Token.of("id"), Clazz.of(String.class)).value("my id"));

		CompoundKey compoundKey2 = new CompoundKey();
		compoundKey2.add(Property.get(Token.of("id"), Clazz.of(String.class)).value("my id"));

		assertEquals(compoundKey, compoundKey2);
		assertEquals(compoundKey2, compoundKey);
	}

	@Test
	public void notEquals_differentValue() {
		CompoundKey compoundKey = new CompoundKey();
		compoundKey.add(Property.get(Token.of("id"), Clazz.of(String.class)).value("my id2"));

		CompoundKey compoundKey2 = new CompoundKey();
		compoundKey2.add(Property.get(Token.of("id"), Clazz.of(String.class)).value("my id"));

		assertNotEquals(compoundKey, compoundKey2);
		assertNotEquals(compoundKey2, compoundKey);
	}

	@Test
	public void notEquals_differentProperty() {
		CompoundKey compoundKey = new CompoundKey();
		compoundKey.add(Property.get(Token.of("id2"), Clazz.of(String.class)).value("my id"));

		CompoundKey compoundKey2 = new CompoundKey();
		compoundKey2.add(Property.get(Token.of("id"), Clazz.of(String.class)).value("my id"));

		assertNotEquals(compoundKey, compoundKey2);
		assertNotEquals(compoundKey2, compoundKey);
	}

	@Test
	public void notEquals_oneSameOneDifferent() {
		CompoundKey compoundKey = new CompoundKey();
		compoundKey.add(Property.get(Token.of("id"), Clazz.of(String.class)).value("my id"));
		compoundKey.add(Property.get(Token.of("id2"), Clazz.of(String.class)).value("my id"));

		CompoundKey compoundKey2 = new CompoundKey();
		compoundKey2.add(Property.get(Token.of("id"), Clazz.of(String.class)).value("my id"));
		compoundKey2.add(Property.get(Token.of("id3"), Clazz.of(String.class)).value("my id"));

		assertNotEquals(compoundKey, compoundKey2);
		assertNotEquals(compoundKey2, compoundKey);
	}

	@Test
	public void mapContains() {
		CompoundKey compoundKey = new CompoundKey();
		compoundKey.add(Property.get(Token.of("id"), Clazz.of(String.class)).value("my id"));

		CompoundKey compoundKey2 = new CompoundKey();
		compoundKey2.add(Property.get(Token.of("id"), Clazz.of(String.class)).value("my id"));

		Map<CompoundKey, Boolean> map = new HashMap<>();
		map.put(compoundKey, true);

		assertTrue(map.containsKey(compoundKey2));
	}

	@Test
	public void mapNotContains() {
		CompoundKey compoundKey = new CompoundKey();
		compoundKey.add(Property.get(Token.of("id"), Clazz.of(String.class)).value("my id"));

		CompoundKey compoundKey2 = new CompoundKey();
		compoundKey2.add(Property.get(Token.of("id"), Clazz.of(String.class)).value("my id2"));

		Map<CompoundKey, Boolean> map = new HashMap<>();
		map.put(compoundKey, true);

		assertFalse(map.containsKey(compoundKey2));
	}
}
