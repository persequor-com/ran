/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran;

import javax.inject.Named;

public class ClassWithMethods {
	public void mySimpleMethod() {

	}

	public String myReturningMethod() {
		return "Return value";
	}

	public int myPrimitiveReturningMethod() {
		return 5;
	}

	@Named("my annotation value")
	public void methodWithAnnotation() {

	}
}
