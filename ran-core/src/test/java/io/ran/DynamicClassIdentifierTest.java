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

	@Test
	public void create_startsWithNr() {
		DynamicClassIdentifier.create("1_class");
	}

	@Test(expected = IllegalArgumentException.class)
	public void create_isKeyword() {
		DynamicClassIdentifier.create("private");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void create_hasSpace() {
		DynamicClassIdentifier.create("has space");
	}

	@Test(expected = IllegalArgumentException.class)
	public void create_isNullLiteral() {
		DynamicClassIdentifier.create("null");
	}

	@Test(expected = IllegalArgumentException.class)
	public void create_isNull() {
		DynamicClassIdentifier.create(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void create_isEmptyString() {
		DynamicClassIdentifier.create("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void create_isBooleanLiteral() {
		DynamicClassIdentifier.create("true");
	}
}
