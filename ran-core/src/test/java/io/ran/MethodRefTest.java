/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2022-02-22
 */
package io.ran;

import org.junit.Test;

import javax.inject.Named;

import static org.junit.Assert.assertEquals;

public class MethodRefTest {
	GuiceHelper helper = new GuiceHelper();
	MappingHelper mappingHelper = new MappingHelper(helper.factory);

	@Test
	public void simpleMethod() {
		ClazzMethod actual = mappingHelper.getMethod(ClassWithMethods.class, ClassWithMethods::mySimpleMethod);

		assertEquals("mySimpleMethod", actual.getName());
		assertEquals(0, actual.parameters().size());
	}

	@Test
	public void returningMethod() {
		ClazzMethod actual = mappingHelper.getMethod(ClassWithMethods.class, ClassWithMethods::myReturningMethod);

		assertEquals("myReturningMethod", actual.getName());
		assertEquals(0, actual.parameters().size());
	}

	@Test
	public void primitiveReturningMethod() {
		ClazzMethod actual = mappingHelper.getMethod(ClassWithMethods.class, ClassWithMethods::myPrimitiveReturningMethod);

		assertEquals("myPrimitiveReturningMethod", actual.getName());
		assertEquals(0, actual.parameters().size());
	}

	@Test
	public void annotatedMethod() {
		ClazzMethod actual = mappingHelper.getMethod(ClassWithMethods.class, ClassWithMethods::methodWithAnnotation);

		assertEquals("methodWithAnnotation", actual.getName());
		assertEquals(0, actual.parameters().size());
		assertEquals("my annotation value", actual.getAnnotation(Named.class).value());
	}

}
