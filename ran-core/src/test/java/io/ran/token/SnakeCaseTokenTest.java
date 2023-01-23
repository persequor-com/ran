/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
