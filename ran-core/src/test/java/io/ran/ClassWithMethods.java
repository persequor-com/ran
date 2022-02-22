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
