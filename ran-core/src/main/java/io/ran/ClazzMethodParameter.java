/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;

public class ClazzMethodParameter {
	private final String name;
	private final Clazz clazz;

	public ClazzMethodParameter(Clazz<?> actualClass, Method method, Parameter p) {
		this.name = p.getName();
		Class<?> declaringClass = method.getDeclaringClass();
		Clazz<?> genericSuper = actualClass.findGenericSuper(declaringClass);
		clazz = Clazz.of(p.getParameterizedType(), genericSuper != null ? genericSuper.genericMap : Collections.emptyMap());
	}

	public String getName() {
		return name;
	}

	public Clazz getClazz() {
		return clazz;
	}

	public boolean matches(Class arg) {
		return arg.equals(clazz.clazz);
	}
}
