/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran;

public class TestClassWrapper extends TestClass {
	public String toString() {
		return getId() + String.valueOf(getaShort()) + "-" + String.valueOf(getInteger()) + "-" + String.valueOf(getaLong());
	}

	public void setNumbers(int i, int i1, int i2) {
		setaShort((short) i);
		setInteger(i1);
		setaLong(i2);
	}
}
