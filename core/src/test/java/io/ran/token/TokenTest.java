/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 
 */
package io.ran.token;

import org.junit.Test;

import static org.junit.Assert.*;

public class TokenTest {
	@Test
	public void happyPath_toString() {
		Token token = Token.of("my","parts");
		assertEquals("{my}{parts}", token.toString());
		assertEquals("MyParts", token.toString(CamelCaseToken.class));
		assertEquals("myParts", token.toString(CamelHumpToken.class));
		assertEquals("my_parts", token.toString(SnakeCaseToken.class));
	}

	@Test
	public void autoReadType() {
		Token camelHump = Token.get("myParts");
		Token camelCase = Token.get("MyParts");
		Token snakeCase = Token.get("my_parts");
		assertEquals(camelCase, camelHump);
		assertEquals(camelCase, snakeCase);
		assertEquals(camelHump, snakeCase);
	}

	@Test
	public void shortStringParts() {
		Token token = Token.of("m","y","p");
		assertEquals("{m}{y}{p}", token.toString());
		assertEquals("MYP", token.toString(CamelCaseToken.class));
		assertEquals("mYP", token.toString(CamelHumpToken.class));
		assertEquals("m_y_p", token.toString(SnakeCaseToken.class));
	}

	@Test(expected = InvalidTokenException.class)
	public void withEmptyPart() {
		Token.of("m","","p");
	}
}
