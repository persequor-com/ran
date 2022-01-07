package io.ran;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.TypeVariable;

public class ClazzMethodParameter {
	private final String name;
	private final Clazz clazz;
	private final Clazz genericClazz;

	public ClazzMethodParameter(Clazz<?> actualClass, Method method, Parameter p) {
		this.name = p.getName();
		if (p.getParameterizedType() instanceof TypeVariable) {
			Class<?> declaringClass = method.getDeclaringClass();
//			TypeVariable[] typeParameters = declaringClass.getTypeParameters();
			Clazz<?> genericSuper = actualClass.findGenericSuper(declaringClass);

			this.genericClazz = genericSuper.genericMap.get(p.getParameterizedType().getTypeName());
		} else {
			this.genericClazz = null;
		}
		this.clazz = Clazz.of(p.getType());
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

	public Clazz getGenericClazz() {
		return genericClazz;
	}

	public Clazz getBestEffortClazz() {
		return genericClazz != null ? genericClazz : clazz;
	}
}
