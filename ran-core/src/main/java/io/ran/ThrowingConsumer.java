package io.ran;

public interface ThrowingConsumer<T, E extends Throwable> {
	void accept(T t) throws E;
}
