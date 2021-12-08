package io.ran;

import java.lang.reflect.Parameter;

public class ClazzMethodParameter {
	private final String name;
	private final Clazz clazz;

	public ClazzMethodParameter(Parameter p) {
		this.name = p.getName();
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
}
