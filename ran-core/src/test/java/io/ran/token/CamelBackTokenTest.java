/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>,
 */
package io.ran.token;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CamelBackTokenTest {
	@Test
	public void happyPath() {
		CamelCaseToken token = new CamelCaseToken("TheBlackEyedPeas");
		assertEquals(Token.of("the","black","eyed","peas"), token.toToken());
	}

	@Test
	public void withNumbers() {
		CamelCaseToken token = new CamelCaseToken("The3BlackEyed3es");
		assertEquals(Token.of("the","3","black","eyed3es"), token.toToken());

		token = new CamelCaseToken("With44BottlesOfRum");
		assertEquals(Token.of("with","44","bottles","of","rum"), token.toToken());
	}

	@Test(expected = InvalidTokenException.class)
	public void unHappy_startWithLower() {
		new CamelCaseToken("theBlackEyedPeas");
	}
}
