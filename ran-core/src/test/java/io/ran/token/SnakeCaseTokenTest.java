/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>,
 */
package io.ran.token;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SnakeCaseTokenTest {

	@Test
	public void happyPath() {
		SnakeCaseToken token = new SnakeCaseToken("i_slither_like_a");
		assertEquals(Token.of("i", "slither", "like", "a"), token.toToken());
	}

	@Test(expected = InvalidTokenException.class)
	public void capitalLetters_snakeNotLike() {
		new SnakeCaseToken("i_slIther_lIke_A");
	}

	@Test
	public void numbers_snakeDontCare() {
		SnakeCaseToken token = new SnakeCaseToken("1_sl1th3r_l1k3_4");
		assertEquals(Token.of("1", "sl1th3r", "l1k3", "4"), token.toToken());
	}
}
