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
