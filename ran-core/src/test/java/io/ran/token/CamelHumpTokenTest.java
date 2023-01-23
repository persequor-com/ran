/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>,
 */
package io.ran.token;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CamelHumpTokenTest {
	@Test
	public void happyPath() {
		CamelHumpToken token = new CamelHumpToken("theBlackEyedPeas");
		assertEquals(Token.of("the", "black", "eyed", "peas"), token.toToken());

		token = new CamelHumpToken("id");
		assertEquals(Token.of("id"), token.toToken());

		token = new CamelHumpToken("idK");
		assertEquals(Token.of("id", "k"), token.toToken());
	}

	@Test
	public void withNumbers() {
		CamelHumpToken token = new CamelHumpToken("the3BlackEyed3es");
		assertEquals(Token.of("the", "3", "black", "eyed3es"), token.toToken());

		token = new CamelHumpToken("with44BottlesOfRum");
		assertEquals(Token.of("with", "44", "bottles", "of", "rum"), token.toToken());
	}

	@Test(expected = InvalidTokenException.class)
	public void unHappy_startWithCapital() {
		new CamelHumpToken("TheBlackEyedPeas");
	}
}
