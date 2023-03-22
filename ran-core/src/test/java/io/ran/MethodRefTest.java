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
