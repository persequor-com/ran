package io.ran;

public interface AutoWrapperGenericFactory {
	<T> T wrapped(Class<T> aClass);
}
