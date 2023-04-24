/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran;

import org.objectweb.asm.ClassWriter;

public class AutoMapperClassWriter extends ClassWriter {
	protected String name;
	protected String shortName;
	protected Class<?> wrapperClass;
	protected Clazz<?> wrapperClazz;
	protected String postFix;

	public AutoMapperClassWriter(Class wrapperClass) {
		super(COMPUTE_FRAMES);
		this.wrapperClass = wrapperClass;
		this.wrapperClazz = Clazz.of(wrapperClass);
	}

	public AutoMapperClassWriter() {
		super(COMPUTE_FRAMES);
	}

	protected Clazz getSelf() {
		return Clazz.of(wrapperClazz.getInternalName() + postFix);
	}

	public MethodWriter method(Access access, MethodSignature signature) {
		return new MethodWriter(getSelf(), wrapperClazz, visitMethod(access.getOpCode(),
				signature.getName(),
				signature.getMethodDescriptor(),
				signature.getMethodSignature(),
				signature.getExceptions()
		), signature.getParameterCount());
	}

	public void addAnnotation(Clazz annotation, boolean visibleAtRuntime) {
		visitAnnotation(annotation.getDescriptor(), visibleAtRuntime);
	}

	public void field(Access access, String name, Clazz type, Object value) {
		visitField(access.getOpCode(), name, type.getDescriptor(), type.generics.isEmpty() ? null : type.getSignature(), value);
	}

	public String getName() {
		return name;
	}

	public String getShortName() {
		return shortName;
	}
}
