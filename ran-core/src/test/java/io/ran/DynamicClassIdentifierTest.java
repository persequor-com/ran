package io.ran;

import org.junit.Test;

import static org.junit.Assert.*;

public class DynamicClassIdentifierTest {

	@Test
	public void create_happy() {
		assertEquals("_good_idenfier$alsoCamelCase", DynamicClassIdentifier.create("_good_idenfier$alsoCamelCase").toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void create_illegalCharacter() {
		DynamicClassIdentifier.create("illegal-parts+");
	}

	@Test(expected = IllegalArgumentException.class)
	public void create_startsWithNr() {
		DynamicClassIdentifier.create("1_class");
	}
}
