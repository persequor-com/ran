/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MethodSignature {
	private Clazz owner;
	private String name;
	private Clazz returnType;
	private Clazzes parameters = new Clazzes();
	private Clazzes exceptions = new Clazzes();
	private boolean isStatic = false;

	public MethodSignature(Clazz owner, String name, Clazz returnType, Clazz... parameters) {
		this(owner, name, returnType, Arrays.asList(parameters));
	}

	public MethodSignature(Clazz owner, String name, Clazz returnType, List<Clazz> parameters) {
		this.owner = owner;
		this.name = name;
		this.returnType = returnType;
		this.parameters.addAll(parameters);
	}

	public MethodSignature(Method method) {
		this(Clazz.of(method.getDeclaringClass()), method.getName(), Clazz.of(method.getReturnType()), Arrays.asList(method.getParameterTypes()).stream().map(Clazz::of).collect(Collectors.toList()));
		if (Modifier.isStatic(method.getModifiers())) {
			isStatic = true;
		}
	}

	public MethodSignature(Constructor method) {
		this(Clazz.of(method.getDeclaringClass()), "<init>", Clazz.getVoid(), Arrays.asList(method.getGenericParameterTypes()).stream().map(Clazz::of).collect(Collectors.toList()));
	}

	public MethodSignature Exceptions(Clazz... exceptions) {
		this.exceptions.addAll(Arrays.asList(exceptions));
		return this;
	}

	public MethodSignature setName(String name) {
		this.name = name;
		return this;
	}

	public String getName() {
		return name;
	}

	public Clazz getReturnType() {
		return returnType;
	}

	public String getMethodDescriptor() {
		return "(" + parameters.getDescriptor() + ")" + returnType.getDescriptor();
	}

	public String getMethodSignature() {
		return "(" + parameters.getSignature() + ")" + returnType.getSignature();
	}

	public String[] getExceptions() {
		return exceptions.toDescriptorArray();
	}

	public boolean isConstructor() {
		return getName().equals("<init>");
	}

	public boolean isStatic() {
		return isStatic;
	}

	public Clazz getOwner() {
		return owner;
	}

	public int getParameterCount() {
		return parameters.size();
	}

	public MethodSignature setOwner(Clazz newOwner) {
		this.owner = newOwner;
		return this;
	}

}
