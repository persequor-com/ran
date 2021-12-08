package io.ran;

import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClazzMethod {
	private final List<Annotation> annotations;
	private final String name;
	private final List<ClazzMethodParameter> parameters;
	private final Method method;
	private final String methodToken;

	public ClazzMethod(Method method) {
		this.method = method;
		this.name = method.getName();
		this.methodToken = method.toString();
		this.annotations = Arrays.asList(method.getAnnotations());
		parameters = Stream.of(method.getParameters()).map(ClazzMethodParameter::new).collect(Collectors.toList());
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
}
