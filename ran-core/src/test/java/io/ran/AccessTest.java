/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
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
