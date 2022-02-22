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
	}

	@Test
	public void notEquals_differentValue() {
		CompoundKey compoundKey = new CompoundKey();
		compoundKey.add(Property.get(Token.of("id"), Clazz.of(String.class)).value("my id2"));

		CompoundKey compoundKey2 = new CompoundKey();
		compoundKey2.add(Property.get(Token.of("id"), Clazz.of(String.class)).value("my id"));

		assertNotEquals(compoundKey, compoundKey2);
	}


	@Test
	public void notEquals_differentProperty() {
		CompoundKey compoundKey = new CompoundKey();
		compoundKey.add(Property.get(Token.of("id2"), Clazz.of(String.class)).value("my id"));

		CompoundKey compoundKey2 = new CompoundKey();
		compoundKey2.add(Property.get(Token.of("id"), Clazz.of(String.class)).value("my id"));

		assertNotEquals(compoundKey, compoundKey2);
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