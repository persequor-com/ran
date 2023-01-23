/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran;

import org.junit.Test;

import static org.junit.Assert.*;

public class AccessTest {
	private Access access;

	@Test
	public void happy_path() {
		access = Access.of(1);
		assertEquals(access, Access.Public);
		access = Access.of(2);
		assertEquals(access, Access.Private);
		access = Access.of(4);
		assertEquals(access, Access.Protected);
	}

	@Test
	public void synthetic_private() {
		// Test when it's a generated synthetic private access modifier
		access = Access.of(4098);
		assertEquals(access, Access.Private);
	}

	@Test
	public void is_method_synthetic() {
		assertTrue(Access.isSyntheticMethod(4098));
		assertFalse(Access.isSyntheticMethod(Access.Private.getOpCode()));
		assertFalse(Access.isSyntheticMethod(Access.Public.getOpCode()));

	}
}
