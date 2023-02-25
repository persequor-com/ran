/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClazzMethod {
	private final List<Annotation> annotations;
	private final String name;
	private final List<ClazzMethodParameter> parameters;
	private final Clazz<?> actualClass;
	private final Method method;
	private final String methodToken;

	public ClazzMethod(Clazz<?> actualClass, Method method) {
		this.actualClass = actualClass;
		this.method = method;
		this.name = method.getName();
		this.methodToken = method.toString();
		this.annotations = Arrays.asList(method.getAnnotations());
		parameters = Stream.of(method.getParameters()).map(p -> new ClazzMethodParameter(actualClass, method, p)).collect(Collectors.toList());
	}

	public String getName() {
		return name;
	}

	public List<ClazzMethodParameter> parameters() {
		return this.parameters;
	}

	public <T extends Annotation> T getAnnotation(Class<T> annotation) {
		return method.getAnnotation(annotation);
	}

	public boolean matches(String token) {
		return this.methodToken.equals(token);
	}

	public int getModifiers() {
		return method.getModifiers();
	}

	public Method getMethod() {
		return method;
	}

	public boolean isAbstract() {
		return Modifier.isAbstract(method.getModifiers());
	}

	public boolean isFinal() {
		return Modifier.isFinal(method.getModifiers());
	}

	public boolean isPrivate() {
		return Modifier.isPrivate(method.getModifiers());
	}

	public boolean isProtected() {
		return Modifier.isProtected(method.getModifiers());
	}

	public boolean isPublic() {
		return Modifier.isPublic(method.getModifiers());
	}

	public boolean isStatic() {
		return Modifier.isStatic(method.getModifiers());
	}

	public boolean isSynchronized() {
		return Modifier.isSynchronized(method.getModifiers());
	}

	private List<Type> getAllGenericTypes() {
		ArrayList<Type> types = new ArrayList<>();
		types.add(method.getGenericReturnType());
		types.addAll(Arrays.asList(method.getGenericParameterTypes()));
		types.addAll(Arrays.asList(method.getGenericExceptionTypes()));
		return types.stream().flatMap(type -> {
			if(type instanceof ParameterizedType) {
				return Stream.of(((ParameterizedType)type).getActualTypeArguments());
			} else {
				return Stream.of(type);
			}
		}).collect(Collectors.toList());
	}

	/**
	 * @return true when the method either returns or takes generic parameters defined at class-level
	 */
	public boolean hasGenericFromClass() {
		List<Type> types = getAllGenericTypes();
		for(Type type : types) {
			if(type instanceof TypeVariable<?>) {
				TypeVariable<?> typed = (TypeVariable<?>) type;
				if(typed.getGenericDeclaration() == method.getDeclaringClass())
					return true;
			}
		}
		return false;
	}

	/**
	 * @return true if the method definition has generic parameters at method-level.
	 */
	public boolean hasGenericFromMethod() {
		List<Type> types = getAllGenericTypes();
		for(Type type : types) {
			if(type instanceof TypeVariable<?>) {
				TypeVariable<?> typed = (TypeVariable<?>) type;
				if(typed.getGenericDeclaration() == method)
					return true;
			} else if(type instanceof WildcardType) {
				//WildcardType typed = (WildcardType) type;
				//if(typed.getTypeName().equals("?"))
				return true;
			}
		}
		return false;
	}

	public Clazz<?> getReturnType() {
		Class<?> declaringClass = method.getDeclaringClass();
		Clazz<?> genericSuper = actualClass.findGenericSuper(declaringClass);
		return Clazz.of(method.getGenericReturnType(), genericSuper.genericMap);
	}

	public Clazz<?> getDeclaringClazz() {
		return Clazz.of(method.getDeclaringClass());
	}

	public MethodSignature getSignature() {
		return new MethodSignature(method);
	}

	public Access getAccess() {
		return Access.of(method.getModifiers());
	}


}
