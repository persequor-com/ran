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

public class TokenTest {
	@Test
	public void happyPath_toString() {
		Token token = Token.of("my", "parts");
		assertEquals("{my}{parts}", token.toString());
		assertEquals("MyParts", token.CamelBack());
		assertEquals("myParts", token.camelHump());
		assertEquals("my_parts", token.snake_case());
		assertEquals("My parts", token.humanReadable());
	}

	@Test
	public void autoReadType() {
		Token camelHump = Token.get("myParts");
		Token camelCase = Token.get("MyParts");
		Token snakeCase = Token.get("my_parts");
		Token humanReadable = Token.get("My parts");
		assertEquals(camelCase, camelHump);
		assertEquals(camelCase, snakeCase);
		assertEquals(camelHump, snakeCase);
		assertEquals(humanReadable, snakeCase);
	}

	@Test
	public void shortStringParts() {
		Token token = Token.of("m", "y", "p");
		assertEquals("{m}{y}{p}", token.toString());
		assertEquals("MYP", token.toString(CamelCaseToken.class));
		assertEquals("mYP", token.toString(CamelHumpToken.class));
		assertEquals("m_y_p", token.toString(SnakeCaseToken.class));
	}

	@Test(expected = InvalidTokenException.class)
	public void withEmptyPart() {
		Token.of("m", "", "p");
	}

	@Test
	public void token_equals() {
		Token token = Token.of("m", "y", "p");
		Token other = Token.CamelCase("MYP");
		assertEquals(token, other);
	}
}
